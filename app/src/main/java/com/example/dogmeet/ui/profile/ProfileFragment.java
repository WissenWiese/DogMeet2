package com.example.dogmeet.ui.profile;

import static android.app.Activity.RESULT_OK;

import static com.example.dogmeet.Constant.URI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Activity.AddPetActivity;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class ProfileFragment extends Fragment {
    ImageButton buttonEdit, buttonAdd, buttonSave, imageView;
    EditText about;
    DatabaseReference database;
    DatabaseReference ref;
    private final int PICK_IMAGE_REQUEST = 71;
    private View view;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;

    public ProfileFragment(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        TextView bio=view.findViewById(R.id.text_name);
        buttonEdit=view.findViewById(R.id.editBtn);
        buttonSave=view.findViewById(R.id.saveBtn);
        buttonAdd=view.findViewById(R.id.addPetBtn);
        imageView=view.findViewById(R.id.imageViewAvatar);
        about=view.findViewById(R.id.edit_about);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance().getReference();
        ref = database.child("Users").child(auth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                bio.setText(user.getName().toString()+", "+user.getAge().toString());
                if (user.getInfo()!=null){
                    about.setText(user.getInfo());
                }
                if (user.getAvatarUri()!=null){
                    Glide.with(imageView.getContext()).load(user.getAvatarUri()).into(imageView);
                }
                else {
                    Glide.with(imageView.getContext()).load(URI).into(imageView);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about.setEnabled(true);
                if (about!=null){
                    buttonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ref.child("info").setValue(about.getText().toString());
                            about.setEnabled(false);
                        }
                    });
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), AddPetActivity.class);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();

            if(filePath != null)
            {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                StorageReference ref1 = storageReference.child("avatar/"+ UUID.randomUUID().toString());
                UploadTask upload_image= ref1.putFile(filePath);
                upload_image
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        return ref1.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            ref.child("avatarUri").setValue(downloadUri.toString());

                        } else {
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}