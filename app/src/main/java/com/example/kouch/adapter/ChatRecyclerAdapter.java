package com.example.kouch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kouch.Model.ChatMessageModel;
import com.example.kouch.Model.User;
import com.example.kouch.R;
import com.example.kouch.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    private OnMessageClickListener onMessageClickListener;

    public interface OnMessageClickListener {
        void onMessageLongClick(ChatMessageModel message, View view);
    }

    public void setOnMessageClickListener(OnMessageClickListener listener) {
        this.onMessageClickListener = listener;
    }

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        // Determine if the current user is the sender or receiver
        boolean isCurrentUserSender = model.getSenderId().equals(FirebaseUtil.currentUserId());

        // Set visibility based on sender/receiver
        holder.leftChatLayout.setVisibility(isCurrentUserSender ? View.GONE : View.VISIBLE);
        holder.rightChatLayout.setVisibility(isCurrentUserSender ? View.VISIBLE : View.GONE);

        // Set message text and timestamp
        if (isCurrentUserSender) {
            holder.rightChatTextView.setText(model.getMessage());
            holder.rightChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        } else {
            holder.leftChatTextView.setText(model.getMessage());
            holder.leftChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        }
        FirebaseUtil.getUserDetails(model.getSenderId()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user!=null) {
                    holder.leftReplySender.setText(user.getFName());
                    holder.rightReplySender.setText(user.getFName());
                } else {

                }
            } else {
                // Handle case where user details are not found
                if (isCurrentUserSender) {
                    holder.rightReplySender.setText("Unknown User");
                } else {
                    holder.leftReplySender.setText("Unknown User");
                }
            }
        });

        // Handle reply message display (similar logic as above for FName)
        if (model.getReplyToMessageId() != null) {
            if (isCurrentUserSender) {
                holder.rightReplyContainer.setVisibility(View.VISIBLE);
                holder.rightReplyText.setText(model.getReplyToMessageText());
            } else {
                holder.leftReplyContainer.setVisibility(View.VISIBLE);
                holder.leftReplyText.setText(model.getReplyToMessageText());
            }
        } else {
            if (isCurrentUserSender) {
                holder.rightReplyContainer.setVisibility(View.GONE);
            } else {
                holder.leftReplyContainer.setVisibility(View.GONE);
            }
        }

        // Long click listener
        holder.itemView.setOnLongClickListener(v -> {
            if (onMessageClickListener != null) {
                onMessageClickListener.onMessageLongClick(model, v);
                return true;
            }
            return false;
        });
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_raw, parent, false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextView, rightChatTextView, rightChatTimestamp, leftChatTimestamp;
        LinearLayout leftReplyContainer, rightReplyContainer;
        TextView leftReplySender, rightReplySender;
        TextView leftReplyText, rightReplyText;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightChatTextView = itemView.findViewById(R.id.right_chat_text_view);
            rightChatTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
            leftChatTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
            leftReplyContainer = itemView.findViewById(R.id.left_reply_container);
            rightReplyContainer = itemView.findViewById(R.id.right_reply_container);
            leftReplySender = itemView.findViewById(R.id.left_reply_sender);
            rightReplySender = itemView.findViewById(R.id.right_reply_sender);
            leftReplyText = itemView.findViewById(R.id.left_reply_text);
            rightReplyText = itemView.findViewById(R.id.right_reply_text);
        }
    }


}