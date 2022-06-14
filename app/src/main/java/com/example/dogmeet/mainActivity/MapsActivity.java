package com.example.dogmeet.mainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.dogmeet.R;
import com.example.dogmeet.entity.Meeting;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase database;
    private DatabaseReference myMeet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Карта мероприятий");
        setContentView(R.layout.activity_maps);

        database = FirebaseDatabase.getInstance();
        myMeet = database.getReference("meeting");

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ValueEventListener meetListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Meeting meeting =dataSnapshot.getValue(Meeting.class);

                    assert meeting != null;
                    String title= meeting.getTitle();
                    String address=meeting.getAddress();
                    String date= meeting.getDate();

                    LatLng point=getLocationFromAddress(MapsActivity.this, address);

                    addMarker(point, title, address, date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMeet.addValueEventListener(meetListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.add:
                intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                return true;
            case R.id.map:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                return true;
            case R.id.list:
                intent = new Intent(this, ListActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng ekb = new LatLng(56.839104, 60.60825);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ekb));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                AlertDialog.Builder meet_dialog=new AlertDialog.Builder(MapsActivity.this);

                LayoutInflater inflator= LayoutInflater.from(MapsActivity.this);
                View meet_window= inflator.inflate(R.layout.meet_window, null);
                meet_dialog.setView(meet_window);

                TextView title=meet_window.findViewById(R.id.title_meet);
                TextView description=meet_window.findViewById(R.id.description_meet);

                title.setText(arg0.getTitle());
                description.setText(arg0.getSnippet());

                meet_dialog.show();
                return true;
            }

        });
    }

    private void addMarker(LatLng point, String title, String address, String date){

        /** Make sure that the map has been initialised **/
        if(null != mMap){
            mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .title(title)
                    .snippet(address+"\n"
                            +date+"\n")
                    .draggable(true)
            );
        }
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
}