package com.example.kouch.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kouch.ChatActivity;
import com.example.kouch.Model.User;
import com.example.kouch.R;
import com.example.kouch.Model.ChatRoomModel;
import com.example.kouch.utils.AndroidUtil;
import com.example.kouch.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class RecentRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;

    public RecentRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {

        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSendByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        User otherUser = task.getResult().toObject(User.class);
                        FirebaseUtil.GetOtherProfilePicStorageRef(otherUser.getId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if (t.isSuccessful()) {
                                        Uri uri = t.getResult();
                                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                                    }
                                });
                        holder.fio.setText(otherUser.getFName());

                        String lastMessage = model.getLastMessage_user();
                        if (lastMessage.length() > 50) {
                            lastMessage = lastMessage.substring(0, 50) + "...";
                        }
                        holder.lastMessageText.setText(lastMessage);

                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessage()));

                        // Set up a listener for the user's status
                        ListenerRegistration statusListener = FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(otherUser.getId())
                                .addSnapshotListener((snapshot, e) -> {
                                    if (e != null) {
                                        Log.w("StatusUpdate", "Listen failed.", e);
                                        return;
                                    }

                                    if (snapshot != null && snapshot.exists()) {
                                        String status = snapshot.getString("status");
                                        if ("online".equals(status)) {
                                            holder.statusIndicator.setVisibility(View.VISIBLE);
                                        } else {
                                            holder.statusIndicator.setVisibility(View.INVISIBLE);
                                        }
                                    } else {
                                        Log.d("StatusUpdate", "Current data: null");
                                    }
                                });

                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserAsIntent(intent, otherUser);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                        // Store the listener registration so it can be removed if necessary
                        holder.setListenerRegistration(statusListener);
                    }
                });
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView fio;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;
        ImageView statusIndicator;
        private ListenerRegistration listenerRegistration;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_messag_time);
            fio = itemView.findViewById(R.id.user_name_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }

        public void setListenerRegistration(ListenerRegistration listenerRegistration) {
            this.listenerRegistration = listenerRegistration;
        }

        // Unregister the listener when the view holder is recycled
        @Override
        protected void finalize() throws Throwable {
            if (listenerRegistration != null) {
                listenerRegistration.remove();
            }
            super.finalize();
        }
    }
}
