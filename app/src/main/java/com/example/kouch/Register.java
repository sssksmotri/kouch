package com.example.kouch;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    private EditText edEmail, edPassword;
    private Button btn_register;
    private FirebaseAuth mAuth;
    private TextView avtoriz_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        avtoriz_txt = findViewById(R.id.avtoriz_txt);
        btn_register = findViewById(R.id.btn_register);

        avtoriz_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edEmail.getText().toString().isEmpty() || edPassword.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    Log.d("RegisterActivity", "Fields are empty");
                } else {
                    Log.d("RegisterActivity", "Attempting to register");
                    mAuth.createUserWithEmailAndPassword(edEmail.getText().toString(), edPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Log.d("RegisterActivity", "Registration successful");
                                        // Сохраняем данные пользователя в Firestore
                                        saveUserToFirestore();
                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Log.e("RegisterActivity", "Registration failed", task.getException());
                                        Toast.makeText(Register.this, "You have errors", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void saveUserToFirestore() {
        String userId = mAuth.getCurrentUser().getUid();
        String email = edEmail.getText().toString();
        String password =edPassword.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        User user = new User(userId, "", "", email, "", password, "",FieldValue.serverTimestamp());

        // Записываем пользователя в Firestore
        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("RegisterActivity", "User data created in Firestore"))
                .addOnFailureListener(e -> Log.e("RegisterActivity", "Error creating user data in Firestore", e));
    }
}
