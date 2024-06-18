package com.example.kouch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.kouch.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String userID;
    BottomNavigationView bottomNavigationView;
    ImageButton serchbutton;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serchbutton = findViewById(R.id.search_user);
        serchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the SearchUserAction activity
                startActivity(new Intent(MainActivity.this, SearchUserAction.class));
            }
        });
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        bottomNavigationView = findViewById(R.id.navigation_bar);
        serchbutton = findViewById(R.id.search_user);
        getFCMToketn();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_chats){
                    getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layoute,chatFragment).commit();
                }
                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layoute,profileFragment).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chats);

        getFCMToketn();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

    }
    void getFCMToketn(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
              if(task.isSuccessful()){
                  String token = task.getResult();
                  FirebaseUtil.currentUserDetails().update("fcmToken",token);
              }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateStatus("offline");
    }

    private void updateStatus(String status) {
        FirebaseUtil.currentUserDetails().update("status", status)
                .addOnSuccessListener(aVoid -> Log.d("StatusUpdate", "User status updated to " + status))
                .addOnFailureListener(e -> Log.w("StatusUpdate", "Error updating user status", e));
    }

}