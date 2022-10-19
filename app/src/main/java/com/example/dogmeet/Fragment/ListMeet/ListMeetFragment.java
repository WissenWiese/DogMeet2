package com.example.dogmeet.Fragment.ListMeet;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ListMeetFragment extends Fragment implements RecyclerViewInterface, View.OnClickListener {
    private DatabaseReference myMeet, users;
    private ArrayList<Meeting> meetings, filterTypeList, updateList;
    private String uidMeet, uid, database, date, dateForFilter, typeDog, add, lastType;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;
    private View view;
    private FloatingActionButton fabFilter;
    private CardView filters;
    private Spinner spinner;
    private CheckedTextView checkedMy;
    private ImageButton calendar, dateOff;
    private Button anyBtn, bigBtn, middleBtn, smallBtn;
    private TextView dateFilter;
    int click, clickBtn;
    LocalDate dateMin, dateMax, dtMeet;

    public ListMeetFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_meet, container, false);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        users= FirebaseDatabase.getInstance().getReference("Users");

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");
        meetings = new ArrayList<>();
        updateList =new ArrayList<>();

        fabFilter=view.findViewById(R.id.fabFilter);

        filters=view.findViewById(R.id.filter);


        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (fabFilter.getTag().toString()) {
                    case "close":
                        filters.animate().translationY(getResources().getDimension(R.dimen.standard_150));
                        fabFilter.animate().translationY(getResources().getDimension(R.dimen.standard_150));
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (checkedMy.isChecked()){
                    checkedMy.setChecked(false);
                    checkedMy.setCheckMarkDrawable(getResources()
                            .getDrawable(R.drawable.checkbox));
                }
                else {
                    checkedMy.setChecked(true);
                    checkedMy.setCheckMarkDrawable(getResources()
                            .getDrawable(R.drawable.checked_checkbox));
                }
                //getMyMeet();
                update();
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
                        Collections.sort(meetings, Comparator.comparing(Meeting::getNumberMember)
                                .thenComparing(Meeting::getNumberComments));
                        meetingAdapter.notifyDataSetChanged();
                        break;
                    case "По дате":
                        Collections.sort(meetings, Comparator.comparing(Meeting::getDate));
                        Collections.reverse(meetings);
                        meetingAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        calendar=view.findViewById(R.id.calendar);
        dateFilter=view.findViewById(R.id.date);
        dateOff=view.findViewById(R.id.imageView4);

        calendar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                showDatePickDlg();
            }
        });

        dateFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(dateFilter.getText().toString())) {
                    dateOff.setVisibility(View.VISIBLE);
                    //getDateMeet();
                    update();
                }
            }
        });

        dateOff.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                dateOff.setVisibility(View.INVISIBLE);
                dateFilter.setText(null);
                //getDateMeet();
                update();
            }
        });

        anyBtn=view.findViewById(R.id.anyBtn);
        anyBtn.setOnClickListener(this);
        bigBtn=view.findViewById(R.id.bigBtn);
        bigBtn.setOnClickListener(this);
        middleBtn=view.findViewById(R.id.middleBtn);
        middleBtn.setOnClickListener(this);
        smallBtn=view.findViewById(R.id.smallBtn);
        smallBtn.setOnClickListener(this);

        filterTypeList=new ArrayList<>();
        clickBtn=0;

        return view;
    }

    private void getDataFromDB(){
        ValueEventListener meetListener = new ValueEventListener()  {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
        i.putExtra(Constant.DATABASE, database);
        startActivity(i);
    }

    public void getMyMeet(Boolean record){
        ValueEventListener myMeetListener = new ValueEventListener()  {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Meeting> filteredlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String myMeetUid=dataSnapshot.getKey();
                    for (Meeting meeting : updateList){
                        if (meeting.getUid().equals(myMeetUid)){
                            filteredlist.add(meeting);
                        }
                    }
                }
                updateList.clear();
                updateList.addAll(filteredlist);
                if (record) meetingAdapter.filterList(filteredlist);
                else  getDateMeet(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        users.child(uid).child("myMeetings").addValueEventListener(myMeetListener);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void showDatePickDlg() {
        dateFilter.setText(null);
        dateForFilter=null;
        click=0;
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (dateForFilter==null){
                    monthOfYear=monthOfYear+1;
                    if (monthOfYear<10) {
                        dateFilter.setText(dayOfMonth + ".0" + monthOfYear + "." + year);
                    }
                    else{
                        dateFilter.setText(dayOfMonth + "." + monthOfYear + "." + year);
                    }
                }
                else {
                    dateFilter.setText(dateForFilter);
                }

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                click=click+1;
                monthOfYear=monthOfYear+1;
                if (monthOfYear<10) {
                    date=dayOfMonth + ".0" + monthOfYear;
                }
                else{
                    date=dayOfMonth + "." + monthOfYear;
                }

                long dateLong=0;

                SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date d = f.parse(dayOfMonth +"."+ monthOfYear +"."+ year);
                    dateLong = d.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (click==1){
                    dateMin=LocalDate.of(year, monthOfYear, dayOfMonth);
                    datePickerDialog.getDatePicker().setMinDate(dateLong);
                    dateForFilter=date;
                    dateMax=LocalDate.of(year, monthOfYear, dayOfMonth);
                }
                else if (click==2){
                    dateMax=LocalDate.of(year, monthOfYear, dayOfMonth);
                    if (!dateMin.isEqual(dateMax)){
                        datePickerDialog.getDatePicker().setMaxDate(dateLong);
                        dateForFilter=dateForFilter+'-'+date;
                    }
                    click=0;
                    datePickerDialog.getDatePicker().setMinDate(0);
                }
            }
        });
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getDateMeet(Boolean record){
        ArrayList<Meeting> filteredlist = new ArrayList<>();
        for (Meeting meeting : updateList) {
            String date = DateFormat.format("dd.MM.yyyy", meeting.getDate()).toString();
            SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
            try {
                Date d = f.parse(date);
                dtMeet = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (dtMeet.isEqual(dateMin)) {
                    filteredlist.add(meeting);
            }
            else if ((dtMeet.isBefore(dateMax) || dtMeet.isEqual(dateMax))
                    && dtMeet.isAfter(dateMin)){
                    filteredlist.add(meeting);
            }
        }
        updateList.clear();
        updateList.addAll(filteredlist);
        if (record) meetingAdapter.filterList(filteredlist);
        else getMyMeet(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getTypeDog(Boolean filter, String typeFilter){
        ArrayList<Meeting> filteredlist = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if (meeting.getTypeOfDogs().equals(typeDog)) {
                filteredlist.add(meeting);
            }
        }
        if (add.equals("true")) {
            if (TextUtils.isEmpty(lastType)
                    || !lastType.equals(typeDog)) filterTypeList.addAll(filteredlist);
        }
        else filterTypeList.removeAll(filteredlist);
        lastType=typeDog;
        updateList.clear();
        updateList.addAll(filterTypeList);
        if (filter) {
            switch (typeFilter){
                case "typeDog":
                    meetingAdapter.filterList(filterTypeList);
                    break;
                case "date":
                    getDateMeet(true);
                    break;
                case "my":
                    getMyMeet(true);
                    break;
            }
        }
        else getDateMeet(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (clickBtn==0) filterTypeList.clear();
        switch (view.getId()){
            case R.id.anyBtn:
                clickBtn(anyBtn);
                break;
            case R.id.bigBtn:
                clickBtn(bigBtn);
                break;
            case R.id.middleBtn:
                clickBtn(middleBtn);
                break;
            case R.id.smallBtn:
                clickBtn(smallBtn);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void clickBtn (Button button){
        if (button.getTag().equals("false")){
            button.setBackground(getResources().getDrawable(R.drawable.incoming));
            button.setTag("true");
            clickBtn=clickBtn+1;

        }
        else {
            button.setBackground(getResources().getDrawable(R.drawable.btn));
            button.setTag("false");
            clickBtn=clickBtn-1;
        }
        add=button.getTag().toString();
        typeDog=button.getText().toString();
        if (clickBtn==0 || clickBtn==4) {
            filterTypeList.clear();
            if (clickBtn==4){
                clickBtn=0;
                anyBtn.setBackground(getResources().getDrawable(R.drawable.btn));
                anyBtn.setTag("false");
                bigBtn.setBackground(getResources().getDrawable(R.drawable.btn));
                bigBtn.setTag("false");
                middleBtn.setBackground(getResources().getDrawable(R.drawable.btn));
                middleBtn.setTag("false");
                smallBtn.setBackground(getResources().getDrawable(R.drawable.btn));
                smallBtn.setTag("false");
            }
        }
        update();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void update(){
        updateList.clear();
        updateList.addAll(meetings);
        if (!checkedMy.isChecked() && clickBtn==0 &&
                TextUtils.isEmpty(dateFilter.getText().toString())){
            meetingAdapter.filterList(meetings);
        }
        else if (checkedMy.isChecked() && clickBtn==0 &&
                TextUtils.isEmpty(dateFilter.getText().toString())){
            getMyMeet(true);
        }
        else if (!checkedMy.isChecked() && clickBtn==0 &&
                !TextUtils.isEmpty(dateFilter.getText().toString())){
            getDateMeet(true);
        }
        else if (!checkedMy.isChecked() && clickBtn!=0 &&
                TextUtils.isEmpty(dateFilter.getText().toString())){
            getTypeDog(true, "typeDog");
        }
        else if (checkedMy.isChecked() && clickBtn==0 &&
                !TextUtils.isEmpty(dateFilter.getText().toString())){
            getMyMeet(false);
        }
        else if (!checkedMy.isChecked() && clickBtn!=0 &&
                !TextUtils.isEmpty(dateFilter.getText().toString())){
            getTypeDog(true, "date");
        }
        else if (checkedMy.isChecked() && clickBtn!=0 &&
                TextUtils.isEmpty(dateFilter.getText().toString())){
            getTypeDog(true, "my");
        }
        else if (checkedMy.isChecked() && clickBtn!=0 &&
                !TextUtils.isEmpty(dateFilter.getText().toString())){
            getTypeDog(false, "date");
        }

    }

}