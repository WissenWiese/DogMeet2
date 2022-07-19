package com.example.dogmeet.Fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Activity.MeetingActivity;
import com.example.dogmeet.Dictionary;
import com.example.dogmeet.R;
import com.example.dogmeet.adapter.MessageAdapter;
import com.example.dogmeet.adapter.UserAdapter;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.Message;
import com.example.dogmeet.entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.ValueEventRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsFragment extends Fragment {
    private DatabaseReference myMeet;
    String meetUid;
    private ArrayList<Message> messageArrayList, mMeesages;
    private RecyclerView commentView;
    private MessageAdapter messageAdapter;
    private View view;
    Map<String, User>  usersDictionary;



    public static CommentsFragment newInstance(String meetUid) {
        CommentsFragment сommentsFragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString("MeetUid", meetUid);
        сommentsFragment.setArguments(args);
        return сommentsFragment;
    }

    public CommentsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        meetUid = getArguments().getString("MeetUid", "");
        usersDictionary = new HashMap<String, User>();
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("Users");

        ValueEventListener meetListener = new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    User user =dataSnapshot.getValue(User.class);
                    assert user!=null;
                    user.setName(user.getName());
                    user.setAvatarUri(user.getAvatarUri());
                    usersDictionary.put(dataSnapshot.getKey(), user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        users.addListenerForSingleValueEvent(meetListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comments, container, false);
        commentView=view.findViewById(R.id.commentsView);

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");

        messageArrayList=new ArrayList<>();
        mMeesages=new ArrayList<>();
        messageAdapter= new MessageAdapter(mMeesages);

        commentView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentView.setHasFixedSize(true);
        commentView.setItemAnimator(new DefaultItemAnimator());
        commentView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        commentView.setAdapter(messageAdapter);

        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(messageArrayList.size() > 0) messageArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Message message1 =dataSnapshot.getValue(Message.class);
                    assert message1 !=null;
                    /*User user=getUser(message1.getUser());
                    User user=usersDictionary.get(message1.getUser());
                    if (user!=null) {
                        message1.setUserName(user.getName());
                        message1.setUserImage(user.getAvatarUri());
                    }*/
                    messageArrayList.add(message1);
                }
                getUser();
                //messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMeet.child(meetUid).child("comments").addValueEventListener(messageListener);

        return view;
    }

    public void getUser(){
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("Users");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMeesages.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    for(Message message : messageArrayList) {
                        if (dataSnapshot.getKey().equals(message.getUser())) {
                            message.setUserName(user.getName());
                            message.setUserImage(user.getAvatarUri());
                            mMeesages.add(message);
                        }
                    }
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        users.addListenerForSingleValueEvent(userListener);
    }
}