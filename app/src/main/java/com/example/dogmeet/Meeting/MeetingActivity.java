package com.example.dogmeet.Meeting;

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
import android.text.format.DateFormat;
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
import com.example.dogmeet.R;
import com.example.dogmeet.model.Message;
import com.example.dogmeet.model.Meeting;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class MeetingActivity extends AppCompatActivity implements CommentsFragment.OnDataPass {
    private TextView meetDate, meetAddress, nameAnswer;
    private ImageView meetImageView;
    private Toolbar toolbar;
    private String creatorUid, uid, meetUid, database, uidComment;
    private DatabaseReference myMeet, comments;
    private Meeting meeting;
    private TabLayout tabLayout;
    int numberComments, numberAnswers;
    private Boolean isComment, isAnswer=false;
    private ImageButton spendMessage, closeBtn;
    private EditText editComment;
    AppBarLayout appBarLayout;
    CardView spendComments, answerName;

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

        appBarLayout=findViewById(R.id.appbar);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        init();
        getIntentMain();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);

        boolean isCreator=creatorUid.equals(uid);
        if (database.equals("archive")){
            isCreator=false;
        }

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
        meetImageView=findViewById(R.id.meetImageView);

        tabLayout=findViewById(R.id.tabLayout);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        ReviewFragment reviewFragment=ReviewFragment.newInstance(meetUid, creatorUid, database);
                        replaceFragment(reviewFragment);
                        spendComments.animate().translationY(0);
                        answerName.animate().translationY(0);
                        answerName.setVisibility(View.INVISIBLE);
                        break;

                    case 1:
                        CommentsFragment сommentsFragment= CommentsFragment.newInstance(meetUid, creatorUid, database);
                        replaceFragment(сommentsFragment);
                        spendComments.animate().translationY(-getResources().getDimension(R.dimen.standard_50));
                        answerName.animate().translationY(-getResources().getDimension(R.dimen.standard_50));
                        spendComments();
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

        editComment =findViewById(R.id.editMessage);
        spendMessage=findViewById(R.id.imageButton);
        spendComments=findViewById(R.id.setMessage);
        answerName=findViewById(R.id.answer);
        closeBtn=findViewById(R.id.closeAnswer);
        nameAnswer=findViewById(R.id.answerName);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAnswer=false;
                answerName.setVisibility(View.INVISIBLE);
                editComment.setText(null);
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
            database=i.getStringExtra(Constant.DATABASE);
            if (meetUid!=null) {
                if (database.equals("meeting")){
                    myMeet = FirebaseDatabase.getInstance().getReference("meeting");
                }
                else {
                    myMeet= FirebaseDatabase.getInstance().getReference("archive").child("meeting");
                }
                ValueEventListener meetingListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        meeting = snapshot.getValue(Meeting.class);
                        if (meeting != null) {
                            toolbar.setTitle(meeting.title);
                            meetDate.setText(DateFormat.format("dd.MM, HH:mm", meeting.getDate()));
                            meetAddress.setText(meeting.address);
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
                    ReviewFragment reviewFragment=ReviewFragment.newInstance(meetUid, creatorUid, database);
                    replaceFragment(reviewFragment);
                    spendComments.animate().translationY(0);
                }
                else{
                    TabLayout.Tab tab=tabLayout.getTabAt(1);
                    assert tab != null;
                    tab.select();
                    spendComments.animate().translationY(-getResources().getDimension(R.dimen.standard_50));
                    spendComments();
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

    public void spendComments(){
        Message message =new Message();

        spendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long date=new Date().getTime();

                if(!editComment.getFreezesText()) {
                    Toast.makeText(MeetingActivity.this, "Введите комментарий", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isAnswer){
                    comments = FirebaseDatabase.getInstance().getReference()
                            .child("meeting").child(meetUid).child("comments")
                            .child(uidComment).child("answers");
                    numberAnswers=numberAnswers+1;
                }
                else {

                    comments = FirebaseDatabase.getInstance().getReference()
                            .child("meeting").child(meetUid).child("comments");
                    numberAnswers=0;
                }

                message.setUser(FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid());
                message.setTime(date);
                message.setMessage(editComment.getText().toString());
                comments.push().setValue(message);

                int numberComments1=numberComments+1;
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("meeting")
                        .child(meetUid)
                        .child("numberComments")
                        .setValue(numberComments1);

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("meeting")
                        .child(meetUid)
                        .child("comments")
                        .child("numberAnswers")
                        .setValue(numberAnswers);
                editComment.setText(null);
                isAnswer=false;
                answerName.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onDataPass(String name, String uidComment, Boolean isAnswer, int numberAnswers) {
        editComment.setText(name+",");
        this.uidComment=uidComment;
        this.isAnswer=isAnswer;
        this.numberAnswers=numberAnswers;
        if (isAnswer){
            answerName.setVisibility(View.VISIBLE);
            nameAnswer.setText(name);
        }
    }
}