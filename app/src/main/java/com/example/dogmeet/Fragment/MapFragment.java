package com.example.dogmeet.Fragment;


import static android.app.Activity.RESULT_OK;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Activity.MeetingActivity;
import com.example.dogmeet.Activity.ProfileUsersActivity;
import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.adapter.MeetingMarkerAdapter;
import com.example.dogmeet.entity.Doghanting;
import com.example.dogmeet.entity.MarkerMeet;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.User;
import com.example.dogmeet.entity.Walker;
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
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MapFragment extends Fragment  implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, MapboxMap.OnMarkerClickListener, RecyclerViewInterface {


    private MapView mapView;
    private MapboxMap mapboxMap;
    private View view;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private DatabaseReference myMeet, mDoghanter, walkers, users;
    private ArrayList<MarkerMeet> pointMeet, doghanterPoint, usersPoint, allPoint;
    Boolean isNewAddress, onClickMap, isFABOpen=false, isWalk=false;
    private FloatingActionButton fab, fab1, fab2;
    private CardView cardView, walk;
    private ImageButton closeBtn,closeBtn2, addPhoto, addPhotoFromGalerea;
    private Button savePoint, walkAction;
    private EditText messagePoint;
    private Doghanting doghanting;
    private static final int REQUEST_TAKE_PHOTO = 16;
    private final int PICK_IMAGE_REQUEST = 71;
    private ImageView imageView;
    private byte[] fileByte;
    private String urlImage, uidCreator="";
    Bundle extras;
    FirebaseStorage storage;
    StorageReference storageReference;
    LatLng point;
    Marker addingMarker, userMarker;
    ArrayList<Meeting> meetingArrayList;
    Walker walker;
    private Uri filePath;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));
        getData();
        view=inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        fab=view.findViewById(R.id.floatingActionButton);
        fab1=view.findViewById(R.id.floatingActionButton2);
        fab2=view.findViewById(R.id.floatingActionButton3);

        cardView=view.findViewById(R.id.createPoint);
        addPhoto=view.findViewById(R.id.cameraBtn);
        addPhotoFromGalerea=view.findViewById(R.id.galerea);

        savePoint=view.findViewById(R.id.saveBtn);

        walk =view.findViewById(R.id.wolk);
        walkAction=view.findViewById(R.id.actionWalk);

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
                walk.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
                onClickMap=true;
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
                walk.animate().translationY(0);
                closeFABMenu();
                if (addingMarker!=null) {
                    addingMarker.remove();
                }
                onClickMap=false;
            }
        });

        startWalk();

        return view;
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        onClickMap=false;
        this.mapboxMap=mapboxMap;
        mapboxMap.setStyle(getString(com.mapbox.services.android.navigation.ui.v5.R.string.navigation_guidance_day), style -> {
            enableLocationComponent(style);
            addDestinationIconSymbolLayer(style);
            mapboxMap.addOnMapClickListener(this);
            mapboxMap.setOnMarkerClickListener(this);

        });



        for (MarkerMeet markerMeet: pointMeet){
            mapboxMap.addMarker(new MarkerOptions()
                    .setPosition(markerMeet.getPoint())
                    .setTitle(markerMeet.getTipe())
                    .setSnippet(markerMeet.getMeetsUid())
                    .setIcon(IconFactory.recreate("meeting_ic",
                            BitmapFactory.decodeResource( this.getResources(), com.mapbox.mapboxsdk.R.drawable.mapbox_logo_helmet))));
        }
        loadingDoghanter(mapboxMap);
        getWalkers(mapboxMap);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        if (onClickMap) {
            this.point = point;

            if (addingMarker !=null){
                addingMarker.remove();
            }

            addingMarker =mapboxMap.addMarker(new MarkerOptions()
                    .setPosition(point));

        }
            return onClickMap;
    }

    private void addDestinationIconSymbolLayer(com.mapbox.mapboxsdk.maps.Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(),
                com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);

        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true));
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent(com.mapbox.mapboxsdk.maps.Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(getContext(), loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);

            //Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            //Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_SHORT).show();
        }
    }

    public void getData(){
        myMeet = FirebaseDatabase.getInstance().getReference("meeting");
        pointMeet =new ArrayList<>();

        ValueEventListener meetListener = new ValueEventListener()  {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (pointMeet.size()>0) pointMeet.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    isNewAddress=true;
                    Meeting meeting = dataSnapshot.getValue(Meeting.class);
                    LatLng point = getLocationFromAddress(getContext(), meeting.address);
                    MarkerMeet markerMeet = new MarkerMeet();
                    markerMeet.setAddress(meeting.address);
                    markerMeet.setMeetsUid(dataSnapshot.getKey());
                    markerMeet.setTipe("meeting");
                    markerMeet.setPoint(point);
                    if (pointMeet.size() > 0) {
                        for (MarkerMeet address1 : pointMeet) {
                            if (address1.getAddress().equals(meeting.address)) {
                                String newAddress = address1.getMeetsUid() + ";" + dataSnapshot.getKey();
                                address1.setMeetsUid(newAddress);
                                isNewAddress=false;
                            }
                        }
                    }

                    if (isNewAddress) pointMeet.add(markerMeet);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        myMeet.addValueEventListener(meetListener);

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        //Point p1 = null;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            //p1 = Point.fromLngLat(location.getLatitude(), location.getLongitude() );
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
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

    public void AddDogHanter (){
        doghanting =new Doghanting();
        onClickMap =true;

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

        addPhotoFromGalerea.setOnClickListener(new View.OnClickListener() {
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

                if (point==null){
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
                doghanting.setLatitude(String.valueOf(point.getLatitude()));
                doghanting.setLongitude(String.valueOf(point.getLongitude()));
                doghanting.setMessage(messagePoint.getText().toString());
                doghanting.setTime(date);

                setmDoghanter(doghanting);
            }
        });
    }

    public void startWalk(){

        walkAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (walkAction.getText().equals("Начать прогулку")){
                    walker=new Walker();

                    /*if (point == null) {
                        Toast.makeText(getContext(), "Укажите местоположение", Toast.LENGTH_SHORT).show();
                        return;
                    }*/
                    walker.setLatitude(String.valueOf(mapboxMap
                                    .getLocationComponent()
                                    .getLastKnownLocation()
                                    .getLatitude()));
                    walker.setLongitude(String.valueOf(mapboxMap
                            .getLocationComponent()
                            .getLastKnownLocation()
                            .getLongitude()));

                    users = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid());

                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            assert user!=null;
                            walker.setUserName(user.getName());
                            walker.setUserUri(user.getAvatarUri());
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("walker")
                                    .child(dataSnapshot.getKey())
                                    .setValue(walker).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                onClickMap=false;
                                                walkAction.setText("Завершить прогулку");
                                            }
                                        }
                                    });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else if (walkAction.getText().equals("Завершить прогулку")){
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("walker")
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        walkAction.setText("Начать прогулку");
                                        onClickMap=true;
                                    }
                                }
                            });
                }
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
                    Clean();

                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Clean(){
        if (addingMarker!=null){
            addingMarker.remove();
        }

        messagePoint.setText(null);
        imageView.setImageResource(0);

    }

    public void loadingDoghanter(MapboxMap mapboxMap){
        mDoghanter=FirebaseDatabase.getInstance().getReference("doghanter");
        doghanterPoint=new ArrayList<>();

        ValueEventListener dListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (doghanterPoint.size()>0) doghanterPoint.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Doghanting doghanting =dataSnapshot.getValue(Doghanting.class);
                    if (doghanting !=null) {
                        double latitude = Double.parseDouble(doghanting.getLatitude());
                        double longitude = Double.parseDouble(doghanting.getLongitude());
                        LatLng latLng = new LatLng(latitude, longitude);
                        mapboxMap.addMarker(new MarkerOptions()
                                .setPosition(latLng)
                                .setTitle("doghanter")
                                .setSnippet(dataSnapshot.getKey())
                                .setIcon(IconFactory.recreate("doghanter_ic",
                                        BitmapFactory.decodeResource( getResources(), com.mapbox.services.android.navigation.ui.v5.R.drawable.map_marker_dark))));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDoghanter.addValueEventListener(dListener);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.getTitle().equals("doghanter")){
            doghanterMarker(marker);
        }
        else if (marker.getTitle().equals("meeting")){
            meetingMarker(marker);
        }
        else if (marker.getTitle().equals("walker")){
            wolkersMarker(marker);
        }
        return true;
    }

    public void doghanterMarker(Marker marker){
        AlertDialog.Builder doghanter_dialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflator= LayoutInflater.from(getActivity());
        View doghanter_window=inflator.inflate(R.layout.window_doghanter, null);
        doghanter_dialog.setView(doghanter_window);

        ImageView photo=doghanter_window.findViewById(R.id.photoWalker);
        TextView message=doghanter_window.findViewById(R.id.nameWalker);
        TextView date=doghanter_window.findViewById(R.id.dateCreate);


        mDoghanter=FirebaseDatabase.getInstance().getReference("doghanter").child(marker.getSnippet());

        ValueEventListener dListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                {
                    Doghanting doghanting =snapshot.getValue(Doghanting.class);
                    if (doghanting !=null){
                        Glide.with(photo.getContext()).load(doghanting.getImage()).into(photo);
                        message.setText(doghanting.getMessage());
                        date.setText(DateFormat.format("dd-MM (HH:mm:ss)", doghanting.getTime()));
                        uidCreator= doghanting.getUser();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        if (uidCreator.equals(FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid())) {
            doghanter_dialog.setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("doghanter").child(marker.getSnippet());
                    ref.removeValue();
                    marker.remove();
                }
            });
        }

        mDoghanter.addValueEventListener(dListener);


        doghanter_dialog.show();
    }

    public void meetingMarker(Marker marker){
        AlertDialog.Builder meeting_dialog =new AlertDialog.Builder(getActivity());
        LayoutInflater inflator= LayoutInflater.from(getActivity());
        View meet_window =inflator.inflate(R.layout.meet_window, null);
        meeting_dialog.setView(meet_window);

        RecyclerView recyclerView = meet_window.findViewById(R.id.markerMeetingRV);
        meetingArrayList=new ArrayList<>();
        MeetingMarkerAdapter meetingMarkerAdapter= new MeetingMarkerAdapter(meetingArrayList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(meetingMarkerAdapter);

        String[] meetigsUid=marker.getSnippet().split(";");

        for (String meetinUid :meetigsUid) {

            myMeet = FirebaseDatabase.getInstance().getReference("meeting").child(meetinUid);

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

        meeting_dialog.show();
    }

    public void wolkersMarker(Marker marker){
        AlertDialog.Builder user_dialog =new AlertDialog.Builder(getActivity());
        LayoutInflater inflator= LayoutInflater.from(getActivity());
        View user_window =inflator.inflate(R.layout.window_walker, null);
        user_dialog.setView(user_window);

        ImageView photo= user_window.findViewById(R.id.photoWalker);
        TextView name= user_window.findViewById(R.id.nameWalker);
        Button chat=user_window.findViewById(R.id.buttonMessage);


        walkers=FirebaseDatabase.getInstance().getReference("walker").child(marker.getSnippet());

        ValueEventListener dListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                {
                    Walker walker=snapshot.getValue(Walker.class);
                    if (walker!=null){
                        Glide.with(photo.getContext()).load(walker.getUserUri()).into(photo);
                        name.setText(walker.getUserName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        walkers.addValueEventListener(dListener);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ProfileUsersActivity.class);
                i.putExtra(Constant.USER_UID, marker.getSnippet());
                startActivity(i);
            }
        });
 
        user_dialog.show();
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

    public void getWalkers(MapboxMap mapboxMap){
        walkers=FirebaseDatabase.getInstance().getReference("walker");
        usersPoint=new ArrayList<>();

        ValueEventListener walkerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (usersPoint.size()>0) usersPoint.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Walker walker=dataSnapshot.getValue(Walker.class);
                    if (walker!=null) {
                        double latitude = Double.parseDouble(walker.getLatitude());
                        double longitude = Double.parseDouble(walker.getLongitude());
                        LatLng latLng = new LatLng(latitude, longitude);
                        if (dataSnapshot.getKey().equals(FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getUid())){

                            walkAction.setText("Завершить прогулку");
                            liveFABMenu();
                            walk.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
                        }
                        else {
                            mapboxMap.addMarker(new MarkerOptions()
                                    .setPosition(latLng)
                                    .setTitle("walker")
                                    .setSnippet(dataSnapshot.getKey()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        walkers.addValueEventListener(walkerListener);
    }

}
