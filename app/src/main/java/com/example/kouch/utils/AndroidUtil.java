package com.example.kouch.utils;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kouch.Model.User;


public class AndroidUtil {
    public static void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
    public static void passUserAsIntent(Intent intent, User model){
        intent.putExtra("FName",model.getFName());
        intent.putExtra("Email",model.getEmail());
        intent.putExtra("id",model.getId());
        intent.putExtra("fcmToken",model.getFcmToken());

    }
    public static User getUserFromInten(Intent intent){
    User user = new User();
    user.setFName(intent.getStringExtra("FName"));
    user.setEmail(intent.getStringExtra("Email"));
    user.setId(intent.getStringExtra("id"));
    user.setFcmToken(intent.getStringExtra("fcmToken"));
    return user;
    }
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);

    }
}
