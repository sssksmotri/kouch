package com.example.kouch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kouch.Model.User;
import com.example.kouch.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private EditText edEmail, edPassword;
    private Button btn_login;
    private FirebaseAuth mAuth;

    private TextView register_txt, email_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btn_login = findViewById(R.id.btn_login);
        register_txt = findViewById(R.id.register_txt);
        email_txt = findViewById(R.id.email_txt);

        // Проверяем сохранённый идентификатор пользователя
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedUserID = sharedPreferences.getString("userID", null);
        Log.d("SavedUserID", "UserID: " + savedUserID);
        if (savedUserID != null) {
            // Пользователь уже авторизован, переходим к главной активности
            Log.d("LoginScreen", "User already logged in, redirecting to MainActivity");
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish(); // Закрываем текущую активность
        } else {
            // Показываем экран авторизации
            Log.d("LoginScreen", "Initializing login screen");
            initializeLoginScreen();
        }
    }

    private void initializeLoginScreen() {
        register_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
        email_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, pass1.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edEmail.getText().toString().isEmpty() || edPassword.getText().toString().isEmpty()) {
                    Toast.makeText(Login.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(edEmail.getText().toString(), edPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Сохраняем идентификатор пользователя
                                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("userID", mAuth.getCurrentUser().getUid());
                                        editor.apply();

                                        // Обновляем данные пользователя в Firestore
                                        updateUserInFirestore();

                                        Log.d("LoginScreen", "User logged in successfully, redirecting to Splash_activity");
                                        Intent intent = new Intent(Login.this, Splash_activity.class);
                                        startActivity(intent);
                                        finish(); // Закрываем текущую активность
                                    } else {
                                        Toast.makeText(Login.this, "You have errors", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
    private void updateUserInFirestore() {
        String userId = mAuth.getCurrentUser().getUid();
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If user details already exist, update them
                        User user = document.toObject(User.class);
                        if (user != null) {
                            Map<String, Object> userUpdates = new HashMap<>();
                            userUpdates.put("email", email);
                            userUpdates.put("FName", user.getFName());
                            userUpdates.put("LName", user.getRole());
                            userUpdates.put("createdAt", user.getCreatedTimeStamp() != null ? user.getCreatedTimeStamp() : FieldValue.serverTimestamp());
                            userUpdates.put("updatedAt", FieldValue.serverTimestamp());
                            userUpdates.put("photoUrl", user.getPhotoUrl());

                            docRef.update(userUpdates)
                                    .addOnSuccessListener(aVoid -> Log.d("LoginActivity", "User data updated in Firestore"))
                                    .addOnFailureListener(e -> Log.e("LoginActivity", "Error updating user data in Firestore", e));
                        }
                    } else {
                        User user = new User(userId, "", "Couch", email, "", password, "", FieldValue.serverTimestamp(),"");
                        db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> Log.d("LoginActivity", "User data created in Firestore"))
                                .addOnFailureListener(e -> Log.e("LoginActivity", "Error creating user data in Firestore", e));
                    }
                } else {
                    Log.e("LoginActivity", "Error fetching user data", task.getException());
                }
            }
        });
    }

    void getUsername() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                if (user != null) {
                    edEmail.setText(user.getEmail());
                }
            }
        });
    }
}
