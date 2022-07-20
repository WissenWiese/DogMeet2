package com.example.dogmeet.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Activity.MeetingActivity;
import com.example.dogmeet.Dictionary;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.adapter.MessageAdapter;
import com.example.dogmeet.adapter.UserAdapter;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.Message;
import com.example.dogmeet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.ValueEventRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsFragment extends Fragment implements RecyclerViewInterface {
    private DatabaseReference myMeet;
    String meetUid, uid;
    private ArrayList<Message> messageArrayList;
    private RecyclerView commentView;
    private MessageAdapter messageAdapter;
    private View view;
    private int numberComment;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comments, container, false);
        commentView=view.findViewById(R.id.commentsView);

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        messageArrayList=new ArrayList<>();
        messageAdapter= new MessageAdapter(messageArrayList, this);

        commentView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentView.setHasFixedSize(true);
        commentView.setItemAnimator(new DefaultItemAnimator());
        commentView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        commentView.setAdapter(messageAdapter);

        getUser();

        return view;
    }

    public void getUser(){
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("Users");

        usersDictionary=new HashMap<String, User>();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (usersDictionary.size()>0) usersDictionary.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user =dataSnapshot.getValue(User.class);
                    assert user!=null;
                    user.setName(user.getName());
                    user.setAvatarUri(user.getAvatarUri());
                    usersDictionary.put(dataSnapshot.getKey(), user);
                }
                getMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        users.addValueEventListener(userListener);
    }

    public void getMessage(){
        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(messageArrayList.size() > 0) messageArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Message message1 =dataSnapshot.getValue(Message.class);
                    assert message1 !=null;
                    User user=usersDictionary.get(message1.getUser());
                    if (user!=null) {
                        message1.setUserName(user.getName());
                        message1.setUserImage(user.getAvatarUri());
                    }
                    message1.setUid(dataSnapshot.getKey());
                    messageArrayList.add(message1);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMeet.child(meetUid).child("comments").addValueEventListener(messageListener);
    }

    @Override
    public void OnItemClick(int position) {
        Message message=messageArrayList.get(position);
        if (message.getUser().equals(uid)){
            new AlertDialog.Builder(getContext())
                    .setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference ref= myMeet.child(meetUid).child("comments").child(message.getUid());
                            ref.removeValue();
                            DatabaseReference comments= myMeet.child(meetUid).child("numberComments");
                            comments.setValue(messageArrayList.size()-1);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void OnButtonClick(int position) {

    }
}