package com.example.kouch.adapter;

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
import com.example.kouch.R;
import com.example.kouch.Model.User;
import com.example.kouch.utils.AndroidUtil;
import com.example.kouch.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;


public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<User,SearchUserRecyclerAdapter.UserViewHolder> {

    Context context;
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        holder.usernameText.setText(model.getCity());
        holder.fio.setText(model.getFName());
        if(model.getId().equals(FirebaseUtil.currentUserId())){
            holder.fio.setText(model.getFName()+" (Me)");
        }
        FirebaseUtil.GetOtherProfilePicStorageRef(model.getId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(context,uri,holder.profile_pic);
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        Log.d("SearchUserAdapter", "User bind: " + model.getCity() + ", fName: " + model.getFName());
        ListenerRegistration statusListener = FirebaseFirestore.getInstance()
                .collection("users")
                .document(model.getId())
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
        holder.setListenerRegistration(statusListener);
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
        ImageView statusIndicator;
        private ListenerRegistration listenerRegistration;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText=itemView.findViewById(R.id.user_name_text);
            fio=itemView.findViewById(R.id.user_name);
            profile_pic=itemView.findViewById(R.id.profile_pic_image_view);
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
