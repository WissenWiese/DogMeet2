package com.example.dogmeet.mainActivity;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dogmeet.R;
import com.example.dogmeet.model.Meeting;
import com.example.dogmeet.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {
    private EditText titleEditText, dateEditText, timeEditText, descriptionEditText;
    private DatabaseReference myMeet, users;
    private Spinner typeOfDogsSpinner;
    private String typeOfDogs;
    private FirebaseAuth auth;
    private String uid, meetUid;
    private int member_number, comments_number;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private ImageButton uploadPhoto;
    FirebaseStorage storage;
    StorageReference storageReference;
    User creator;
    Toolbar toolbar;
    AutoCompleteTextView addressEditText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        toolbar = findViewById(R.id.toolbar_add_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Создать встречу");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });
        creator=new User();

        titleEditText = findViewById(R.id.editName);
        dateEditText = findViewById(R.id.editDate);
        timeEditText =findViewById(R.id.editTime);
        descriptionEditText=findViewById(R.id.editMessage);

        uploadPhoto=findViewById(R.id.uploadPhoto);

        FirebaseUser cur_user = auth.getInstance().getCurrentUser();

        if(cur_user == null)
        {
            startActivity(new Intent(AddActivity.this, LoginActivity.class));
        } else {
            uid = cur_user.getUid();
        }

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");
        users = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getMeetCreator();

        dateEditText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
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

        timeEditText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    showTimePickDlg(timeEditText);
                    return true;
                }

                return false;
            }
        });

        timeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showTimePickDlg(timeEditText);
                }

            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        addressEditText = findViewById(R.id.editBreed);

        String[] address = getResources().getStringArray(R.array.address);
        List<String> addressList = Arrays.asList(address);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, addressList);
        addressEditText.setAdapter(adapter);

        ArrayAdapter<?> adapterType =
                ArrayAdapter.createFromResource(this, R.array.typeOgDogs,
                        android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfDogsSpinner = findViewById(R.id.editTypeOfDogs);
        typeOfDogsSpinner.setAdapter(adapterType);

        typeOfDogsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getItemAtPosition(i).toString()){
                    case "Любые":
                        typeOfDogs="Любые";
                        break;
                    case "Крупные":
                        typeOfDogs="Крупные";
                        break;
                    case "Средние":
                        typeOfDogs="Средние";
                        break;
                    case "Мелкие":
                        typeOfDogs="Мелкие";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    protected void showTimePickDlg(EditText time){
        // получаем текущее время
        final Calendar cal = Calendar.getInstance();
        int mHour = cal.get(Calendar.HOUR_OF_DAY);
        int mMinute = cal.get(Calendar.MINUTE);



        // инициализируем диалог выбора времени текущими значениями
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        view.setIs24HourView(true);
                        if (hourOfDay<10 && minute<10){
                            time.setText("0"+hourOfDay + ":0" + minute);
                        }
                        else if (hourOfDay>=10 && minute<10){
                            time.setText(hourOfDay + ":0" + minute);
                        }
                        else if (hourOfDay<10 && minute>=10){
                            time.setText("0"+hourOfDay + ":" + minute);
                        }
                        else if (hourOfDay>=10 && minute>=10){
                            time.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    public void getMeetCreator() {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user!=null;
                String creator_name=user.getName();
                if (creator_name!=null) {
                    creator.setName(creator_name);
                }
                String creator_url=user.getAvatarUri();
                if (creator_url!=null) {
                    creator.setAvatarUri(creator_url);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadPhoto.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Meeting meet, String meetUid) {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("meeting/"+meetUid);
            UploadTask upload_image=ref.putFile(filePath);
            upload_image
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
            Task<Uri> urlTask = upload_image.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        meet.setUrlImage(downloadUri.toString());
                        myMeet.child(meetUid).setValue(meet);
                        users.child("Meeting").push().setValue(meetUid);
                        AddActivity.this.finish();

                    } else {
                        Toast.makeText(AddActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);

        MenuItem editMenuItem = menu.findItem(R.id.action_edit);
        editMenuItem.setVisible(false);

        MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        saveMenuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveEdit();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveEdit(){
        if(TextUtils.isEmpty(titleEditText.getText().toString())) {
            Toast.makeText(AddActivity.this, "Введите название", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(AddActivity.this, "Введите адрес", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(dateEditText.getText().toString())) {
            Toast.makeText(AddActivity.this, "Введите дату", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(timeEditText.getText().toString())) {
            Toast.makeText(AddActivity.this, "Введите время", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(descriptionEditText.getText().toString())) {
            Toast.makeText(AddActivity.this, "Введите описание", Toast.LENGTH_SHORT).show();
            return;
        }

        meetUid=UUID.randomUUID().toString();
        String titleText = titleEditText.getText().toString();
        String addressText = addressEditText.getText().toString();
        String dateText = dateEditText.getText().toString();
        String timeText= timeEditText.getText().toString();
        String descriptionText=descriptionEditText.getText().toString();
        member_number=0;
        comments_number=0;

        long dateMeet=0;

        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            Date d = f.parse(dateText+" "+timeText);
            dateMeet = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Meeting meet = new Meeting();
        meet.setTitle(titleText);
        meet.setAddress(addressText);
        meet.setDate(dateMeet);
        meet.setCreatorUid(uid);
        meet.setTypeOfDogs(typeOfDogs);
        meet.setDescription(descriptionText);
        meet.setNumberMember(member_number);
        if (filePath!=null) {
            uploadImage(meet, meetUid);

        }
        else {
            myMeet.child(meetUid).setValue(meet);
            AddActivity.this.finish();
        }

    }
}