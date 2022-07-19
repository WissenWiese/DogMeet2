package com.example.dogmeet.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.Meeting;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditMeetingActivity extends AppCompatActivity {
    private String meetUid;
    private EditText titleEditText, addressEditText,
            dateEditText, timeEditText, descriptionEditText, numberEditText;
    private TextView meet_for;
    private Button caveButton, deleteButton;

    private FirebaseDatabase database;
    private DatabaseReference myMeet;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meeting);

        toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Редактировать мероприятие");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });

        titleEditText = findViewById(R.id.editTitle2);
        addressEditText = findViewById(R.id.editPostalAddress2);
        dateEditText = findViewById(R.id.editDate2);
        timeEditText=findViewById(R.id.editTime2);
        descriptionEditText=findViewById(R.id.editDescription2);
        caveButton = findViewById(R.id.btnSave);
        deleteButton = findViewById(R.id.btnDelete);


        database = FirebaseDatabase.getInstance();
        myMeet = database.getReference("meeting");

        getIntentMain();

        caveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleText = titleEditText.getText().toString();
                String addressText = addressEditText.getText().toString();
                String dateText = dateEditText.getText().toString();
                String timeText=timeEditText.getText().toString();
                String descriptionText=descriptionEditText.getText().toString();

                myMeet.child(meetUid).child("title").setValue(titleText);
                myMeet.child(meetUid).child("address").setValue(addressText);
                myMeet.child(meetUid).child("date").setValue(dateText);
                myMeet.child(meetUid).child("time").setValue(timeText);
                myMeet.child(meetUid).child("description").setValue(descriptionText);

                EditMeetingActivity.this.finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteWindow();
            }
        });
    }

    private void getIntentMain()
    {
        Intent i = getIntent();
        if(i != null)
        {
            meetUid=i.getStringExtra(Constant.MEETING_UID);
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref = database.child("meeting").child(meetUid);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Meeting meeting = dataSnapshot.getValue(Meeting.class);
                    if (meeting!=null){
                        titleEditText.setText(meeting.title);
                        dateEditText.setText(meeting.date);
                        addressEditText.setText(meeting.address);
                        timeEditText.setText(meeting.time);
                        descriptionEditText.setText(meeting.description);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void showDeleteWindow(){

        AlertDialog.Builder dialog=new AlertDialog.Builder(this);

        LayoutInflater inflator= LayoutInflater.from(this);
        dialog.setTitle("Вы дейсвительно хотите удалить встречу?");

        dialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref = database.child("meeting").child(meetUid);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeValue();
                            EditMeetingActivity.this.finish();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        dialog.show();
    }
}