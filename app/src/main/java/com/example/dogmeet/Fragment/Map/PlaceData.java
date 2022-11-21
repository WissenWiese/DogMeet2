package com.example.dogmeet.Fragment.Map;

import android.graphics.BitmapFactory;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dogmeet.Fragment.ListMeet.ListMeetFragment;
import com.example.dogmeet.Fragment.ListMeet.MeetingData;
import com.example.dogmeet.model.Meeting;
import com.example.dogmeet.model.Place;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class PlaceData {

    private final DatabaseReference placeDB;
    private ArrayList<Place> places;
    private MapFragment view;

    public PlaceData(DatabaseReference placeDB){
        this.placeDB=placeDB;
    }

    public void attachView(MapFragment mapFragment) {
        view=mapFragment;
    }

    public void loadPlace(){
        places=new ArrayList<>();
        ValueEventListener placeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (places.size()>0) places.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Place place=dataSnapshot.getValue(Place.class);
                    if (place!=null) {
                        place.setUid(dataSnapshot.getKey());
                        places.add(place);
                    }
                }
                view.setPlace(places);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        placeDB.addValueEventListener(placeListener);
    }

    public void getPlace(PlaceFragment view){
        ArrayList<String> meetUidList=new ArrayList<>();
        ValueEventListener placeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Place place=snapshot.getValue(Place.class);
                if (meetUidList.size()>0) meetUidList.clear();
                for(DataSnapshot dataSnapshot : snapshot.child("meetings").getChildren())
                {
                    meetUidList.add(dataSnapshot.getKey());
                }

                view.setPlace(place, meetUidList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        placeDB.addValueEventListener(placeListener);
    }

    public void filterPlace(ArrayList<String> typeList){
        ArrayList<String> filteredList = new ArrayList<>();
        for (Place place: places){
            if (typeList.isEmpty()){
                break;
            }
            else if (!typeList.isEmpty()){
                for (String type: typeList){
                    if (place.getType().equals(type)){
                        filteredList.add(place.getUid());
                    }
                }
            }
        }
        view.filterMarker(filteredList);
    }
}
