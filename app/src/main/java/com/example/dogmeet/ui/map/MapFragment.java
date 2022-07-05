package com.example.dogmeet.ui.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.dogmeet.R;
import com.example.dogmeet.entity.Meeting;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MapFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference myMeet;
    private GoogleMap mMap;

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
                    AlertDialog.Builder meet_dialog=new AlertDialog.Builder(getActivity());

                    LayoutInflater inflator= LayoutInflater.from(getActivity());
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
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);

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

                    LatLng point=getLocationFromAddress(getActivity(), address);

                    addMarker(point, title, address, date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMeet.addValueEventListener(meetListener);
    }

    private void addMarker(LatLng point, String title, String address, String date){

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