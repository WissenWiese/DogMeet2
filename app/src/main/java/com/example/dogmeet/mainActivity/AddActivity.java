package com.example.dogmeet.mainActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dogmeet.R;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {
    private EditText titleEditText, addressEditText, dateEditText, timeEditText, descriptionEditText, numberEditText;
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
        timeEditText=findViewById(R.id.editTime);
        descriptionEditText=findViewById(R.id.editDescription);
        numberEditText=findViewById(R.id.editNumberMember);
        addButton = findViewById(R.id.btnAdd);
        cancelButton = findViewById(R.id.btnCancel);
        database = FirebaseDatabase.getInstance();
        myMeet = database.getReference("meeting");
        users = database.getReference("Users");

        TextView meet_for=findViewById(R.id.size_dog_view);

        String[] size = { "Любых пород", "Мелких пород", "Средних пород", "Крупных пород"};

        Spinner size_spinner = findViewById(R.id.size_spinner);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, size);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        size_spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                String item = (String)parent.getItemAtPosition(position);
                meet_for.setText(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        size_spinner.setOnItemSelectedListener(itemSelectedListener);

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
                String timeText=timeEditText.getText().toString();
                String descriptionText=descriptionEditText.getText().toString();
                String numdeText=numberEditText.getText().toString();
                String tupeText=meet_for.getText().toString();
                Meeting meet = new Meeting();
                meet.setTitle(titleText);
                meet.setAddress(addressText);
                meet.setDate(dateText);
                meet.setCreatorUid(uid);
                meet.setCreator(creator);
                meet.setTime(timeText);
                meet.setDescription(descriptionText);
                meet.setNubmerMember(numdeText);
                meet.setTupeDog(tupeText);

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