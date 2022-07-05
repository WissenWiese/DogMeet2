package com.example.dogmeet.Activity;

import static com.example.dogmeet.Constant.URI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.Meeting;
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
import com.bumptech.glide.Glide;

public class MeetingActivity extends AppCompatActivity {
    private TextView meetTitle, meetDate, meetAddress, meetCreator, meetTime, meetDescription, meetNumber;
    private String creatorUid, uid, meetUid;
    private Button button;
    private FirebaseAuth auth;
    private DatabaseReference myMeet, users;
    private List<String> listMember;
    private User user;
    private Meeting meeting;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    ImageView imageView;
    DatabaseReference database;
    private int member_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        init();
        getIntentMain();
        button=findViewById(R.id.button);

        imageView=findViewById(R.id.imageView_meet);

        Glide.with(imageView.getContext()).load(URI).into(imageView);

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");
        users = FirebaseDatabase.getInstance().getReference("Users");

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
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MeetingActivity.this, EditMeetingActivity.class);
                    intent.putExtra(Constant.MEETING_UID, meetUid);
                    startActivity(intent);
                }
            });
        }
        else if (member_number==0){
            button.setText("Прсоединиться");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myMeet.child(meetUid).child("members").child(uid).setValue(user.getName());
                    int member_number1=member_number+1;
                    myMeet.child(meetUid).child("numberMember").setValue(member_number1);
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
        meetCreator.setClickable(true);
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
            meetUid=i.getStringExtra(Constant.MEETING_UID);
            creatorUid=i.getStringExtra(Constant.MEETING_CREATOR_UID);
            if (meetUid!=null) {
                database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref_meet=database.child("meeting").child(meetUid);
                ValueEventListener meetingListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        meeting = snapshot.getValue(Meeting.class);
                        if (meeting != null) {
                            meetTitle.setText(meeting.title);
                            meetDate.setText(meeting.date);
                            meetAddress.setText(meeting.address);
                            meetCreator.setText(meeting.creator);
                            meetTime.setText(meeting.time);
                            meetDescription.setText(meeting.description);
                            member_number=meeting.numberMember;
                            meetNumber.setText(Integer.toString(member_number));
                            String url=meeting.urlImage;
                            Glide.with(imageView.getContext()).load(url).into(imageView);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                ref_meet.addValueEventListener(meetingListener);
            }
        }
    }

    public void getMember() {
        users.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    if (dataSnapshot.getKey().equals(uid)){
                        button.setText("Покинуть");
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dataSnapshot.getRef().removeValue();
                                int member_number1=member_number-1;
                                myMeet.child(meetUid).child("numberMember").setValue(member_number1);
                            }
                        });
                    }
                    else{
                        button.setText("Прсоединиться");
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myMeet.child(meetUid).child("members").child(uid).setValue(user.getName());
                                int member_number1=member_number+1;
                                myMeet.child(meetUid).child("numberMember").setValue(member_number1);
                                button.setText("Покинуть");
                            }
                        });
                    }
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