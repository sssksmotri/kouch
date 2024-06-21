package com.example.kouch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kouch.Model.User;
import com.example.kouch.utils.AndroidUtil;
import com.example.kouch.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText cityinput;
    EditText addresinput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;
    User currentUser;
    ActivityResultLauncher<Intent> imagePicLauncher;
    Uri selectedImageUri;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePicLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        cityinput = view.findViewById(R.id.profile_city);
        addresinput = view.findViewById(R.id.profile_addres);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.Logout_btn);
        getUserData();
        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClick();
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           logout();
                       }
                    }
                });
            }
        });
        profilePic.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePicLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        return view;
    }
    public void logout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userID");
        editor.apply();

        // Вызов метода signOut для выхода из Firebase
        FirebaseAuth.getInstance().signOut();

        // Возвращение к экрану авторизации
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        getActivity().finish();
    }
    void updateBtnClick() {
        String newusername = usernameInput.getText().toString();
        String newcity = cityinput.getText().toString();
        String addres = addresinput.getText().toString();

        // Username validation
        if (newusername.isEmpty() || newusername.length() < 3) {
            usernameInput.setError("Введите минимум 3 символа");
            return;
        }
        currentUser.setFName(newusername);
        // Email validation
        if (newcity.isEmpty()|| newcity.length()<3 ) {
            cityinput.setError("Введите минимум 3 символа");
            return;
        }
        currentUser.setCity(newcity);
        if (addres.isEmpty()|| addres.length()<3 ) {
            addresinput.setError("Введите минимум 3 символа");
            return;
        }
        currentUser.setAddres(addres);
        setInProggres(true);
        if(selectedImageUri!=null){
            FirebaseUtil.GetCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateToFirestore();
                    });
        }
        else {
            updateToFirestore();
        }
    }
    void updateToFirestore(){
        FirebaseUtil.currentUserDetails().set(currentUser)
                .addOnCompleteListener(task -> {
                    setInProggres(false);
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(getContext(),"Данные успешно обновленны");
                    }
                    else {
                        AndroidUtil.showToast(getContext(),"Возникли проблемы при обновлении");
                    }
                });
    }


    void getUserData(){
        setInProggres(true);
        FirebaseUtil.GetCurrentProfilePicStorageRef().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePic(getContext(),uri,profilePic);
                            }
                        });
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProggres(false);
            currentUser=task.getResult().toObject(User.class);
            usernameInput.setText(currentUser.getFName());
            cityinput.setText(currentUser.getCity());
            addresinput.setText(currentUser.getAddres());
        });
    }
    void setInProggres(boolean inProggres){
        if(inProggres){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }
        else {
            updateProfileBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}