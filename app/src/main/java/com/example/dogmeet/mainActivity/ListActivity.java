package com.example.dogmeet.mainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dogmeet.Constant;
import com.example.dogmeet.Activity.MeetingActivity;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.Meeting;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private DatabaseReference myMeet;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private List<Meeting> listTemp;
    private String uidMeet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Список мероприятий");
        setContentView(R.layout.activity_main);

        init();
        getDataFromDB();
        setOnClickItem();
    }

    private void init()
    {
        listView = findViewById(R.id.listView);
        listData = new ArrayList<>();
        listTemp = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");
    }

    private void getDataFromDB()
    {

        ValueEventListener meetListener = new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(listData.size() > 0)listData.clear();
                if(listTemp.size() > 0)listTemp.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Meeting meeting =dataSnapshot.getValue(Meeting.class);
                    assert meeting != null;
                    meeting.setUid(dataSnapshot.getKey());
                    listData.add(meeting.title);
                    listTemp.add(meeting);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMeet.addValueEventListener(meetListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.add:
                intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                return true;
            case R.id.map:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                return true;
            case R.id.list:
                intent = new Intent(this, ListActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setOnClickItem()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Meeting meeting = listTemp.get(position);
                Intent i = new Intent(ListActivity.this, MeetingActivity.class);
                i.putExtra(Constant.MEETING_UID, meeting.uid);
                i.putExtra(Constant.MEETING_TITLE,meeting.title);
                i.putExtra(Constant.MEETING_DATE,meeting.date);
                i.putExtra(Constant.MEETING_ADDRESS,meeting.address);
                i.putExtra(Constant.MEETING_CREATOR,meeting.creator);
                i.putExtra(Constant.MEETING_CREATOR_UID,meeting.creatorUid);
                i.putExtra(Constant.MEETING_TIME,meeting.time);
                i.putExtra(Constant.MEETING_DESCRIPTION,meeting.description);
                i.putExtra(Constant.MEETING_NUMBER,meeting.nubmerMember);
                startActivity(i);

            }
        });
    }
}