package com.example.dogmeet.Fragment.Profile;

import static android.app.Activity.RESULT_OK;
import static com.example.dogmeet.Constant.URI;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.model.Pet;
import com.example.dogmeet.model.User;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ProfileFragment_creator extends Fragment implements RecyclerViewInterface{
    ImageButton buttonEdit, buttonSave, buttonAdd, imageView, avatarPet;
    EditText about;
    DatabaseReference users, pets;
    private final int PICK_IMAGE_REQUEST = 71;
    private View view;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    private ArrayList<Pet> mPets;
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    private Boolean editPet, isPetAvatar;
    FirebaseAuth auth;
    String petUid;


    public ProfileFragment_creator(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mPets = new ArrayList<>();
        editPet=false;
        isPetAvatar=false;

        auth = FirebaseAuth.getInstance();
        TextView bio=view.findViewById(R.id.text_name);
        buttonEdit=view.findViewById(R.id.editBtn);
        buttonAdd=view.findViewById(R.id.addPetBtn);
        buttonSave=view.findViewById(R.id.saveBtn);
        imageView=view.findViewById(R.id.chatAvatar);
        about=view.findViewById(R.id.edit_about);
        about.setCursorVisible(false);
        about.setBackgroundColor(Color.TRANSPARENT);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        users = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());
        pets=FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid()).child("pets");

        recyclerView=view.findViewById(R.id.r_v_pet);
        recyclerView.setHasFixedSize(true);

        petAdapter= new PetAdapter(mPets, this, editPet);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(petAdapter);

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                bio.setText(user.getName().toString());
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

        pets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mPets.size() > 0)mPets.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Pet pet =dataSnapshot.getValue(Pet.class);
                    assert pet != null;
                    pet.setPetUid(dataSnapshot.getKey());
                    mPets.add(pet);
                }
                petAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about.setEnabled(true);
                buttonEdit.setTag("save");
                editPet=true;
                buttonEdit.setVisibility(View.INVISIBLE);
                buttonSave.setVisibility(View.VISIBLE);
                petAdapter.notifyDataSetChanged();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (about!=null){
                    users.child("info").setValue(about.getText().toString());
                    about.setEnabled(false);
                    about.setCursorVisible(false);
                    about.setBackgroundColor(Color.TRANSPARENT);
                }
                editPet=false;
                buttonEdit.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.INVISIBLE);
                petAdapter.notifyDataSetChanged();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPetWindow();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isPetAvatar=false;
                showImageWindow(false, null);
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
                if (!isPetAvatar){
                    uploadImage(false, null);
                }
                else {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                        avatarPet.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void OnItemClick(int position) {
        Pet pet=mPets.get(position);
        showEditPetWindow(pet);
    }

    @Override
    public void OnButtonClick(int position) {

    }

    private void showImageWindow(boolean isPetAvatar, String petUid){
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());

        final String[] editPhoto={"Загрузить", "Удалить"};

        StorageReference ref;
        DatabaseReference databaseReference;

        if (isPetAvatar){
            ref = storageReference.child(auth.getUid()).child(petUid);
            databaseReference=pets.child(petUid).child("avatar_pet");
        }
        else{
            ref = storageReference.child(auth.getUid()).child("avatar");
            databaseReference=users.child("avatarUri");
        }

        dialog.setItems(editPhoto, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (editPhoto[i]){
                    case "Загрузить":
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        break;
                    case "Удалить":
                        databaseReference.removeValue();
                        ref.delete();
                        break;
                }
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private void showPetWindow(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());

        LayoutInflater inflator= LayoutInflater.from(getContext());
        View add_pet_window = inflator.inflate(R.layout.item_edit_pet, null);
        dialog.setView(add_pet_window);

        final EditText namePet= add_pet_window.findViewById(R.id.name);
        avatarPet= add_pet_window.findViewById(R.id.avatar);


        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(namePet.getText().toString())) {
                    Toast.makeText(getContext(), "Введите имя питомца", Toast.LENGTH_LONG).show();
                    return;
                }
                petUid=UUID.randomUUID().toString();
                pets.child(petUid).child("name").setValue(namePet.getText().toString());
                if (filePath!=null) {
                    uploadImage(true, petUid);
                }
                dialogInterface.dismiss();
            }
        });

        avatarPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                isPetAvatar=true;
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        dialog.show();
    }

    private void showEditPetWindow(Pet pet){
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());

        LayoutInflater inflator= LayoutInflater.from(getContext());
        View register_window= inflator.inflate(R.layout.item_edit_pet, null);
        dialog.setView(register_window);

        final EditText namePet=register_window.findViewById(R.id.name);
        avatarPet=register_window.findViewById(R.id.avatar);

        if (pet.getAvatar_pet()!=null){
            String url=pet.getAvatar_pet();
            Glide.with(getContext()).load(url).into(avatarPet);
        }
        else {
            Glide.with(getContext()).load(URI).into(avatarPet);
        }

        namePet.setText(pet.getName());

        dialog.setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                pets.child(pet.getPetUid()).removeValue();
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(namePet.getText().toString())) {
                    Toast.makeText(getContext(), "Введите имя питомца", Toast.LENGTH_LONG).show();
                    return;
                }
                pets.child(pet.getPetUid()).child("name").setValue(namePet.getText().toString());
                if (filePath!=null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                        avatarPet.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploadImage(true, pet.getPetUid());
                }
                dialogInterface.dismiss();
            }
        });

        avatarPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageWindow(true, pet.getPetUid());
            }
        });

        dialog.show();
    }

    private void uploadImage(Boolean isPetAvatar, String petUid) {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref;
            DatabaseReference databaseReference;
            if (isPetAvatar){
                ref = storageReference.child(auth.getUid()).child(petUid);
                databaseReference=pets.child(petUid).child("avatar_pet");
            }
            else{
                ref = storageReference.child(auth.getUid()).child("avatar");
                databaseReference=users.child("avatarUri");
            }

            UploadTask upload_image=ref.putFile(filePath);
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
                    return ref.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        databaseReference.setValue(downloadUri.toString());

                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}