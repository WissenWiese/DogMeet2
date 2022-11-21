package com.example.dogmeet.Fragment.ListMeet;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogmeet.Meeting.MeetingActivity;
import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.model.Meeting;
import com.example.dogmeet.mainActivity.AddActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class ListMeetFragment extends Fragment implements RecyclerViewInterface{
    private ArrayList<Meeting> meetings, updateList;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;
    private View view;
    private FloatingActionButton fabFilter, fabAddMeet;
    private CardView filters;
    private String database="meeting";
    private DatabaseReference myMeet;


    public ListMeetFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_meet, container, false);

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");

        meetings = new ArrayList<>();
        updateList =new ArrayList<>();

        fabFilter=view.findViewById(R.id.fabFilter);
        fabAddMeet=view.findViewById(R.id.fabAddMeet);

        filters=view.findViewById(R.id.filter);

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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAddMeet.animate().translationY(getResources().getDimension(R.dimen.standard_100));
                } else if (dy < 0) {
                    fabAddMeet.animate().translationY(0);
                }
            }
        });

        meetingAdapter= new MeetingAdapter(this, meetings);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(meetingAdapter);

        MeetingData meetingData=new MeetingData(myMeet);
        meetingData.attachView(this);
        meetingData.loadMeetings();

        FilterFragment filterFragment=FilterFragment.newInstance();
        filterFragment.setModel(meetingData);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainerView, filterFragment).addToBackStack(null);
        transaction.commit();



        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (fabFilter.getTag().toString()) {
                    case "close":
                        filters.animate().translationY(getResources().getDimension(R.dimen.standard_210));
                        fabFilter.animate().translationY(getResources().getDimension(R.dimen.standard_210));
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

        return view;
    }

    @Override
    public void OnItemClick(int position) {
        Meeting meeting;
        if (updateList.size()>0){
            meeting=updateList.get(position);
        }
        else {
            meeting=meetings.get(position);
        }

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
        i.putExtra(Constant.DATABASE, database);
        startActivity(i);
    }

    public void showListMeet(ArrayList<Meeting> meetings){
        meetingAdapter.setList(meetings);
        this.meetings=meetings;
    }

    public void showFilteredList(ArrayList<Meeting> filteredList){
        meetingAdapter.filterList(filteredList);
        this.updateList=filteredList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sort(String sortType){
        switch (sortType){
            case "По умолчанию":
                meetingAdapter.sort();
                break;
            case "По популярности":
                meetingAdapter.sortByPopular();
                break;
            case "По дате":
                meetingAdapter.sortByDate();
                break;
        }
    }
}