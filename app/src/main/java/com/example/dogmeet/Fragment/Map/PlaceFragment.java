package com.example.dogmeet.Fragment.Map;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dogmeet.Fragment.Profile.PetAdapter;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.model.Meeting;
import com.example.dogmeet.model.Pet;
import com.example.dogmeet.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.security.acl.Group;
import java.util.ArrayList;


public class PlaceFragment extends Fragment implements RecyclerViewInterface {

    private View view;
    private String placeUid, uid;
    private Group group;
    private DatabaseReference place, myMeet;
    private ArrayList<String> meetUidList;
    private ArrayList<Meeting> meetingArrayList;
    private RecyclerView recyclerView;
    private MeetingMarkerAdapter meetingMarkerAdapter;
    private TextView namePlace, notMeeting, ratingTextView;
    float rating;
    private RatingBar ratingBar;


    public PlaceFragment() {

    }


    public static PlaceFragment newInstance(String placeUid) {
        PlaceFragment fragment = new PlaceFragment();
        Bundle args = new Bundle();
        args.putString("UID", placeUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeUid=getArguments().getString("UID", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_place, container, false);
        namePlace=view.findViewById(R.id.textName);
        notMeeting=view.findViewById(R.id.not_meeting);
        namePlace.setText(placeUid);
        ratingBar=view.findViewById(R.id.ratingBar2);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        meetingArrayList=new ArrayList<>();
        meetUidList=new ArrayList<>();

        recyclerView=view.findViewById(R.id.place_meeting);
        recyclerView.setHasFixedSize(true);

        meetingMarkerAdapter= new MeetingMarkerAdapter(meetingArrayList, this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(meetingMarkerAdapter);

        place= FirebaseDatabase.getInstance().getReference("places").child(placeUid);

        ValueEventListener placeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (meetUidList.size()>0) meetUidList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String meetUid=dataSnapshot.getValue(String.class);
                    if (meetUid!=null){
                        meetUidList.add(meetUid);
                    }
                }
                getMeeting();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        place.child("meetings").addValueEventListener(placeListener);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                place.child("ratingList").child(uid).setValue(String.valueOf(v));
                if (rating!=0) {
                    rating=(rating+v)/2;
                }
                else {
                    rating=v;
                }
                place.child("rating").setValue(String.valueOf(rating));
            }
        });

        return view;
    }

    @Override
    public void OnItemClick(int position) {

    }

    @Override
    public void OnButtonClick(int position) {

    }

    public void getMeeting(){
        if (meetUidList.size()>0){
            notMeeting.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            for (String meetingUid: meetUidList){
                myMeet = FirebaseDatabase.getInstance().getReference("meeting").child(meetingUid);

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
        else {
            notMeeting.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}