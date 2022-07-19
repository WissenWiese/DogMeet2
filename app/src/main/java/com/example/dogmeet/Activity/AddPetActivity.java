package com.example.dogmeet.Activity;

import static com.example.dogmeet.Constant.URI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.Pet;
import com.example.dogmeet.mainActivity.AddActivity;
import com.example.dogmeet.mainActivity.LoginActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class AddPetActivity extends AppCompatActivity {
    private EditText namePetEditText, agePetEditText, breedEditText, genderPetEditText;
    private Button addPetButton, button_upload_avatar;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private FirebaseAuth auth;
    private String uid;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
        setTitle("Добавить питомца");

        namePetEditText = findViewById(R.id.edit_name_pet);
        agePetEditText = findViewById(R.id.editAge_pet);
        genderPetEditText = findViewById(R.id.editGender);
        breedEditText=findViewById(R.id.editBreed);
        addPetButton = findViewById(R.id.btnAddPet);
        button_upload_avatar=findViewById(R.id.button_upload_avatar_pet);
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        imageView=findViewById(R.id.imageView_pet);

        Glide.with(imageView.getContext()).load(URI).into(imageView);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FirebaseUser cur_user = auth.getInstance().getCurrentUser();

        if(cur_user == null)
        {
            startActivity(new Intent(AddPetActivity.this, LoginActivity.class));
        } else {
            uid = cur_user.getUid();
        }

        addPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameText = namePetEditText.getText().toString();
                String ageText = agePetEditText.getText().toString();
                String breedText=breedEditText.getText().toString();
                String genderText=genderPetEditText.getText().toString();

                Pet pet = new Pet();
                pet.setName(nameText);
                pet.setAge(ageText);
                pet.setBreed(breedText);
                pet.setGender(genderText);
                if (filePath!=null) {
                    uploadImage(pet);
                }
                else {
                    users.child(uid).child("pets").push().setValue(pet);
                    AddPetActivity.this.finish();
                }
            }
        });

        button_upload_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            button_upload_avatar.setText("Изображение загружено");
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Pet pet) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("pet/"+ UUID.randomUUID().toString());
            UploadTask upload_image=ref.putFile(filePath);
            upload_image
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddPetActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Uri downloadUri = taskSnapshot.getUploadSessionUri();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddPetActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
            Task<Uri> urlTask = upload_image.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        pet.setAvatar_pet(downloadUri.toString());
                        users.child(uid).child("pets").push().setValue(pet);
                        AddPetActivity.this.finish();

                    } else {
                        Toast.makeText(AddPetActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}