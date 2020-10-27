package com.example.recipe_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Detail_Activity extends AppCompatActivity {

    TextView description,name,price;
    ImageView image_view;
    Button delete,update;
    String key = "";
    Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_);

        description = findViewById(R.id.description);
        image_view = findViewById(R.id.image_view);
        delete = findViewById(R.id.delete);
        update = findViewById(R.id.update);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);

        String get_description = getIntent().getExtras().getString("Description");
        final String get_image = getIntent().getExtras().getString("Image");
        key = getIntent().getExtras().getString("Key");
        final String get_name = getIntent().getExtras().getString("Name");
        String get_price = getIntent().getExtras().getString("Price");

        description.setText(get_description);
        name.setText(get_name);
        price.setText(get_price);

        Glide.with(this).load(get_image).into(image_view);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReferenceFromUrl(get_image);

                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReference.child("Recipe").child(key).removeValue();
                        Toast.makeText(Detail_Activity.this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Detail_Activity.this,MainActivity.class));
                        finish();
                    }
                });

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Detail_Activity.this,Update_Activity.class)
                        .putExtra("Recipe Name",name.getText().toString()).putExtra("Recipe Description",description.getText().toString())
                        .putExtra("Recipe Price",price.getText().toString()).putExtra("old image url",get_image)
                        .putExtra("old key",key));
            }
        });

    }

}