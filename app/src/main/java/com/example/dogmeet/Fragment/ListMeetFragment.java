package com.example.dogmeet.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogmeet.Activity.MeetingActivity;
import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.adapter.MeetingAdapter;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.User;
import com.example.dogmeet.mainActivity.AddActivity;
import com.example.dogmeet.mainActivity.NavigatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListMeetFragment extends Fragment implements RecyclerViewInterface {
    private DatabaseReference myMeet;
    private ArrayList<Meeting> meetings;
    private String uidMeet;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;
    private View view;
    private FloatingActionButton fabAddMeet;


    public ListMeetFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_meet, container, false);
        myMeet = FirebaseDatabase.getInstance().getReference("meeting");
        meetings = new ArrayList<>();

        fabAddMeet=view.findViewById(R.id.fabAddMeet);

        fabAddMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), AddActivity.class);
                startActivity(i);
            }
        });

        recyclerView=view.findViewById(R.id.recycle_view_meeting_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        meetingAdapter= new MeetingAdapter(meetings, this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(meetingAdapter);

        getDataFromDB();
        return view;
    }

    private void getDataFromDB(){
        ValueEventListener meetListener = new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(meetings.size() > 0) meetings.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Meeting meeting =dataSnapshot.getValue(Meeting.class);
                    assert meeting != null;
                    if (UpdateListMeeting(meeting)){
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("archive")
                                .child("meeting")
                                .child(dataSnapshot.getKey())
                                .setValue(meeting);

                        dataSnapshot.getRef().removeValue();
                    }
                    else {
                        uidMeet = dataSnapshot.getKey();
                        meeting.setUid(uidMeet);
                        meetings.add(meeting);
                    }
                }
                meetingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        myMeet.addValueEventListener(meetListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void OnItemClick(int position) {
        Meeting meeting=meetings.get(position);
        Intent i = new Intent(getContext(), MeetingActivity.class);
        i.putExtra(Constant.MEETING_UID, meeting.getUid());
        i.putExtra(Constant.MEETING_CREATOR_UID, meeting.getCreatorUid());
        i.putExtra(Constant.IS_COMMENT, false);
        startActivity(i);
    }

    @Override
    public void OnButtonClick(int position) {
        Meeting meeting=meetings.get(position);
        Intent i = new Intent(getContext(), MeetingActivity.class);
        i.putExtra(Constant.MEETING_UID, meeting.getUid());
        i.putExtra(Constant.MEETING_CREATOR_UID, meeting.getCreatorUid());
        i.putExtra(Constant.IS_COMMENT, true);
        startActivity(i);
    }

    private boolean UpdateListMeeting(Meeting meeting){
        long date=new Date().getTime();
        long dateMeet = 0;

        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date d = f.parse(meeting.getDate());
            dateMeet = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dateMeet<date){
            return true;
        }
        else {
            return false;
        }
    }
}