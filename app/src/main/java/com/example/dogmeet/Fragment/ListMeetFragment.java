package com.example.dogmeet.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListMeetFragment extends Fragment implements RecyclerViewInterface {
    private DatabaseReference myMeet, users;
    private ArrayList<Meeting> meetings;
    private String uidMeet, uid;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;
    private View view;
    private FloatingActionButton fabAddMeet, fabFilter;
    private CardView filters;
    private Button sortPopular, sortDate, filterMy;


    public ListMeetFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_meet, container, false);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        users= FirebaseDatabase.getInstance().getReference("Users").child(uid).child("myMeetings");

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");
        meetings = new ArrayList<>();

        fabAddMeet=view.findViewById(R.id.fabAddMeet);
        fabFilter=view.findViewById(R.id.fabFilter);

        filters=view.findViewById(R.id.filter);

        sortPopular=view.findViewById(R.id.button2);
        sortDate=view.findViewById(R.id.button3);
        filterMy=view.findViewById(R.id.button4);

        fabAddMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), AddActivity.class);
                startActivity(i);
            }
        });

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (fabFilter.getTag().toString()) {
                    case "close":
                        filters.animate().translationY(getResources().getDimension(R.dimen.standard_100));
                        fabFilter.animate().translationY(getResources().getDimension(R.dimen.standard_100));
                        fabFilter.setTag("open");
                        fabFilter.setImageDrawable(getResources().getDrawable(R.drawable.up));
                        break;
                    case "open":
                        filters.animate().translationY(0);
                        fabFilter.animate().translationY(0);
                        fabFilter.setTag("close");
                        fabFilter.setImageDrawable(getResources().getDrawable(R.drawable.poits));
                        break;
                }
            }
        });

        filterMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValueEventListener myMeetListener = new ValueEventListener()  {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Meeting> meetings1 = new ArrayList<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            String myMeetUid=dataSnapshot.getKey();
                            for (Meeting meeting : meetings){
                                if (meeting.getUid().equals(myMeetUid)){
                                    meetings1.add(meeting);
                                }
                            }
                        }
                        meetings.clear();
                        meetings.addAll(meetings1);
                        meetingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                };
                users.addValueEventListener(myMeetListener);
            }
        });

        sortDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Collections.sort(meetings, Collections.reverseOrder(Comparator.comparing(Meeting::getDate)));
                meetingAdapter.notifyDataSetChanged();
            }
        });

        sortPopular.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Collections.sort(meetings, Comparator.comparing(Meeting::getNumberMember));
                meetingAdapter.notifyDataSetChanged();
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
                if (recyclerView.getAdapter().getItemCount()>2) {
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }
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
        long dateMeet = meeting.getDate();

        if (dateMeet>=date){
            return false;
        }
        else {
            return true;
        }
    }
}