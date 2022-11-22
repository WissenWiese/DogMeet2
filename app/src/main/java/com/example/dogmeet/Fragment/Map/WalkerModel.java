package com.example.dogmeet.Fragment.Map;

import androidx.annotation.NonNull;

import com.example.dogmeet.entity.Pet;
import com.example.dogmeet.entity.Walker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfMeasurement;

import java.util.ArrayList;

public class WalkerModel {

    private final DatabaseReference walkersDB;
    private ArrayList<Walker> walkers;
    private Walker mWalker=null;
    private MapFragment view;

    public WalkerModel(DatabaseReference walkersDB){
        this.walkersDB=walkersDB;
    }

    public void attachView(MapFragment mapFragment) {
        view=mapFragment;
    }

    public void loadWalkers(String uid){
        walkers=new ArrayList<>();
        ValueEventListener walkerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (walkers.size()>0) walkers.clear();
                mWalker=null;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Walker walker=dataSnapshot.getValue(Walker.class);
                    if (walker!=null) {
                        walker.setUserUId(dataSnapshot.getKey());
                        if (walker.getUserUId().equals(uid)){
                            mWalker=walker;
                        }
                        walkers.add(walker);
                    }
                }
                view.setWalkers(walkers);
                getUserNearby(mWalker);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        walkersDB.addValueEventListener(walkerListener);
    }

    public void getWalker(WalkerMarker view){
        ArrayList<Pet> pets=new ArrayList<>();
        ValueEventListener dListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                {
                    Walker walker =snapshot.getValue(Walker.class);
                    if (walker !=null){
                        if(pets.size() > 0)pets.clear();
                        for (DataSnapshot dataSnapshot: snapshot.child("pets").getChildren()){
                            Pet pet =dataSnapshot.getValue(Pet.class);
                            assert pet != null;
                            pet.setPetUid(dataSnapshot.getKey());
                            pets.add(pet);
                        }
                        walker.setPets(pets);
                        view.setWalker(walker);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        walkersDB.addListenerForSingleValueEvent(dListener);
    }

    public void filterPet(String gender, ArrayList<String> sizeList, ArrayList<String> breedList){
        ArrayList<String> filteredList = new ArrayList<>();
        for (Walker walker:walkers){
            ArrayList<Pet> petArrayList=walker.getPets();
            for (Pet pet: petArrayList){
                if (!filteredList.contains(walker)) {
                    if (gender == null && sizeList.isEmpty() && breedList.isEmpty()) {
                        break;
                    } else if (gender != null && sizeList.isEmpty() && breedList.isEmpty()) {
                        if (pet.getGender().equals(gender)) filteredList.add(walker.getUserUId());
                    } else if (gender == null && !sizeList.isEmpty() && breedList.isEmpty()) {
                        for (String size : sizeList) {
                            if (pet.getSize().equals(size)) filteredList.add(walker.getUserUId());
                        }
                    } else if (gender == null && sizeList.isEmpty() && !breedList.isEmpty()) {
                        for (String breed : breedList) {
                            if (pet.getBreed().equals(breed)) filteredList.add(walker.getUserUId());
                        }
                    } else if (gender != null && !sizeList.isEmpty() && breedList.isEmpty()) {
                        for (String size : sizeList) {
                            if (pet.getSize().equals(size)
                                    && pet.getGender().equals(gender))
                                filteredList.add(walker.getUserUId());
                        }
                    } else if (gender != null && sizeList.isEmpty() && !breedList.isEmpty()) {
                        for (String breed : breedList) {
                            if (pet.getBreed().equals(breed)
                                    && pet.getGender().equals(gender))
                                filteredList.add(walker.getUserUId());
                        }
                    } else if (gender == null && !sizeList.isEmpty() && !breedList.isEmpty()) {
                        for (String size : sizeList) {
                            if (pet.getSize().equals(size)) {
                                for (String breed : breedList) {
                                    if (pet.getBreed().equals(breed))
                                        filteredList.add(walker.getUserUId());
                                }
                            }
                        }
                    } else if (gender != null && !sizeList.isEmpty() && !breedList.isEmpty()) {
                        for (String size : sizeList) {
                            if (pet.getSize().equals(size)) {
                                for (String breed : breedList) {
                                    if (pet.getBreed().equals(breed)
                                            && pet.getGender().equals(gender))
                                        filteredList.add(walker.getUserUId());
                                }
                            }
                        }
                    }
                }
            }
        }
        view.filterMarker(filteredList);
    }

    public void getUserNearby(Walker mWalker){
        Point myPoint, point;
        String message=null;
        if (mWalker !=null) {
            double latitude = Double.parseDouble(mWalker.getLatitude());
            double longitude = Double.parseDouble(mWalker.getLongitude());
            myPoint = Point.fromLngLat(latitude, longitude);
            for (Walker user : walkers) {
                if (!user.getUserUId().equals(mWalker.getUserUId())) {
                    double latitude1 = Double.parseDouble(user.getLatitude());
                    double longitude1 = Double.parseDouble(user.getLongitude());
                    point = Point.fromLngLat(latitude1, longitude1);
                    if (TurfMeasurement.distance(myPoint, point) < 0.05 || TurfMeasurement.distance(myPoint, point)== 0.0) {
                        for (Pet mPet : mWalker.getPets()) {
                            for (Pet pet : user.getPets()) {
                                if (mPet.getSize().equals("Маленький") && pet.getSize().equals("Большой")) {
                                    message = "Рядом большая собака!";
                                    view.getAttention(message);
                                    break;
                                } else if (mPet.getSize().equals("Большой") && pet.getSize().equals("Маленький")) {
                                    message = "Рядом маленькая собака!";
                                    view.getAttention(message);
                                    break;
                                }
                            }
                        }
                    }
                    else view.getAttention(message);
                }
            }
        }
        else  view.getAttention(null);

    }
}
