package com.example.dogmeet.Meeting;

import static com.example.dogmeet.Constant.URI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Activity.ProfileUsersActivity;
import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.model.Meeting;
import com.example.dogmeet.model.Message;
import com.example.dogmeet.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReviewFragment extends Fragment implements RecyclerViewInterface {
    private TextView meetDescription, meetNumber, meetCreator, typeOfDogs, ratingTextView;
    private Button button;
    private CardView cardView;
    ImageView meetCreat;
    private int member_number;
    String meetUid, uid, creatorUid, database;
    private ArrayList<User> mUsers;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    Meeting meeting;
    View view;
    private DatabaseReference myMeet, ref, users;
    Map<String, User> usersDictionary;
    long date, dateMeet;
    float rating;
    RatingBar ratingBar;

    public static ReviewFragment newInstance(String meetUid, String creatorUid, String database) {
        ReviewFragment reviewFragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString("MeetUid", meetUid);
        args.putString("CreatorUid", creatorUid);
        args.putString("Database", database);
        reviewFragment.setArguments(args);
        return reviewFragment;
    }


    public ReviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        meetUid = getArguments().getString("MeetUid", "");
        creatorUid = getArguments().getString("CreatorUid", "");
        database = getArguments().getString("Database", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_review, container, false);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (database.equals("meeting")){
            myMeet = FirebaseDatabase.getInstance().getReference("meeting");
        }
        else {
            myMeet= FirebaseDatabase.getInstance().getReference("archive").child("meeting");
        }

        users= FirebaseDatabase.getInstance().getReference("Users");

        button=view.findViewById(R.id.button);
        meetCreator = view.findViewById(R.id.meetCreator);
        meetDescription=view.findViewById(R.id.meetDescription);
        meetNumber=view.findViewById(R.id.meetNumber);
        typeOfDogs=view.findViewById(R.id.meetTypeOfDogs);
        cardView=view.findViewById(R.id.cardView);
        meetCreat=view.findViewById(R.id.imageCreat);
        ratingTextView=view.findViewById(R.id.ratingTextView);
        ratingBar=view.findViewById(R.id.ratingBar);

        mUsers = new ArrayList<>();

        recyclerView=view.findViewById(R.id.recycler_view_user);
        recyclerView.setHasFixedSize(true);

        userAdapter= new UserAdapter(mUsers, this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(userAdapter);

        date=new Date().getTime();

        getUser();

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!creatorUid.equals(uid)){
                    Intent i = new Intent(getContext(), ProfileUsersActivity.class);
                    i.putExtra(Constant.USER_UID, creatorUid);
                    startActivity(i);
                }
            }
        });

        return view;
    }

    @Override
    public void OnItemClick(int position) {
        User user=mUsers.get(position);
        if (!user.getUid().equals(uid)){
            Intent i = new Intent(getContext(), ProfileUsersActivity.class);
            i.putExtra(Constant.USER_UID, user.getUid());
            startActivity(i);
        }
    }

    @Override
    public void OnButtonClick(int position) {

    }

    public void getData() {

        ValueEventListener meetingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                meeting = snapshot.getValue(Meeting.class);
                assert meeting!=null;
                User creator=usersDictionary.get(creatorUid);
                assert creator!=null;
                meetCreator.setText(creator.getName());
                if (creator.getAvatarUri()!=null) {
                    Glide.with(meetCreat.getContext()).load(creator.getAvatarUri()).into(meetCreat);
                }
                else {
                    Glide.with(meetCreat.getContext()).load(URI).into(meetCreat);
                }

                switch (meeting.typeOfDogs){
                    case "Любой":
                        typeOfDogs.setText("Любых пород");
                        break;
                    case "Большой":
                        typeOfDogs.setText("Больших пород");
                        break;
                    case "Средний":
                        typeOfDogs.setText("Средних пород");
                        break;
                    case "Маленький":
                        typeOfDogs.setText("Маленьких пород");
                        break;
                }
                meetDescription.setText(meeting.description);
                member_number=meeting.numberMember;
                dateMeet=meeting.getDate();
                if (TextUtils.isEmpty(meeting.getRating())){
                    rating=0;
                }
                else {
                    rating=Float.valueOf(meeting.getRating());
                    for (DataSnapshot dataSnapshot: snapshot.child("ratingList").getChildren()){
                        if (uid.equals(dataSnapshot.getKey())) {
                            String rating1 = dataSnapshot.getValue(String.class);
                            ratingBar.setRating(Float.valueOf(rating1));
                        }
                    }
                }
                meetNumber.setText(Integer.toString(member_number));
                setButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMeet.child(meetUid).addValueEventListener(meetingListener);
    }

    public void setButton(){

        if (creatorUid.equals(uid) || dateMeet<date){
            button.setVisibility(View.GONE);
            //users.child(uid).child("Meeting").push().setValue(meetUid);
        }
        else {
            button.setVisibility(View.VISIBLE);
            button.setText("Присоединиться");
            ratingTextView.setVisibility(View.INVISIBLE);
            ratingBar.setVisibility(View.INVISIBLE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button.getText().equals("Присоединиться")){
                    myMeet.child(meetUid).child("members").push().setValue(uid);
                    int member_number1=member_number+1;
                    myMeet.child(meetUid).child("numberMember").setValue(member_number1);
                    users.child(uid).child("myMeetings").child(meetUid).child("date").setValue(date);
                    button.setText("Покинуть");
                    button.setBackground(getResources().getDrawable(R.drawable.btn2));
                }
                else if (button.getText().equals("Покинуть")){
                    ref.removeValue();
                    int member_number1=member_number-1;
                    myMeet.child(meetUid).child("numberMember").setValue(member_number1);
                    users.child(uid).child("myMeetings").child(meetUid).removeValue();
                    button.setText("Присоединиться");
                    button.setBackground(getResources().getDrawable(R.drawable.btn));
                }
            }
        });

    }

    public void getListMember(){
        ValueEventListener memberListener = new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(mUsers.size() > 0)mUsers.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String memberUid=dataSnapshot.getValue(String.class);
                    if (memberUid.equals(uid)) {
                        ref = dataSnapshot.getRef();
                        if (dateMeet<date){
                            button.setVisibility(View.INVISIBLE);
                            ratingTextView.setVisibility(View.VISIBLE);
                            ratingBar.setVisibility(View.VISIBLE);
                            getRating();
                        }
                        else {
                            button.setText("Покинуть");
                            button.setBackground(getResources().getDrawable(R.drawable.btn2));
                            button.setVisibility(View.VISIBLE);
                            ratingTextView.setVisibility(View.INVISIBLE);
                            ratingBar.setVisibility(View.INVISIBLE);
                        }
                    }
                    User member = usersDictionary.get(memberUid);
                    assert member != null;
                    mUsers.add(member);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMeet.child(meetUid).child("members").addValueEventListener(memberListener);
    }

    public void getUser(){
        usersDictionary=new HashMap<String, User>();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (usersDictionary.size()>0) usersDictionary.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user =dataSnapshot.getValue(User.class);
                    assert user!=null;
                    user.setUid(dataSnapshot.getKey());
                    user.setName(user.getName());
                    user.setAvatarUri(user.getAvatarUri());
                    usersDictionary.put(dataSnapshot.getKey(), user);
                }
                getData();
                getListMember();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        users.addValueEventListener(userListener);
    }

    public void getRating(){

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                users.child(uid).child("myMeetings").child(meetUid).child("rating").setValue(String.valueOf(v));
                myMeet.child(meetUid).child("ratingList").child(uid).setValue(String.valueOf(v));
                if (rating!=0) {
                    rating=(rating+v)/2;
                }
                else {
                    rating=v;
                }
                myMeet.child(meetUid).child("rating").setValue(String.valueOf(rating));
            }
        });
    }
}
