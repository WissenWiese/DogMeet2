package com.example.dogmeet.Fragment.ListMeet;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.example.dogmeet.R;
import com.example.dogmeet.model.Meeting;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class FilterFragment extends Fragment implements View.OnClickListener {
    private View view;
    private Spinner spinner;
    private CheckedTextView checkedMy;
    private ImageButton calendar, dateOff;
    private Button anyBtn, bigBtn, middleBtn, smallBtn,
            walkBtn, dogShowBtn, partyBtn, festivalBtn;
    private TextView dateFilter;
    private String uid, date, dateForFilter, sort;
    int click;
    LocalDate dateMin, dateMax;
    private ArrayList<String> sizeList, typeList, myMeetList, meetList;
    private MeetingData meetingData;

    public FilterFragment() {

    }

    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_filter, container, false);
        meetList=new ArrayList<>();
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("Users");
        ValueEventListener myMeetListener = new ValueEventListener()  {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (meetList.size()>0) meetList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String myMeetUid=dataSnapshot.getKey();
                    meetList.add(myMeetUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        users.child(uid).child("myMeetings").addValueEventListener(myMeetListener);
        init();
        return view;
    }

    public void init(){
        sizeList=new ArrayList<>();
        typeList=new ArrayList<>();
        myMeetList=new ArrayList<>();
        dateMin=null;
        dateMax=null;
        sort=null;

        checkedMy=view.findViewById(R.id.checkedMy);

        checkedMy.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (checkedMy.isChecked()){
                    checkedMy.setChecked(false);
                    checkedMy.setCheckMarkDrawable(getResources()
                            .getDrawable(R.drawable.checkbox));
                    myMeetList.clear();
                    meetingData.filterMeetings(dateMin, dateMax, meetList, sizeList, typeList);
                }
                else {
                    checkedMy.setChecked(true);
                    checkedMy.setCheckMarkDrawable(getResources()
                            .getDrawable(R.drawable.checked_checkbox));
                    myMeetList=meetList;
                    meetingData.filterMeetings(dateMin, dateMax, meetList, sizeList, typeList);
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sort=adapterView.getItemAtPosition(i).toString();
                meetingData.sortList(sort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sort=null;
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
                    meetingData.filterMeetings(dateMin, dateMax, meetList, sizeList, typeList);
                }
            }
        });

        dateOff.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                dateOff.setVisibility(View.INVISIBLE);
                dateFilter.setText(null);
                dateMin=null;
                dateMax=null;
                meetingData.filterMeetings(dateMin, dateMax, meetList, sizeList, typeList);
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

        walkBtn=view.findViewById(R.id.walkBtn);
        walkBtn.setOnClickListener(this);
        dogShowBtn=view.findViewById(R.id.dogShowBtn);
        dogShowBtn.setOnClickListener(this);
        partyBtn=view.findViewById(R.id.partyBtn);
        partyBtn.setOnClickListener(this);
        festivalBtn=view.findViewById(R.id.festivalBtn);
        festivalBtn.setOnClickListener(this);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void showDatePickDlg() {
        dateFilter.setText(null);
        dateForFilter=null;
        dateMin=null;
        dateMax=null;
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
                    dateMin= LocalDate.of(year, monthOfYear, dayOfMonth);
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
    @Override
    public void onClick(View view) {
        if (sizeList.size()==4) sizeList.clear();
        if (typeList.size()==4) typeList.clear();
        switch (view.getId()){
            case R.id.anyBtn:
                setSizeDog(anyBtn);
                break;
            case R.id.bigBtn:
                setSizeDog(bigBtn);
                break;
            case R.id.middleBtn:
                setSizeDog(middleBtn);
                break;
            case R.id.smallBtn:
                setSizeDog(smallBtn);
                break;
            case R.id.walkBtn:
                setType(walkBtn);
                break;
            case R.id.dogShowBtn:
                setType(dogShowBtn);
                break;
            case R.id.partyBtn:
                setType(partyBtn);
                break;
            case R.id.festivalBtn:
                setType(festivalBtn);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    public void setSizeDog(Button button){
        if (button.getTag().equals("false")){
            setTrue(button);
            if (sizeList.size()<3){
                sizeList.add(button.getText().toString());
            }
            else {
                sizeList.clear();
                setFalse(bigBtn);
                setFalse(smallBtn);
                setFalse(middleBtn);
                setFalse(anyBtn);
            }
        }
        else {
            setFalse(button);
            if (sizeList.size()>0){
                sizeList.remove(button.getText().toString());
            }
        }
        meetingData.filterMeetings(dateMin, dateMax, meetList, sizeList, typeList);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setType(Button button){
        if (button.getTag().equals("false")){
            setTrue(button);
            if (typeList.size()<3){
                typeList.add(button.getText().toString());
            }
            else {
                typeList.clear();
                setFalse(walkBtn);
                setFalse(dogShowBtn);
                setFalse(festivalBtn);
                setFalse(partyBtn);
            }
        }
        else {
            setFalse(button);
            if (typeList.size()>0){
                typeList.remove(button.getText().toString());
            }
        }
        meetingData.filterMeetings(dateMin, dateMax, meetList, sizeList, typeList);
    }



    @SuppressLint("ResourceAsColor")
    public void setTrue(Button button){
        button.setBackground(getResources().getDrawable(R.drawable.incoming));
        button.setTextColor(R.color.text);
        button.setTag("true");
    }

    @SuppressLint("ResourceAsColor")
    public void setFalse(Button button){
        button.setBackground(getResources().getDrawable(R.drawable.btn3));
        button.setTag("false");
        button.setTextColor(R.color.primary_dark);
    }

    public void setModel(MeetingData meetingData){
        this.meetingData=meetingData;
    }
}