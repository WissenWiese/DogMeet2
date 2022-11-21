package com.example.dogmeet.Fragment.ListMeet;

import android.os.Build;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.dogmeet.Fragment.Map.PlaceFragment;
import com.example.dogmeet.model.Meeting;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class MeetingData {

    private final DatabaseReference myMeet;
    private ListMeetFragment view;
    private ArrayList<Meeting> meetings;

    public MeetingData(DatabaseReference myMeet) {
        this.myMeet=myMeet;
    }

    public void attachView(ListMeetFragment listMeetFragment) {
        view=listMeetFragment;
    }

    public void loadMeetings() {
        meetings = new ArrayList<>();
        ValueEventListener meetListener = new ValueEventListener()  {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(meetings.size() > 0) meetings.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                        Meeting meeting =dataSnapshot.getValue(Meeting.class);
                        assert meeting != null;
                        String uidMeet = dataSnapshot.getKey();
                        meeting.setUid(uidMeet);
                        meetings.add(meeting);
                }
                view.showListMeet(meetings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        myMeet.addValueEventListener(meetListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void filterMeetings( LocalDate minDate, LocalDate maxDate, ArrayList<String> meetList,
                               ArrayList<String> sizeList, ArrayList<String> typeList) {
        ArrayList<Meeting> filteredList = new ArrayList<>();
        LocalDate dtMeet = null;
        for (Meeting meeting : meetings) {
            String date = DateFormat.format("dd.MM.yyyy", meeting.getDate()).toString();
            SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
            try {
                Date d = f.parse(date);
                dtMeet = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (ParseException e) {
                e.printStackTrace();
            }
           if (minDate==null && meetList.isEmpty() && sizeList.isEmpty() && typeList.isEmpty()){
               filteredList=meetings;
               break;
           }
           else if (minDate==null && !meetList.isEmpty() && sizeList.isEmpty() && typeList.isEmpty()){
               for (String uid:meetList){
                   if (meeting.uid.equals(uid)) filteredList.add(meeting);
               }
            }
           else if (minDate==null && meetList.isEmpty() && !sizeList.isEmpty() && typeList.isEmpty()){
               for (String size:sizeList){
                   if (meeting.typeOfDogs.equals(size)) filteredList.add(meeting);
               }
           }
           else if (minDate==null && meetList.isEmpty() && sizeList.isEmpty() && !typeList.isEmpty()){
               for (String type:typeList){
                   if (meeting.typeOfMeet.equals(type)) filteredList.add(meeting);
               }
           }
           else if (minDate==null && !meetList.isEmpty() && !sizeList.isEmpty() && typeList.isEmpty()){
               for (String uid:meetList){
                   if (meeting.uid.equals(uid)){
                       for (String size: sizeList){
                           if (meeting.typeOfDogs.equals(size)) filteredList.add(meeting);
                       }
                   }
               }
           }
           else if (minDate==null && !meetList.isEmpty() && sizeList.isEmpty() && !typeList.isEmpty()){
               for (String uid:meetList){
                   if (meeting.uid.equals(uid)){
                       for (String type: typeList){
                           if (meeting.typeOfMeet.equals(type)) filteredList.add(meeting);
                       }
                   }
               }
           }
           else if (minDate==null && meetList.isEmpty() && !sizeList.isEmpty() && !typeList.isEmpty()){
               for (String size:sizeList){
                   if (meeting.typeOfDogs.equals(size)){
                       for (String type: typeList){
                           if (meeting.typeOfMeet.equals(type)) filteredList.add(meeting);
                       }
                   }
               }
           }
           else if (minDate==null && !meetList.isEmpty() && !sizeList.isEmpty() && !typeList.isEmpty()){
               for (String uid:meetList){
                   if (meeting.uid.equals(uid)){
                       for (String size: sizeList){
                           if (meeting.typeOfDogs.equals(size)) {
                               for (String type: typeList){
                                   if (meeting.typeOfMeet.equals(type)) filteredList.add(meeting);
                               }
                           }
                       }
                   }
               }
           }
           else if (minDate!=null && meetList.isEmpty() && sizeList.isEmpty() && typeList.isEmpty()){
               if (dtMeet.isEqual(minDate)) {
                   filteredList.add(meeting);
               }
               else if ((dtMeet.isBefore(maxDate) || dtMeet.isEqual(maxDate))
                       && dtMeet.isAfter(minDate)){
                   filteredList.add(meeting);
               }
           }
           else if (minDate!=null && !meetList.isEmpty() && sizeList.isEmpty() && typeList.isEmpty()){
                for (String uid:meetList){
                    if (meeting.uid.equals(uid)) {
                        if (dtMeet.isEqual(minDate)) {
                            filteredList.add(meeting);
                        }
                        else if ((dtMeet.isBefore(maxDate) || dtMeet.isEqual(maxDate))
                                && dtMeet.isAfter(minDate)){
                            filteredList.add(meeting);
                        }
                    }
                }
            }
           else if (minDate!=null && meetList.isEmpty() && !sizeList.isEmpty() && typeList.isEmpty()){
                for (String size:sizeList){
                    if (meeting.typeOfDogs.equals(size)) {
                        if (dtMeet.isEqual(minDate)) {
                            filteredList.add(meeting);
                        }
                        else if ((dtMeet.isBefore(maxDate) || dtMeet.isEqual(maxDate))
                            && dtMeet.isAfter(minDate)){
                            filteredList.add(meeting);
                        }
                    }
                }
            }
           else if (minDate!=null && meetList.isEmpty() && sizeList.isEmpty() && !typeList.isEmpty()){
                for (String type:typeList){
                    if (meeting.typeOfMeet.equals(type)) {
                        if (dtMeet.isEqual(minDate)) {
                            filteredList.add(meeting);
                        }
                        else if ((dtMeet.isBefore(maxDate) || dtMeet.isEqual(maxDate))
                                && dtMeet.isAfter(minDate)){
                            filteredList.add(meeting);
                        }
                    }
                }
            }
           else if (minDate!=null && !meetList.isEmpty() && !sizeList.isEmpty() && typeList.isEmpty()){
                for (String uid:meetList){
                    if (meeting.uid.equals(uid)){
                        for (String size: sizeList){
                            if (meeting.typeOfDogs.equals(size)) {
                                if (dtMeet.isEqual(minDate)) {
                                    filteredList.add(meeting);
                                }
                                else if ((dtMeet.isBefore(maxDate) || dtMeet.isEqual(maxDate))
                                        && dtMeet.isAfter(minDate)){
                                    filteredList.add(meeting);
                                }
                            }
                        }
                    }
                }
            }
           else if (minDate!=null && !meetList.isEmpty() && sizeList.isEmpty() && !typeList.isEmpty()){
                for (String uid:meetList){
                    if (meeting.uid.equals(uid)){
                        for (String type: typeList){
                            if (meeting.typeOfMeet.equals(type)) {
                                if (dtMeet.isEqual(minDate)) {
                                    filteredList.add(meeting);
                                }
                                else if ((dtMeet.isBefore(maxDate) || dtMeet.isEqual(maxDate))
                                        && dtMeet.isAfter(minDate)){
                                    filteredList.add(meeting);
                                }
                            }
                        }
                    }
                }
            }
           else if (minDate!=null && meetList.isEmpty() && !sizeList.isEmpty() && !typeList.isEmpty()){
                for (String size:sizeList){
                    if (meeting.typeOfDogs.equals(size)){
                        for (String type: typeList){
                            if (meeting.typeOfMeet.equals(type)){
                                if (dtMeet.isEqual(minDate)) {
                                    filteredList.add(meeting);
                                }
                                else if ((dtMeet.isBefore(maxDate) || dtMeet.isEqual(maxDate))
                                        && dtMeet.isAfter(minDate)){
                                    filteredList.add(meeting);
                                }
                            }
                        }
                    }
                }
            }
           else if (minDate!=null && !meetList.isEmpty() && !sizeList.isEmpty() && !typeList.isEmpty()){
                for (String uid:meetList){
                    if (meeting.uid.equals(uid)){
                        for (String size: sizeList){
                            if (meeting.typeOfDogs.equals(size)) {
                                for (String type: typeList){
                                    if (meeting.typeOfMeet.equals(type)){
                                        if (dtMeet.isEqual(minDate)) {
                                            filteredList.add(meeting);
                                        }
                                        else if ((dtMeet.isBefore(maxDate) || dtMeet.isEqual(maxDate))
                                                && dtMeet.isAfter(minDate)){
                                            filteredList.add(meeting);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        view.showFilteredList(filteredList);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortList(String type){
        view.sort(type);
    }

    public void getMeetings(ArrayList<String> meetingsUid, PlaceFragment view){
        ArrayList<Meeting> mMeetings=new ArrayList();
        ValueEventListener meetListener = new ValueEventListener()  {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mMeetings.size() > 0) mMeetings.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Meeting meeting =dataSnapshot.getValue(Meeting.class);
                    assert meeting != null;
                    String uidMeet = dataSnapshot.getKey();
                    meeting.setUid(uidMeet);
                    for (String meetingUid: meetingsUid){
                        if (meeting.getUid().equals(meetingUid)) mMeetings.add(meeting);
                    }
                }
                view.setMeeting(mMeetings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        myMeet.addValueEventListener(meetListener);
    }
}
