package com.example.recipe_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class Update_Activity extends AppCompatActivity {

    EditText name,description,price;
    Button select,update;
    ImageView image_view;
    Uri image_uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    AlertDialog progressDialog;
    String image_url;
    String recipe_name;
    String recipe_description;
    String recipe_price;
    String get_image;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        select = findViewById(R.id.select);
        update = findViewById(R.id.update);
        image_view = findViewById(R.id.image_view);

        String get_name = getIntent().getExtras().getString("Recipe Name");
        String get_description = getIntent().getExtras().getString("Recipe Description");
        String get_price = getIntent().getExtras().getString("Recipe Price");
        String get_key = getIntent().getExtras().getString("old key");
        get_image = getIntent().getExtras().getString("old image url");

        Glide.with(this).load(get_image).into(image_view);
        name.setText(get_name);
        description.setText(get_description);
        price.setText(get_price);

        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe")
        .child(get_key);


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent image_picker = new Intent(Intent.ACTION_GET_CONTENT);
                image_picker.setType("image/*");
                startActivityForResult(image_picker,101);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipe_name = name.getText().toString().trim();
                recipe_description = description.getText().toString().trim();
                recipe_price = price.getText().toString();

                progressDialog = new ProgressDialog(Update_Activity.this);
                progressDialog.setMessage("Recipe Updating...");
                progressDialog.show();

                storageReference = FirebaseStorage.getInstance().getReference().child("Recipe_Images").child(image_uri.getLastPathSegment());

                storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri urlImage = uriTask.getResult();
                        image_url = urlImage.toString();
                        updateRecipe();
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101){
            if(resultCode == RESULT_OK){
                image_uri = data.getData();
                image_view.setImageURI(image_uri);
            } else {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }

        }

    }

    private void updateRecipe() {


        if(name.getText().toString().isEmpty()){
            name.setError("Required");
        }
        if(description.getText().toString().isEmpty()){
            description.setError("Required");
        }
        if(price.getText().toString().isEmpty()){
            price.setError("Required");
        }

        ModelClass model = new ModelClass(recipe_name,recipe_description
                ,recipe_price,image_url);

        databaseReference.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                StorageReference storageReference1 = FirebaseStorage.getInstance().getReferenceFromUrl(get_image);
                storageReference1.delete();
                Toast.makeText(Update_Activity.this, "Data Updated", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        });

    }

}