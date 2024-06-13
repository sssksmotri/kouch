package com.example.kouch.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kouch.ChatActivity;
import com.example.kouch.R;
import com.example.kouch.Model.ChatMessageModel;
import com.example.kouch.utils.AndroidUtil;
import com.example.kouch.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel,ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    private OnMessageClickListener onMessageClickListener;

    public interface OnMessageClickListener {
        void onMessageLongClick(ChatMessageModel message, View view);
    }

    public void setOnMessageClickListener(OnMessageClickListener listener) {
        this.onMessageClickListener = listener;
    }

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
    if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
        holder.leftChatLayout.setVisibility(View.GONE);
        holder.rightChatLayout.setVisibility(View.VISIBLE);
        holder.rightChatTextView.setText(model.getMessage());
        holder.rightChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        holder.itemView.setOnLongClickListener(v -> {
            if (onMessageClickListener != null) {
                onMessageClickListener.onMessageLongClick(model, v);
                return true;
            }
            return false;
        });
    }
    else {
        holder.rightChatLayout.setVisibility(View.GONE);
        holder.leftChatLayout.setVisibility(View.VISIBLE);
        holder.leftChatTextView.setText(model.getMessage());
        holder.leftChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        holder.itemView.setOnLongClickListener(v -> {
            if (onMessageClickListener != null) {
                onMessageClickListener.onMessageLongClick(model, v);
                return true;
            }
            return false;
        });
    }
    }
    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_raw,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextView,rightChatTextView,rightChatTimestamp,leftChatTimestamp;
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout =itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout =itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView =itemView.findViewById(R.id.left_chat_text_view);
            rightChatTextView =itemView.findViewById(R.id.right_chat_text_view);
            rightChatTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
            leftChatTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
        }
    }
}
