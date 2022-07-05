package com.example.dogmeet.mainActivity;

import static com.example.dogmeet.Constant.URI;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.User;
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
import java.util.Calendar;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {
    private EditText titleEditText, addressEditText, dateEditText, timeEditText, descriptionEditText;
    private Button addButton, cancelButton, button_upload;
    private FirebaseDatabase database;
    private DatabaseReference myMeet, users;
    private FirebaseAuth auth;
    private String uid, creator, imageUrl;
    private int member_number;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setTitle("Создать встречу");

        titleEditText = findViewById(R.id.editTitle);
        addressEditText = findViewById(R.id.editPostalAddress);
        dateEditText = findViewById(R.id.editDate);
        timeEditText=findViewById(R.id.editTime);
        descriptionEditText=findViewById(R.id.editDescription);
        addButton = findViewById(R.id.btnAdd);
        cancelButton = findViewById(R.id.btnCancel);
        button_upload=findViewById(R.id.button_upload_photo);
        database = FirebaseDatabase.getInstance();
        myMeet = database.getReference("meeting");
        users = database.getReference("Users");
        imageView=findViewById(R.id.imageView3);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Glide.with(imageView.getContext()).load(URI).into(imageView);

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
                String tupeText=meet_for.getText().toString();
                member_number=0;

                Meeting meet = new Meeting();
                meet.setTitle(titleText);
                meet.setAddress(addressText);
                meet.setDate(dateText);
                meet.setCreatorUid(uid);
                meet.setCreator(creator);
                meet.setTime(timeText);
                meet.setDescription(descriptionText);
                meet.setTupeDog(tupeText);
                meet.setNumberMember(member_number);
                if (filePath!=null) {
                    uploadImage(meet);

                }
                else {
                    myMeet.push().setValue(meet);
                    AddActivity.this.finish();
                }


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddActivity.this.finish();
            }
        });

        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            button_upload.setText("Изображение загружено");
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Meeting meet) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();



            StorageReference ref = storageReference.child("meeting/"+UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Uri downloadUri = taskSnapshot.getUploadSessionUri();
                            meet.setUrlImage(downloadUri.toString());
                            myMeet.push().setValue(meet);
                            AddActivity.this.finish();
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

        }
    }
}