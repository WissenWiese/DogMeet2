package com.example.dogmeet.Fragment.Map;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dogmeet.Constant;
import com.example.dogmeet.Fragment.ListMeet.MeetingData;
import com.example.dogmeet.Meeting.MeetingActivity;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.model.Meeting;
import com.example.dogmeet.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PlaceFragment extends Fragment implements RecyclerViewInterface {

    private View view;
    private String placeUid, uid;
    private Group group;
    private DatabaseReference place, myMeet;
    private ArrayList<String> meetUidList;
    private ArrayList<Meeting> meetingArrayList;
    private RecyclerView recyclerView;
    private ListMeetingAdapter listMeetingAdapter;
    private TextView namePlace, notMeeting, ratingTextView, type, contact, openHours, address;
    float rating;
    private RatingBar ratingBar;
    private MeetingData meetingData;


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
        type=view.findViewById(R.id.textTypePlace);
        notMeeting=view.findViewById(R.id.not_meeting);
        ratingTextView=view.findViewById(R.id.rating);
        address=view.findViewById(R.id.textViewAddress);
        contact=view.findViewById(R.id.textViewContacts);
        openHours=view.findViewById(R.id.textViewOpenHours);

        group=view.findViewById(R.id.info);

        namePlace.setText(placeUid);
        ratingBar=view.findViewById(R.id.ratingBar2);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        meetingArrayList=new ArrayList<>();
        meetUidList=new ArrayList<>();

        recyclerView=view.findViewById(R.id.place_meeting);
        recyclerView.setHasFixedSize(true);

        listMeetingAdapter = new ListMeetingAdapter(meetingArrayList, this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(listMeetingAdapter);

        place= FirebaseDatabase.getInstance().getReference("places").child(placeUid);
        myMeet = FirebaseDatabase.getInstance().getReference("meeting");

        PlaceData placeData=new PlaceData(place);
        placeData.getPlace(this);

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
        Meeting meeting;
        meeting=meetingArrayList.get(position);

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

    public void setPlace(Place place,  ArrayList<String> meetingsUid){
        String typeText=place.getType();
        type.setText(typeText);
        if (!place.getType().equals("Парк")){
            address.setVisibility(View.VISIBLE);
            address.setText(place.getAddress());
        }
        else {
            address.setVisibility(View.GONE);
        }
        if (place.getRating()!=null){
            ratingTextView.setText(place.getRating());
        }

        if (meetingsUid.isEmpty()){
            notMeeting.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            meetingData=new MeetingData(myMeet);
            meetingData.getMeetings(meetingsUid, this);
            notMeeting.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void setMeeting(ArrayList<Meeting> meetings){
        listMeetingAdapter.setList(meetings);
        meetingArrayList=meetings;
    }
}