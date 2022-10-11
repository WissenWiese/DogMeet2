package com.example.dogmeet.Meeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.model.Meeting;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditMeetingActivity extends AppCompatActivity {
    private String meetUid;
    private EditText titleEditText, addressEditText,
            dateEditText, timeEditText, descriptionEditText;
    private Button deleteButton;
    private ImageButton avatarButton;
    private DatabaseReference myMeet;
    private Toolbar toolbar;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;
    CardView imageWindow;


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

        titleEditText = findViewById(R.id.editTitleEdit);
        addressEditText = findViewById(R.id.editPostalAddressEdit);
        dateEditText = findViewById(R.id.editDateEdit);
        timeEditText=findViewById(R.id.editTimeEdit);
        descriptionEditText=findViewById(R.id.editDescriptionEdit);
        deleteButton = findViewById(R.id.btnDelete);
        avatarButton=findViewById(R.id.imageEdit);

        imageWindow=findViewById(R.id.imageWindow);

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getIntentMain();


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteWindow();
            }
        });

        avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageWindow();
            }
        });
    }

    private void getIntentMain()
    {
        Intent i = getIntent();
        if(i != null)
        {
            meetUid=i.getStringExtra(Constant.MEETING_UID);
            myMeet.child(meetUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Meeting meeting = dataSnapshot.getValue(Meeting.class);
                    if (meeting!=null){
                        titleEditText.setText(meeting.title);
                        dateEditText.setText(DateFormat.format("dd.MM.yyyy", meeting.date));
                        addressEditText.setText(meeting.address);
                        timeEditText.setText(DateFormat.format("HH:mm", meeting.date));
                        descriptionEditText.setText(meeting.description);
                        String url=meeting.urlImage;
                        if (url!=null){
                            Glide.with(avatarButton.getContext()).load(url).into(avatarButton);
                        }
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
                myMeet.child(meetUid).removeValue();
                storageReference.child("meeting/").child(meetUid).delete();
                dialogInterface.dismiss();
                EditMeetingActivity.this.finish();
            }
        });

        dialog.show();
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
        String titleText = titleEditText.getText().toString();
        String addressText = addressEditText.getText().toString();
        String dateText = dateEditText.getText().toString();
        String timeText=timeEditText.getText().toString();
        String descriptionText=descriptionEditText.getText().toString();

        long dateMeet=0;

        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            Date d = f.parse(dateText+" "+timeText);
            dateMeet = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        myMeet.child(meetUid).child("title").setValue(titleText);
        myMeet.child(meetUid).child("address").setValue(addressText);
        myMeet.child(meetUid).child("date").setValue(dateMeet);
        myMeet.child(meetUid).child("description").setValue(descriptionText);
        if (filePath!=null) {
            uploadImage();
        }
    }

    private void showImageWindow(){
        androidx.appcompat.app.AlertDialog.Builder dialog=new androidx.appcompat.app.AlertDialog.Builder(EditMeetingActivity.this);

        final String[] editPhoto={"Загрузить", "Удалить"};

        dialog.setItems(editPhoto, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (editPhoto[i]){
                    case "Загрузить":
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        break;
                    case "Удалить":
                        myMeet.child(meetUid).child("urlImage").removeValue();
                        storageReference.child("meeting/").child(meetUid).delete();
                        break;
                }
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
        }
    }

    private void uploadImage() {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("meeting/"+ meetUid);
            UploadTask upload_image=ref.putFile(filePath);
            upload_image
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditMeetingActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditMeetingActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        myMeet.child(meetUid).child("urlImage").setValue(downloadUri.toString());

                    } else {
                        Toast.makeText(EditMeetingActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}