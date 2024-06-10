package com.example.kouch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchUserAction extends AppCompatActivity {
    EditText searchinput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_action);
        searchinput=findViewById(R.id.serch_username_inpput);
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
   void setupSearchRecyclerView(String searchTerm){


   }
}