package com.example.kouch.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
                    if(task.isSuccessful()){
                        boolean LastMessageSendByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        User otheruser = task.getResult().toObject(User.class);
                        holder.fio.setText(otheruser.getFName());
                        if(LastMessageSendByMe)
                            holder.lastMessegeText.setText(model.getLastMessage_user());
                        else
                        holder.lastMessegeText.setText(model.getLastMessage_user());
                        holder.lastMessegeTime.setText(FirebaseUtil.timestampToString(model.getLastMessage()));

                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserAsIntent(intent,otheruser);
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
        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            lastMessegeText=itemView.findViewById(R.id.last_message_text);
            lastMessegeTime=itemView.findViewById(R.id.last_messag_time);
            fio=itemView.findViewById(R.id.user_name_text);
            profile_pic=itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
