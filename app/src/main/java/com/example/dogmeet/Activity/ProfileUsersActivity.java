package com.example.dogmeet.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileUsersActivity extends AppCompatActivity {
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_users);

        /*TextView bio=findViewById(R.id.text_name);

        Intent i = getIntent();
        if(i != null)
        {
            uid=i.getStringExtra(Constant.USER_UID);
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                bio.setText(user.getName().toString()+", "+user.getAge().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }
}