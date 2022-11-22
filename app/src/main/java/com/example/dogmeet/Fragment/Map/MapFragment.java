package com.example.dogmeet.Fragment.Map;


import static com.mapbox.geojson.Point.fromLngLat;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.example.dogmeet.R;
import com.example.dogmeet.entity.Doghanting;
import com.example.dogmeet.entity.Place;
import com.example.dogmeet.entity.Walker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapFragment extends Fragment  implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener,
        MapboxMap.OnMarkerClickListener{


    private MapView mapView;
    private MapboxMap mapboxMap;
    private View view;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private Boolean onClickMap=false, isFABOpen=false;
    private FloatingActionButton fab, fab1, fab2, search;
    private FrameLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;
    private TextView doghantingCreate, attention;
    private Marker addingMarker;
    private Boolean isWalk=false, hasAttention=false;
    private DatabaseReference doghanting, walkers, place;
    private String uid;
    WalkerModel walkerData;
    PlaceModel placeData;
    Timer myTimer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We set the listener on the child fragmentManager
        getChildFragmentManager()
                .setFragmentResultListener("requestKey", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                        Boolean started = bundle.getBoolean("isWalk");
                        startTimer(started);
                        isWalk=started;
                        // Do something with the result
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));
        view=inflater.inflate(R.layout.fragment_map, container, false);

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        mapView = view.findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        fab=view.findViewById(R.id.floatingActionButton);
        fab1=view.findViewById(R.id.floatingActionButton2);
        fab2=view.findViewById(R.id.floatingActionButton3);
        search=view.findViewById(R.id.floatingActionButtonSearch);

        mBottomSheetLayout = view.findViewById(R.id.containerBottomSheet);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        sheetBehavior.setSaveFlags(BottomSheetBehavior.SAVE_NONE);

        doghantingCreate=view.findViewById(R.id.doghantingPointTextView);
        attention=view.findViewById(R.id.attention);

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

                onClickMap=true;
                if (hasAttention){
                    attention.setVisibility(View.INVISIBLE);
                }
                doghantingCreate.setVisibility(View.VISIBLE);
                closeFABMenu();
                liveFABMenu();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                liveFABMenu();
                WalkFragment walkFragment= WalkFragment.newInstance(
                        mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude(),
                        mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude(),
                        isWalk);
                replaceFragment(walkFragment);
                if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                liveFABMenu();
                SearchFragment searchFragment=SearchFragment.newInstance();
                searchFragment.setModel(walkerData, placeData);
                replaceFragment(searchFragment);
                if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset==0){
                    closeFABMenu();
                    onClickMap=false;
                    if (addingMarker!=null) {
                        addingMarker.remove();
                    }
                }

            }
        });

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

        loadingDoghanter(mapboxMap);

        walkers=FirebaseDatabase.getInstance().getReference("walker");
        walkerData=new WalkerModel(walkers);
        walkerData.attachView(this);
        walkerData.loadWalkers(uid);

        place=FirebaseDatabase.getInstance().getReference("places");
        placeData=new PlaceModel(place);
        placeData.attachView(this);
        placeData.loadPlace();
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        if (onClickMap) {

            if (addingMarker !=null){
                addingMarker.remove();
            }

            addingMarker =mapboxMap.addMarker(new MarkerOptions()
                    .setPosition(point));
            doghantingCreate.setVisibility(View.INVISIBLE);
            if (hasAttention){
                attention.setVisibility(View.VISIBLE);
            }

            DoghantingFragment doghanterFragment= DoghantingFragment.newInstance(point.getLatitude(), point.getLongitude());
            replaceFragment(doghanterFragment);
            if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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

    public void replaceFragment(Fragment someFragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.containerBottomSheet, someFragment).addToBackStack(null);
        transaction.commit();
    }

    public void loadingDoghanter(MapboxMap mapboxMap){
        doghanting= FirebaseDatabase.getInstance().getReference("doghanter");

        ValueEventListener dListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
        doghanting.addValueEventListener(dListener);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (addingMarker!=null) {
            addingMarker.remove();
        }
        closeFABMenu();
        liveFABMenu();
        if (marker.getTitle().equals("doghanter")){
            DoghantingMarker doghantingMarker= DoghantingMarker.newInstance(marker.getSnippet());
            replaceFragment(doghantingMarker);
        }
        else if (marker.getTitle().equals("walker")){
            WalkerMarker walkerMarker= WalkerMarker.newInstance(marker.getSnippet());
            replaceFragment(walkerMarker);

        }
        else if (marker.getTitle().equals("place")){
            PlaceFragment placeFragment= PlaceFragment.newInstance(marker.getSnippet());
            replaceFragment(placeFragment);
        }
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (marker.getTitle().equals("place")) sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        else sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return true;
    }
    
    public void startTimer(Boolean started) {

        if (started){
            myTimer = new Timer();

            myTimer.schedule(new TimerTask() {
                public void run() {
                    if (getActivity() == null) {
                        myTimer = null;
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                                String latitude = String.valueOf(mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
                                String longitude = String.valueOf(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude());
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("walker")
                                        .child(uid)
                                        .child("latitude")
                                        .setValue(latitude);
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("walker")
                                        .child(uid)
                                        .child("longitude")
                                        .setValue(longitude);
                        }
                    });
                }
            }, 0, 30000);
        }
        else {
            if (myTimer != null) {
                myTimer.cancel();
                myTimer = null;
            }
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("walker")
                    .child(uid)
                    .removeValue();
        }
    }

    public void setPlace(ArrayList<Place> places){
        for (Place place :places){
            double latitude = place.getLatitude();
            double longitude = place.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            mapboxMap.addMarker(new MarkerOptions()
                    .setPosition(latLng)
                    .setTitle("place")
                    .setSnippet(place.getUid())
                    .setIcon(IconFactory.recreate("place_ic",
                            BitmapFactory.decodeResource( getResources(),
                                    com.mapbox.mapboxsdk.R.drawable.mapbox_logo_helmet))));
        }
    }

    public void setWalkers(ArrayList<Walker> walkers){
        for (Walker walker :walkers){
            double latitude = Double.parseDouble(walker.getLatitude());
            double longitude = Double.parseDouble(walker.getLongitude());
            LatLng latLng = new LatLng(latitude, longitude);
            if (walker.getUserUId().equals(uid)) {
                isWalk=true;
                if (myTimer==null){
                    startTimer(true);
                }
            }
            else {
                mapboxMap.addMarker(new MarkerOptions()
                                .setPosition(latLng)
                                .setTitle("walker")
                                .setSnippet(walker.getUserUId()))
                        .setIcon(IconFactory.recreate("walker_ic",
                                BitmapFactory.decodeResource( getResources(),
                                        com.mapbox.services.android.navigation.ui.v5.R.drawable.map_marker_light)));
                            }
        }

    }

    public void getAttention(String message){
        if (message!=null){
            attention.setVisibility(View.VISIBLE);
            attention.setText(message);
            hasAttention=true;
        }
        else {
            attention.setVisibility(View.INVISIBLE);
            hasAttention=false;
        }
    }

    public void filterMarker(ArrayList<String> filterList){
        for (Marker marker: mapboxMap.getMarkers()){
            if (filterList.isEmpty()){
                if (marker.getTitle().equals("walker")){
                    marker.setIcon(IconFactory.recreate("walker_ic",
                            BitmapFactory.decodeResource( getResources(),
                                    com.mapbox.services.android.navigation.ui.v5.R.drawable.map_marker_light)));
                }
                else if (marker.getTitle().equals("place")){
                    marker.setIcon(IconFactory.recreate("place_ic",
                            BitmapFactory.decodeResource( getResources(),
                                    com.mapbox.mapboxsdk.R.drawable.mapbox_logo_helmet)));
                }
            }
            else {
                for (String uid : filterList) {
                    if (marker.getSnippet().equals(uid))
                        marker.setIcon(IconFactory.recreate("filter_ic",
                                BitmapFactory.decodeResource(getResources(),
                                        com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default)));

                }
            }
        }
    }
}
