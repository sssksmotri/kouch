package com.example.kouch.utils;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.kouch.Model.User;


public class AndroidUtil {
    public static void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
    public static void passUserAsIntent(Intent intent, User model){
        intent.putExtra("FName",model.getFName());
        intent.putExtra("Email",model.getEmail());
        intent.putExtra("id",model.getId());
    }
    public static User getUserFromInten(Intent intent){
    User user = new User();
    user.setFName(intent.getStringExtra("FName"));
    user.setEmail(intent.getStringExtra("Email"));
    user.setId(intent.getStringExtra("id"));
    return user;
    }
}
