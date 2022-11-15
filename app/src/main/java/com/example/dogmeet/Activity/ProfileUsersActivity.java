package com.example.dogmeet.Activity;

import static com.example.dogmeet.Constant.URI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Chat.ChatActivity;
import com.example.dogmeet.Constant;
import com.example.dogmeet.Fragment.Map.MeetingMarkerAdapter;
import com.example.dogmeet.Meeting.MeetingActivity;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.Fragment.Profile.PetAdapter;
import com.example.dogmeet.mainActivity.AddActivity;
import com.example.dogmeet.mainActivity.LoginActivity;
import com.example.dogmeet.model.Meeting;
import com.example.dogmeet.model.Pet;
import com.example.dogmeet.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ProfileUsersActivity extends AppCompatActivity implements RecyclerViewInterface {
    private String uid, myUid;
    private ImageView avatar;
    private TextView bio, name, petText, meetText, phone, web;
    private Toolbar toolbar;
    private ArrayList<Pet> mPets;
    private RecyclerView recyclerView, recyclerView2;
    private PetAdapter petAdapter;
    private ArrayList<String> meetUidList;
    private ArrayList<Meeting> meetingArrayList;
    private MeetingMarkerAdapter meetingMarkerAdapter;
    private ConstraintLayout contact;
    private Boolean activeSubscription=false;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_users);

        toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileUsersActivity.this.finish();// возврат на предыдущий activity
            }
        });
        init();
        getIntentMain();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        MenuItem messageMenuItem = menu.findItem(R.id.action_message);
        messageMenuItem.setVisible(true);

        FirebaseUser cur_user = auth.getInstance().getCurrentUser();
        if(cur_user != null)
        {
            myUid = cur_user.getUid();
        }


        FirebaseDatabase.getInstance().getReference("Users")
                .child(myUid).child("subscription")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                MenuItem callOnMenuItem = menu.findItem(R.id.action_call);
                callOnMenuItem.setVisible(true);
                if (snapshot.child(uid).exists()) {
                    callOnMenuItem.setIcon(R.drawable.call_on);
                }
                else {
                    callOnMenuItem.setIcon(R.drawable.call_off);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_message:
                Intent i = new Intent(ProfileUsersActivity.this, ChatActivity.class);
                i.putExtra(Constant.USER_UID, uid);
                startActivity(i);
                return true;
            case R.id.action_call:
                if (activeSubscription){
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("MEET"+uid);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(myUid).child("subscription").child(uid).getRef().removeValue();
                    activeSubscription=false;
                    item.setIcon(R.drawable.call_off);
                }
                else {
                    FirebaseMessaging.getInstance().subscribeToTopic("MEET"+uid);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(myUid).child("subscription").child(uid).setValue("");
                    activeSubscription=true;
                    item.setIcon(R.drawable.call_on);
                }
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void init(){
        avatar=findViewById(R.id.chatAvatar);
        name=findViewById(R.id.text_name);
        bio=findViewById(R.id.text_about_me);
        petText=findViewById(R.id.text_my_pet);
        meetText=findViewById(R.id.textView4);

        contact=findViewById(R.id.contactText);
        phone=findViewById(R.id.textTelephone);
        web=findViewById(R.id.textWeb);

        recyclerView=findViewById(R.id.r_v_pets);

        mPets = new ArrayList<>();
        meetingArrayList=new ArrayList<>();
        meetUidList=new ArrayList<>();

        petAdapter= new PetAdapter(mPets, this, false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileUsersActivity.this,LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(petAdapter);

        recyclerView2=findViewById(R.id.r_v_meetings);
        recyclerView2.setHasFixedSize(true);

        meetingMarkerAdapter= new MeetingMarkerAdapter(meetingArrayList, this);

        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setLayoutManager(new LinearLayoutManager(ProfileUsersActivity.this,LinearLayoutManager.VERTICAL, false));
        recyclerView2.setAdapter(meetingMarkerAdapter);
    }

    private void getIntentMain(){
        Intent i = getIntent();
        if(i != null) {
            uid = i.getStringExtra(Constant.USER_UID);
            DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    name.setText(user.getName().toString());
                    bio.setText(user.getInfo());
                    String url = user.getAvatarUri();
                    if (url != null) {
                        Glide.with(avatar.getContext()).load(url).into(avatar);
                    } else {
                        Glide.with(avatar.getContext()).load(URI).into(avatar);
                    }
                    if (user.getPhone()!=null || user.getWeb()!=null){
                        contact.setVisibility(View.VISIBLE);
                        if (user.getPhone()!=null) {
                            phone.setVisibility(View.VISIBLE);
                            phone.setText(user.getPhone());
                        }
                        else  phone.setVisibility(View.INVISIBLE);
                        if (user.getWeb()!=null) {
                            web.setVisibility(View.VISIBLE);
                            web.setText(user.getWeb());
                        }
                        else web.setVisibility(View.INVISIBLE);
                    }
                    else {
                        contact.setVisibility(View.GONE);
                    }
                    if(meetUidList.size() > 0)meetUidList.clear();
                    for (DataSnapshot snapshot: dataSnapshot.child("Meeting").getChildren()){
                        String meetUid =snapshot.getKey();
                        if (meetUid!=null){
                            meetUidList.add(meetUid);
                        }
                    }
                    getMeeting();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference pets = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("pets");
            pets.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(mPets.size() > 0)mPets.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Pet pet =dataSnapshot.getValue(Pet.class);
                        if (pet!=null) {
                            petText.setVisibility(View.VISIBLE);
                            mPets.add(pet);
                        }
                        else {
                            petText.setVisibility(View.GONE);
                        }
                    }
                    petAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void OnItemClick(int position) {
        Meeting meeting;
        meeting=meetingArrayList.get(position);

        Intent i = new Intent(ProfileUsersActivity.this, MeetingActivity.class);
        i.putExtra(Constant.MEETING_UID, meeting.getUid());
        i.putExtra(Constant.MEETING_CREATOR_UID, meeting.getCreatorUid());
        i.putExtra(Constant.IS_COMMENT, false);
        i.putExtra(Constant.DATABASE, "meeting");
        startActivity(i);
    }

    @Override
    public void OnButtonClick(int position) {
    }

    public void getMeeting(){
        if (meetUidList.size()>0){
            meetText.setVisibility(View.VISIBLE);
            for (String meetingUid: meetUidList){
                DatabaseReference myMeet = FirebaseDatabase.getInstance().getReference("meeting").child(meetingUid);

                ValueEventListener meetListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        {
                            Meeting meeting = snapshot.getValue(Meeting.class);
                            if (meeting != null) {
                                meeting.setUid(snapshot.getKey());
                                meetingArrayList.add(meeting);
                            }

                        }
                        meetingMarkerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                myMeet.addValueEventListener(meetListener);
            }
        }
        else {
            meetText.setVisibility(View.GONE);
        }
    }
}