package com.example.dogmeet.ui.profile;

import static com.example.dogmeet.Constant.URI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Activity.AddPetActivity;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {
    ImageButton buttonEdit, buttonAdd, buttonSave;
    ImageView imageView;
    EditText about;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference database;
    DatabaseReference ref;
    private View view;

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
                if (user.getAvatar_uri()!=null){
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

        return view;
    }
}