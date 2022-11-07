package com.example.dogmeet.Fragment.Map;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dogmeet.R;
import com.example.dogmeet.model.Doghanting;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;


public class DoghantingFragment extends Fragment {
    private View view;
    private ImageButton addPhoto, addPhotoFromGallery;
    private Button savePoint;
    private EditText messagePoint;
    private Doghanting doghanting;
    private static final int REQUEST_TAKE_PHOTO = 16;
    private final int PICK_IMAGE_REQUEST = 71;
    private ImageView imageView;
    private byte[] fileByte;
    private String urlImage;
    private Uri filePath;
    private Bundle extras;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Double latitude, longitude;


    public DoghantingFragment() {


    }

    public static DoghantingFragment newInstance(Double latitude, Double longitude) {
        DoghantingFragment fragment = new DoghantingFragment();
        Bundle args = new Bundle();
        args.putDouble("Latitude", latitude);
        args.putDouble("Longitude", longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latitude = getArguments().getDouble("Latitude", 0);
        longitude = getArguments().getDouble("Longitude", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_doghanting, container, false);

        addPhoto=view.findViewById(R.id.cameraBtn);
        addPhotoFromGallery=view.findViewById(R.id.galerea);
        savePoint=view.findViewById(R.id.saveBtn);

        imageView=view.findViewById(R.id.imageView4);

        messagePoint=view.findViewById(R.id.editDescription);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        AddDogHanter ();
        return view;
    }

    public void AddDogHanter (){
        doghanting =new Doghanting();

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try{
                    startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }
            }
        });

        addPhotoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        savePoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long date=new Date().getTime();

                if (latitude==null || longitude==null){
                    Toast.makeText(getContext(), "Укажите местоположение", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (imageView.getDrawable()==null){
                    Toast.makeText(getContext(), "Загруите фотографию", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!messagePoint.getFreezesText()) {
                    Toast.makeText(getContext(), "Добавте описание", Toast.LENGTH_SHORT).show();
                    return;
                }

                doghanting.setUser(FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid());
                doghanting.setLatitude(String.valueOf(latitude));
                doghanting.setLongitude(String.valueOf(longitude));
                doghanting.setMessage(messagePoint.getText().toString());
                doghanting.setTime(date);

                setmDoghanter(doghanting);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            extras = data.getExtras();
            Bitmap thumbnailBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(thumbnailBitmap);
        }
        else  if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setmDoghanter(Doghanting doghanting){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        UploadTask upload_image;

        StorageReference ref1 = storageReference.child("doghanter/"+ UUID.randomUUID().toString());

        if (filePath==null) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            fileByte = baos.toByteArray();
            upload_image= ref1.putBytes(fileByte);
        }
        else {
            upload_image=ref1.putFile(filePath);
        }


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
                return ref1.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    urlImage=downloadUri.toString();
                    doghanting.setImage(urlImage);
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("doghanter")
                            .push()
                            .setValue(doghanting);

                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}