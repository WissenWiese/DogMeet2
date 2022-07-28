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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;
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
    private DatabaseReference myMeet, users, archive;
    private ArrayList<Meeting> meetings;
    private String uidMeet, uid, database;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;
    private View view;
    private FloatingActionButton fabAddMeet, fabFilter;
    private CardView filters;
    private Spinner spinner;
    private CheckedTextView checkedMy, checkedArchive;


    public ListMeetFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_meet, container, false);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        users= FirebaseDatabase.getInstance().getReference("Users").child(uid).child("myMeetings");
        archive= FirebaseDatabase.getInstance().getReference("archive").child("meeting");

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");
        meetings = new ArrayList<>();

        fabAddMeet=view.findViewById(R.id.fabAddMeet);
        fabFilter=view.findViewById(R.id.fabFilter);

        filters=view.findViewById(R.id.filter);

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

        checkedMy=view.findViewById(R.id.checkedMy);

        checkedMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkedMy.isChecked()){
                    if (!checkedArchive.isChecked()){
                        getDataFromDB();
                    }
                    checkedMy.setChecked(false);
                    checkedMy.setCheckMarkDrawable(getResources().getDrawable(R.drawable.checkbox));
                }
                else {
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
                    checkedMy.setChecked(true);
                    checkedMy.setCheckMarkDrawable(getResources().getDrawable(R.drawable.checked_checkbox));
                }
            }
        });

        checkedArchive=view.findViewById(R.id.checkedArchive);

        checkedArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkedArchive.isChecked()){
                    getDataFromDB();
                    checkedArchive.setChecked(false);
                    checkedArchive.setCheckMarkDrawable(getResources().getDrawable(R.drawable.checkbox));
                }
                else {
                    ValueEventListener archiveListener = new ValueEventListener()  {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(meetings.size() > 0) meetings.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                Meeting meeting =dataSnapshot.getValue(Meeting.class);
                                assert meeting != null;
                                uidMeet = dataSnapshot.getKey();
                                meeting.setUid(uidMeet);
                                meetings.add(meeting);
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
                    archive.addValueEventListener(archiveListener);
                    database="archive";
                    checkedArchive.setChecked(true);
                    checkedArchive.setCheckMarkDrawable(getResources().getDrawable(R.drawable.checked_checkbox));
                }
            }
        });

        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(getContext(), R.array.sortList,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = view.findViewById(R.id.sortSpinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getItemAtPosition(i).toString()){
                    case "По умолчанию":
                        Collections.sort(meetings, Comparator.comparing(Meeting::getUid));
                        meetingAdapter.notifyDataSetChanged();
                        break;
                    case "По популярности":
                        Collections.sort(meetings, Comparator.comparing(Meeting::getNumberMember));
                        meetingAdapter.notifyDataSetChanged();
                        break;
                    case "По дате":
                        Collections.sort(meetings, Collections.reverseOrder(Comparator.comparing(Meeting::getDate)));
                        meetingAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
        database="meeting";
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
        i.putExtra(Constant.DATABASE, database);
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