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



public class RecentRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel,RecentRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;
    public RecentRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options,Context context) {
        super(options);
        this.context=context;
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {

        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSendByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        User otheruser = task.getResult().toObject(User.class);
                        FirebaseUtil.GetOtherProfilePicStorageRef(otheruser.getId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if (t.isSuccessful()) {
                                        Uri uri = t.getResult();
                                        AndroidUtil.setProfilePic(context, uri, holder.profile_pic);
                                    }
                                });
                        holder.fio.setText(otheruser.getFName());

                        String lastMessage = model.getLastMessage_user();
                        if (lastMessage.length() > 50) {
                            lastMessage = lastMessage.substring(0, 50) + "...";
                        }
                        holder.lastMessegeText.setText(lastMessage);

                        holder.lastMessegeTime.setText(FirebaseUtil.timestampToString(model.getLastMessage()));

                        if ("online".equals(otheruser.getStatus())) {
                            holder.statusIndicator.setVisibility(View.VISIBLE);
                        } else {
                            holder.statusIndicator.setVisibility(View.INVISIBLE);
                        }

                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserAsIntent(intent, otheruser);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder{
        TextView fio;
        TextView lastMessegeText;
        TextView lastMessegeTime;
        ImageView profile_pic;
        ImageView statusIndicator;
        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            lastMessegeText=itemView.findViewById(R.id.last_message_text);
            lastMessegeTime=itemView.findViewById(R.id.last_messag_time);
            fio=itemView.findViewById(R.id.user_name_text);
            profile_pic=itemView.findViewById(R.id.profile_pic_image_view);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }
    }
}
