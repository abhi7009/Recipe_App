package com.example.recipe_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class Upload_Activity extends AppCompatActivity {

    ImageView image_view;
    EditText name,description,price;
    Button select,upload;
    Uri image_uri;
    String image_url;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        select = findViewById(R.id.select);
        upload = findViewById(R.id.upload);
        image_view = findViewById(R.id.image_view);
        progressDialog = new ProgressDialog(this);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent image_picker = new Intent(Intent.ACTION_GET_CONTENT);
                image_picker.setType("image/*");
                startActivityForResult(image_picker,101);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

    private void uploadImage() {

        progressDialog.setMessage("Image Uploading...");
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference().child("Recipe_Images").child(image_uri.getLastPathSegment());

        storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                image_url = urlImage.toString();
                uploadRecipe();
            }
        });
    }

    private void uploadRecipe() {

        if(name.getText().toString().isEmpty()){
            name.setError("Required");
        }
        if(description.getText().toString().isEmpty()){
            description.setError("Required");
        }
        if(price.getText().toString().isEmpty()){
            price.setError("Required");
        }

        progressDialog.setMessage("Recipe Uploading...");
        progressDialog.show();

        ModelClass model = new ModelClass(name.getText().toString(),description.getText().toString()
        ,price.getText().toString(),image_url);


        String myCurrentDateTime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        FirebaseDatabase.getInstance().getReference("Recipe")
                .child(myCurrentDateTime).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Upload_Activity.this, "Recipe Uploaded", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Upload_Activity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
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
}