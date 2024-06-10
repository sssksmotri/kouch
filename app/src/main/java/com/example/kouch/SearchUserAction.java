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
        Log.d("SearchUserAction", "Setting up recycler view with searchTerm: " + searchTerm);
        Query query = FirebaseUtil.allCollectionReferens()
                .whereGreaterThanOrEqualTo("FName", searchTerm)
                .whereLessThanOrEqualTo("FName", searchTerm + '\uf8ff'); // Добавляем верхний предел

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();

        Log.d("SearchUserAction", "Query created, options set.");

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        Log.d("SearchUserAction", "Adapter set and listening started.");
    }
    @Override
    protected void onStart(){
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();}
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.startListening();
        }
    }
}