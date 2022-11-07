package com.example.dogmeet.Fragment.Profile;

import static android.app.Activity.RESULT_OK;
import static com.example.dogmeet.Constant.URI;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Constant;
import com.example.dogmeet.Fragment.Map.MeetingMarkerAdapter;
import com.example.dogmeet.Meeting.MeetingActivity;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.model.Meeting;
import com.example.dogmeet.model.Pet;
import com.example.dogmeet.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ProfileCreatorFragment extends Fragment implements RecyclerViewInterface{
    ImageButton buttonEdit, buttonSave, imageView;
    EditText about;
    DatabaseReference users, meetings;
    private final int PICK_IMAGE_REQUEST = 71;
    private View view;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    private ArrayList<Meeting> meetingArrayList;
    private ArrayList<String> meetingsUid;
    private RecyclerView recyclerView;
    private MeetingMarkerAdapter meetingMarkerAdapter;
    FirebaseAuth auth;


    public ProfileCreatorFragment(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        meetingArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        TextView bio=view.findViewById(R.id.text_name);
        buttonEdit=view.findViewById(R.id.editBtn);
        buttonSave=view.findViewById(R.id.saveBtn);
        imageView=view.findViewById(R.id.chatAvatar);
        about=view.findViewById(R.id.edit_about);
        about.setCursorVisible(false);
        about.setBackgroundColor(Color.TRANSPARENT);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        users = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());
        meetings=FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid()).child("Meeting");

        recyclerView=view.findViewById(R.id.r_v_meetings);
        recyclerView.setHasFixedSize(true);

        meetingMarkerAdapter= new MeetingMarkerAdapter(meetingArrayList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(meetingMarkerAdapter);

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                bio.setText(user.getName().toString());
                if (user.getInfo()!=null){
                    about.setText(user.getInfo());
                }
                if (user.getAvatarUri()!=null){
                    Glide.with(imageView.getContext()).load(user.getAvatarUri()).into(imageView);
                }
                else {
                    Glide.with(imageView.getContext()).load(URI).into(imageView);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        meetings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(meetingsUid.size() > 0) meetingsUid.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String meetingUid =dataSnapshot.getValue(String.class);
                    assert meetingUid != null;
                    meetingsUid.add(meetingUid);
                }
                getMeetingList(meetingsUid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about.setEnabled(true);
                buttonEdit.setTag("save");
                buttonEdit.setVisibility(View.INVISIBLE);
                buttonSave.setVisibility(View.VISIBLE);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (about!=null){
                    users.child("info").setValue(about.getText().toString());
                    about.setEnabled(false);
                    about.setCursorVisible(false);
                    about.setBackgroundColor(Color.TRANSPARENT);
                }
                buttonEdit.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.INVISIBLE);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageWindow();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            if(filePath != null)
            {
                uploadImage();
            }
        }
    }

    @Override
    public void OnItemClick(int position) {
        Meeting meeting=meetingArrayList.get(position);
        Intent i = new Intent(getContext(), MeetingActivity.class);
        i.putExtra(Constant.MEETING_UID, meeting.getUid());
        i.putExtra(Constant.MEETING_CREATOR_UID, meeting.getCreatorUid());
        i.putExtra(Constant.IS_COMMENT, false);
        i.putExtra(Constant.DATABASE, "meeting");
        startActivity(i);
    }

    @Override
    public void OnButtonClick(int position) {

    }

    private void showImageWindow(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());

        final String[] editPhoto={"Загрузить", "Удалить"};

        StorageReference ref;
        DatabaseReference databaseReference;

        ref = storageReference.child(auth.getUid()).child("avatar");
        databaseReference=users.child("avatarUri");

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
                        databaseReference.removeValue();
                        ref.delete();
                        break;
                }
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private void uploadImage() {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref;
            DatabaseReference databaseReference;

            ref = storageReference.child(auth.getUid()).child("avatar");
            databaseReference=users.child("avatarUri");

            UploadTask upload_image=ref.putFile(filePath);
            upload_image
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        databaseReference.setValue(downloadUri.toString());

                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getMeetingList (ArrayList<String> meetigsUid){

        for (String meetinUid :meetigsUid) {

            DatabaseReference myMeet = FirebaseDatabase.getInstance().getReference("meeting").child(meetinUid);

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

}