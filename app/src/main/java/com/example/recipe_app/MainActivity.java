package com.example.recipe_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    List<ModelClass> modelClassList;
    FloatingActionButton floating;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    EditText search;
    Adapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerview = findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        recyclerview.setLayoutManager(gridLayoutManager);

        search = findViewById(R.id.search);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Items...");

        modelClassList = new ArrayList<>();
        myAdapter = new Adapter(MainActivity.this,modelClassList);
        recyclerview.setAdapter(myAdapter);

        progressDialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference();
               databaseReference.child("Recipe").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot itemSnapshot : snapshot.getChildren()){

                          ModelClass model = itemSnapshot.getValue(ModelClass.class);

                          model.setKey(itemSnapshot.getKey());

                          modelClassList.add(model);
                        }
                        myAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });

        floating = findViewById(R.id.floating);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(MainActivity.this,Upload_Activity.class));
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                filter(editable.toString());
            }
        });

    }

    private void filter(String text) {

        ArrayList<ModelClass> filterlist = new ArrayList<>();

        for(ModelClass item : modelClassList){
            if(item.getItemName().toLowerCase().contains(text.toLowerCase())){
                filterlist.add(item);
            }
        }

        myAdapter.filteredlist(filterlist);

    }

}