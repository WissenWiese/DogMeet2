package com.example.dogmeet;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Query;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText addressEditText;
    private EditText dateEditText;
    private Button addButton, cancelButton;
    private FirebaseDatabase database;
    private DatabaseReference myMeet, users;
    private FirebaseAuth auth;
    String uid, creator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        titleEditText = findViewById(R.id.editTitle);
        addressEditText = findViewById(R.id.editPostalAddress);
        dateEditText = findViewById(R.id.editDate);
        addButton = findViewById(R.id.btnAdd);
        cancelButton = findViewById(R.id.btnCancel);
        database = FirebaseDatabase.getInstance();
        myMeet = database.getReference("meeting");
        users = database.getReference("Users");

        FirebaseUser cur_user = auth.getInstance().getCurrentUser();

        if(cur_user == null)
        {
            startActivity(new Intent(AddActivity.this, LoginActivity.class));
        } else {
            uid = cur_user.getUid();
        }

        getMeetCreator();

        dateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePickDlg(dateEditText);
                    return true;
                }

                return false;
            }
        });

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showDatePickDlg(dateEditText);
                }

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleText = titleEditText.getText().toString();
                String addressText = addressEditText.getText().toString();
                String dateText = dateEditText.getText().toString();
                Meeting meet = new Meeting();
                meet.setTitle(titleText);
                meet.setAddress(addressText);
                meet.setDate(dateText);
                meet.setCreatorUid(uid);
                meet.setCreator(creator);

                myMeet.push().setValue(meet);
                AddActivity.this.finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddActivity.this.finish();
            }
        });
    }

    protected void showDatePickDlg(EditText date) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear=monthOfYear+1;
                if (monthOfYear<10) {
                    date.setText(dayOfMonth + ".0" + monthOfYear + "." + year);
                }
                else{
                    date.setText(dayOfMonth + "." + monthOfYear + "." + year);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void getMeetCreator() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                creator=user.getName();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}