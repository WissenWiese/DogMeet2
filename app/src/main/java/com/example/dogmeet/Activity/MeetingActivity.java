package com.example.dogmeet.Activity;

import static com.example.dogmeet.Constant.IS_COMMENT;
import static com.example.dogmeet.Constant.URI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Constant;
import com.example.dogmeet.Fragment.CommentsFragment;
import com.example.dogmeet.Fragment.ReviewFragment;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.Message;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class MeetingActivity extends AppCompatActivity{
    private TextView meetDate, meetAddress, meetTime;
    ImageView meetImageView;
    Toolbar toolbar;
    String creatorUid, uid, meetUid;
    private DatabaseReference myMeet;
    private Meeting meeting;
    TabLayout tabLayout;
    int numberComments;
    Boolean isComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingActivity.this.finish();// возврат на предыдущий activity
            }
        });

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");

        init();
        getIntentMain();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);

        boolean isCreator=creatorUid.equals(uid);

        MenuItem editMenuItem = menu.findItem(R.id.action_edit);
        editMenuItem.setVisible(isCreator);

        MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        saveMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(MeetingActivity.this, EditMeetingActivity.class);
                intent.putExtra(Constant.MEETING_UID, meetUid);
                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init(){
        meetDate = findViewById(R.id.meetDate);
        meetAddress = findViewById(R.id.meetAddress);
        meetTime=findViewById(R.id.meetTime);
        meetImageView=findViewById(R.id.meetImageView);

        tabLayout=findViewById(R.id.tabLayout);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        ReviewFragment reviewFragment=ReviewFragment.newInstance(meetUid, creatorUid);
                        replaceFragment(reviewFragment);
                        break;

                    case 1:
                        CommentsFragment сommentsFragment=CommentsFragment.newInstance(meetUid);
                        replaceFragment(сommentsFragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void getIntentMain(){
        Intent i = getIntent();
        if(i != null)
        {
            meetUid=i.getStringExtra(Constant.MEETING_UID);
            creatorUid=i.getStringExtra(Constant.MEETING_CREATOR_UID);
            isComment=i.getExtras().getBoolean(IS_COMMENT);
            if (meetUid!=null) {
                ValueEventListener meetingListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        meeting = snapshot.getValue(Meeting.class);
                        if (meeting != null) {
                            toolbar.setTitle(meeting.title);
                            meetDate.setText(meeting.date+",");
                            meetAddress.setText(meeting.address);
                            meetTime.setText(meeting.time);
                            numberComments=meeting.numberComments;
                            String url=meeting.urlImage;
                            if (url!=null){
                                Glide.with(meetImageView.getContext()).load(url).into(meetImageView);
                            }
                            else {
                                Glide.with(meetImageView.getContext()).load(URI).into(meetImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                myMeet.child(meetUid).addValueEventListener(meetingListener);

                if (!isComment) {
                    TabLayout.Tab tab=tabLayout.getTabAt(0);
                    assert tab != null;
                    tab.select();
                    ReviewFragment reviewFragment = ReviewFragment.newInstance(meetUid, creatorUid);
                    replaceFragment(reviewFragment);
                }
                else{
                    TabLayout.Tab tab=tabLayout.getTabAt(1);
                    assert tab != null;
                    tab.select();

                }
            }
        }
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_container, someFragment, meetUid).addToBackStack(null);
        transaction.commit();
    }

}