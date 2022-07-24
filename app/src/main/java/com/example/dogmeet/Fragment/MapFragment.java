package com.example.dogmeet.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Activity.EditMeetingActivity;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.Doghanter;
import com.example.dogmeet.entity.Meeting;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MapFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference myMeet, mDoghanter;
    private GoogleMap mMap;
    private View view;
    private FloatingActionButton fab, fab1, fab2;
    private boolean isFABOpen;
    private CardView cardView, wolk;
    private ImageButton closeBtn,closeBtn2, addPhoto;
    private Button savePoint;
    private EditText messagePoint;
    Marker dMarker;
    private Doghanter doghanter;
    private static final int REQUEST_TAKE_PHOTO = 16;
    private ImageView imageView;
    private byte[] fileByte;
    private FirebaseAuth auth;
    private String urlImage, uidCreator;
    Bundle extras;
    FirebaseStorage storage;
    StorageReference storageReference;
    Boolean onClickMap;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            LatLng ekb = new LatLng(56.839104, 60.60825);
            mMap.moveCamera(CameraUpdateFactory.zoomTo(12.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ekb));

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {

                @Override
                public boolean onMarkerClick(Marker arg0) {
                    if (arg0.getTitle().equals("doghanter")){
                        AlertDialog.Builder doghanter_dialog=new AlertDialog.Builder(getActivity());
                        LayoutInflater inflator= LayoutInflater.from(getActivity());
                        View doghanter_window=inflator.inflate(R.layout.window_doghanter, null);
                        doghanter_dialog.setView(doghanter_window);

                        ImageView photo=doghanter_window.findViewById(R.id.photoDoghanter);
                        TextView message=doghanter_window.findViewById(R.id.messegeDoghanter);
                        TextView date=doghanter_window.findViewById(R.id.dateCreate);


                        mDoghanter=FirebaseDatabase.getInstance().getReference("doghanter").child(arg0.getSnippet());

                        ValueEventListener dListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                {
                                    Doghanter doghanter=snapshot.getValue(Doghanter.class);
                                    if (doghanter!=null){
                                        Glide.with(photo.getContext()).load(doghanter.getImage()).into(photo);
                                        message.setText(doghanter.getMessage());
                                        date.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", doghanter.getTime()));
                                        uidCreator=doghanter.getUser();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        doghanter_dialog.setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (uidCreator.equals(FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getUid())){
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("doghanter").child(arg0.getSnippet());
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            dataSnapshot.getRef().removeValue();
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                    arg0.remove();
                                }
                            }
                        });
                        mDoghanter.addValueEventListener(dListener);

                        doghanter_dialog.show();
                    }
                    return true;
                }
            });
            
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_map, container, false);

        fab=view.findViewById(R.id.floatingActionButton);
        fab1=view.findViewById(R.id.floatingActionButton2);
        fab2=view.findViewById(R.id.floatingActionButton3);

        cardView=view.findViewById(R.id.createPoint);
        addPhoto=view.findViewById(R.id.cameraBtn);
        savePoint=view.findViewById(R.id.saveBtn);

        wolk=view.findViewById(R.id.wolk);

        closeBtn=view.findViewById(R.id.closeBtn);
        closeBtn2=view.findViewById(R.id.closeBtn2);

        imageView=view.findViewById(R.id.imageView4);

        messagePoint=view.findViewById(R.id.editMessage);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                } else{
                    closeFABMenu();
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                liveFABMenu();

                cardView.animate().translationY(-getResources().getDimension(R.dimen.standard_220));

                AddDogHanter();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                liveFABMenu();

                wolk.animate().translationY(-getResources().getDimension(R.dimen.standard_150));
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView.animate().translationY(0);
                closeFABMenu();
                Clean();
                onClickMap=false;

            }
        });

        closeBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wolk.animate().translationY(0);
                closeFABMenu();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        database = FirebaseDatabase.getInstance();
        myMeet = database.getReference("meeting");

        //loadingDoghanter();
    }

    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_130));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab.animate().translationY(0);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
    }

    private void liveFABMenu(){
        fab.animate().translationY(getResources().getDimension(R.dimen.standard_100));
        fab1.animate().translationY(getResources().getDimension(R.dimen.standard_100));
        fab2.animate().translationY(getResources().getDimension(R.dimen.standard_100));
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    
    public void AddDogHanter (){
        doghanter=new Doghanter();
        onClickMap =true;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if (onClickMap){
                    if (dMarker !=null){
                        dMarker.remove();
                    }

                    dMarker =mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true));
                }
            }
        });

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

        savePoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long date=new Date().getTime();

                if (dMarker==null){
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

                doghanter.setUser(FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid());
                doghanter.setLatitude(String.valueOf(dMarker.getPosition().latitude));
                doghanter.setLongitude(String.valueOf(dMarker.getPosition().longitude));
                doghanter.setMessage(messagePoint.getText().toString());
                doghanter.setTime(date);

                setmDoghanter(doghanter);
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
    }

    public void setmDoghanter(Doghanter doghanter){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        fileByte=baos.toByteArray();

        StorageReference ref1 = storageReference.child("doghanter/"+ UUID.randomUUID().toString());

        UploadTask upload_image= ref1.putBytes(fileByte);
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
                    doghanter.setImage(urlImage);
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("doghanter")
                            .push()
                            .setValue(doghanter);
                    Clean();

                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Clean(){
        if (dMarker!=null){
            dMarker.remove();
        }
        messagePoint.setText(null);
        imageView.setImageResource(0);

    }

    public void loadingDoghanter(){
        mDoghanter=FirebaseDatabase.getInstance().getReference("doghanter");

        ValueEventListener dListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Doghanter doghanter=dataSnapshot.getValue(Doghanter.class);
                    if (doghanter!=null) {
                        double latitude = Double.parseDouble(doghanter.getLatitude());
                        double longitude = Double.parseDouble(doghanter.getLongitude());
                        LatLng latLng = new LatLng(latitude, longitude);
                        if (mMap!=null){
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .draggable(true)
                                    .title("doghanter")
                                    .snippet(dataSnapshot.getKey())
                                    /*.icon(BitmapDescriptorFactory.fromResource(R.drawable.doghanter_marker))
                                    .flat(true)*/);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDoghanter.addValueEventListener(dListener);
    }
}