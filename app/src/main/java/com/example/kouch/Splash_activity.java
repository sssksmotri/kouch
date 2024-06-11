package com.example.kouch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kouch.Model.User;
import com.example.kouch.utils.AndroidUtil;
import com.example.kouch.utils.FirebaseUtil;

public class Splash_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(getIntent().getExtras()!=null) {
            String userId=getIntent().getExtras().getString("userId");
            FirebaseUtil.allCollectionReferens().document(userId).get()
                    .addOnCompleteListener(task ->{
                        if(task.isSuccessful()){
                            User model =task.getResult().toObject(User.class);
                            Intent mainIntent = new Intent(this,MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);
                            Intent intent = new Intent(this, ChatActivity.class);
                            AndroidUtil.passUserAsIntent(intent,model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
        else{
        new Handler().postDelayed(() -> {
            // Проверяем сохранённый идентификатор пользователя
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String savedUserID = sharedPreferences.getString("userID", null);
            Intent intent;
            if (savedUserID != null) {
                // Пользователь уже авторизован, переходим к главной активности
                intent = new Intent(Splash_activity.this, MainActivity.class);
            } else {
                // Показываем экран авторизации
                intent = new Intent(Splash_activity.this, Login.class);
            }
            startActivity(intent);
            finish();
        }, 20);
    }
}
}
