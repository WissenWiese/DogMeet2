package com.example.dogmeet.Activity;

import static com.example.dogmeet.Constant.URI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.adapter.UserAdapter;
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

import com.bumptech.glide.Glide;

public class MeetingActivity extends AppCompatActivity implements RecyclerViewInterface {
    private TextView meetTitle, meetDate, meetAddress, meetCreator, meetTime, meetDescription, meetNumber;
    private String creatorUid, uid, meetUid, userUrl;
    private Button button;
    private FirebaseAuth auth;
    private DatabaseReference myMeet, users, ref, url;
    private User user;
    private Meeting meeting;
    ImageView imageView, meetCreat;
    DatabaseReference database;
    private int member_number;
    Context context;
    CardView cardView;
    private ArrayList<User> mUsers;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        init();
        getIntentMain();
        button=findViewById(R.id.button);

        context=this;

        imageView=findViewById(R.id.imageView_meet);

        user=new User();

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");
        users = FirebaseDatabase.getInstance().getReference("Users");

        mUsers = new ArrayList<>();

        recyclerView=findViewById(R.id.recycler_view_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(MeetingActivity.this));
        recyclerView.setHasFixedSize(true);

        userAdapter= new UserAdapter(mUsers, this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(MeetingActivity.this, LinearLayoutManager.HORIZONTAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(userAdapter);

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
        else /*if (member_number==0)*/{
            button.setText("Присоединиться");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button.getText().equals("Присоединиться")){

                    myMeet.child(meetUid).child("members").child(uid).setValue(user);
                    int member_number1=member_number+1;
                    myMeet.child(meetUid).child("numberMember").setValue(member_number1);
                    button.setText("Покинуть");
                }
                else if (button.getText().equals("Покинуть")){
                    ref.removeValue();
                    int member_number1=member_number-1;
                    myMeet.child(meetUid).child("numberMember").setValue(member_number1);
                    button.setText("Присоединиться");
                }
                else if (button.getText().equals("Редактировать")){
                    Intent intent = new Intent(MeetingActivity.this, EditMeetingActivity.class);
                    intent.putExtra(Constant.MEETING_UID, meetUid);
                    startActivity(intent);
                }
            }
        });

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
        cardView=findViewById(R.id.cardView);
        meetCreat=findViewById(R.id.imageCreat);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MeetingActivity.this, ProfileUsersActivity.class);
                i.putExtra(Constant.MEETING_CREATOR_UID, creatorUid);
                startActivity(i);
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
                        User creator=meeting.getCreator();
                        if (creator!=null){
                            String creator_name=creator.getName();
                            meetCreator.setText(creator_name);
                        }

                        if (meeting != null) {
                            meetTitle.setText(meeting.title);
                            meetDate.setText(meeting.date);
                            meetAddress.setText(meeting.address);
                            meetTime.setText(meeting.time);
                            meetDescription.setText(meeting.description);
                            member_number=meeting.numberMember;
                            meetNumber.setText(Integer.toString(member_number));
                            String url=meeting.urlImage;
                            if (url!=null){
                                Glide.with(imageView.getContext()).load(url).into(imageView);
                            }
                            else {
                                Glide.with(imageView.getContext()).load(URI).into(imageView);
                            }
                        }
                        String creatorUrl =creator.getAvatarUri();
                        if (creatorUrl !=null) {
                            Glide.with(meetCreat.getContext()).load(creatorUrl).into(meetCreat);
                        }
                        else {
                            Glide.with(meetCreat.getContext()).load(URI).into(meetCreat);
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
                User mUser = dataSnapshot.getValue(User.class);
                assert mUser!=null;
                String user_name=mUser.getName();
                if (user_name!=null) {
                    user.setName(user_name);
                }
                String user_uri=mUser.getAvatarUri();
                if (user_uri!=null) {
                    user.setAvatarUri(user_uri);
                }
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

                if(mUsers.size() > 0)mUsers.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    if (dataSnapshot.getKey().equals(uid) ) {
                        ref = dataSnapshot.getRef();
                        button.setText("Покинуть");
                    }
                    /*else if (!creatorUid.equals(uid)){
                        button.setText("Присоединиться");
                    }*/
                    User member =dataSnapshot.getValue(User.class);
                    assert member != null;
                    mUsers.add(member);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMeet.child(meetUid).child("members").addValueEventListener(memberListener);

    }

    @Override
    public void OnItemClick(int position) {
        User user = mUsers.get(position);
        Intent i = new Intent(MeetingActivity.this, ProfileUsersActivity.class);
        i.putExtra(Constant.MEETING_CREATOR_UID, user.getUid());
        startActivity(i);
    }
}