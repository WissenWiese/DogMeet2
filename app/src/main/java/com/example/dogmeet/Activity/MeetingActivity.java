package com.example.dogmeet.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.User;
import com.example.dogmeet.mainActivity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MeetingActivity extends AppCompatActivity {
    private TextView meetTitle, meetDate, meetAddress, meetCreator, meetTime, meetDescription, meetNumber;
    private String creatorUid, uid, meetUid;
    private int number_mender;
    private Button button;
    private FirebaseAuth auth;
    private DatabaseReference myMeet, users;
    private FirebaseDatabase database;
    private List<String> listMember;
    private User user;
    private ListView listView;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        init();
        getIntentMain();
        button=findViewById(R.id.button);

        database = FirebaseDatabase.getInstance();
        myMeet = database.getReference("meeting");
        users = database.getReference("Users");

        listView = findViewById(R.id.listView2);
        listMember = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listMember);
        listView.setAdapter(adapter);

        FirebaseUser cur_user = auth.getInstance().getCurrentUser();

        if(cur_user == null)
        {
            startActivity(new Intent(MeetingActivity.this, LoginActivity.class));
        } else {
            uid = cur_user.getUid();
        }

        getMember();

        if (creatorUid.equals(uid)){
            button.setText("Редактировать");
        }
        else {
            button.setText("Присоединиться");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myMeet.child(meetUid).child("members").child(uid).setValue(user.getName());
                }
            });
        }

        getDataFromDB();
    }
    private void init()
    {
        meetTitle = findViewById(R.id.meetTitle);
        meetDate = findViewById(R.id.meetDate);
        meetAddress = findViewById(R.id.meetAddress);
        meetCreator = findViewById(R.id.meetCreator);
        meetTime=findViewById(R.id.meetTime);
        meetDescription=findViewById(R.id.meetDescription);
        meetNumber=findViewById(R.id.meetNumber);
        meetCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                if(i != null)
                {
                    Intent intent = new Intent(MeetingActivity.this, ProfileUsersActivity.class);
                    intent.putExtra(Constant.MEETING_TITLE, i.getStringExtra(Constant.MEETING_CREATOR_UID));
                }
            }
        });
    }
    private void getIntentMain()
    {
        Intent i = getIntent();
        if(i != null)
        {
            meetTitle.setText(i.getStringExtra(Constant.MEETING_TITLE));
            meetDate.setText(i.getStringExtra(Constant.MEETING_DATE));
            meetAddress.setText(i.getStringExtra(Constant.MEETING_ADDRESS));
            meetCreator.setText(i.getStringExtra(Constant.MEETING_CREATOR));
            meetTime.setText(i.getStringExtra(Constant.MEETING_TIME));
            meetDescription.setText(i.getStringExtra(Constant.MEETING_DESCRIPTION));
            meetNumber.setText(i.getStringExtra(Constant.MEETING_NUMBER));
            creatorUid=i.getStringExtra(Constant.MEETING_CREATOR_UID);
            meetUid=i.getStringExtra(Constant.MEETING_UID);

        }
    }

    public void getMember() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDataFromDB()
    {

        ValueEventListener memberListener = new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(listMember.size() > 0)listMember.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String user_name =dataSnapshot.getValue(String.class);
                    assert user_name != null;
                    listMember.add(user_name);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMeet.child(meetUid).child("members").addValueEventListener(memberListener);
    }
}