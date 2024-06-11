package com.example.kouch.adapter;

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
import com.example.kouch.R;
import com.example.kouch.Model.User;
import com.example.kouch.utils.AndroidUtil;
import com.example.kouch.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<User,SearchUserRecyclerAdapter.UserViewHolder> {

    Context context;
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        holder.usernameText.setText(model.getEmail());
        holder.fio.setText(model.getFName());
        if(model.getId().equals(FirebaseUtil.currentUserId())){
            holder.fio.setText(model.getFName()+" (Me)");
        }
        holder.itemView.setOnClickListener(v -> {
        Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserAsIntent(intent,model);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        });
        Log.d("SearchUserAdapter", "User bind: " + model.getEmail() + ", fName: " + model.getFName());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_raw,parent,false);
        return new UserViewHolder(view);
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView fio;
        ImageView profile_pic;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText=itemView.findViewById(R.id.user_name_text);
            fio=itemView.findViewById(R.id.user_name);
            profile_pic=itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
