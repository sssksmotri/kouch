package com.example.kouch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.kouch.Model.User;
import com.example.kouch.adapter.SearchUserRecyclerAdapter;
import com.example.kouch.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserAction extends AppCompatActivity {
    EditText searchinput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_action);
        searchinput=findViewById(R.id.serch_username_input);
        searchButton=findViewById(R.id.search_user_btn);
        backButton=findViewById(R.id.back_btn);
        recyclerView=findViewById(R.id.search_user_resycler_view);
        searchinput.requestFocus();
        backButton.setOnClickListener(v->{
            onBackPressed();
        });
        searchButton.setOnClickListener(v->{
            String searchTerm=searchinput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){
                searchinput.setError("Invalide Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });
    }
    void setupSearchRecyclerView(String searchTerm) {

        Query query = FirebaseUtil.allCollectionReferens()
                .whereGreaterThanOrEqualTo("city", searchTerm)
                .whereLessThanOrEqualTo("city", searchTerm + '\uf8ff');

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();



        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected  void onStart(){
        super.onStart();
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

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
            updateStatus("online");
        }
    }
}