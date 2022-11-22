package com.example.dogmeet.Meeting;

import android.content.Context;
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

import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.entity.Message;
import com.example.dogmeet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsFragment extends Fragment implements RecyclerViewInterface {
    private DatabaseReference myMeet;
    private String meetUid, uid, database, creatorUid;
    private ArrayList<Message> messageArrayList;
    private RecyclerView commentView;
    private CommentAdapter messageAdapter;
    private View view;
    Map<String, User>  usersDictionary;
    private OnDataPass mDataPasser;

    public static CommentsFragment newInstance(String meetUid, String creatorUid, String database) {
        CommentsFragment сommentsFragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString("MeetUid", meetUid);
        args.putString("CreatorUid", creatorUid);
        args.putString("Database", database);
        сommentsFragment.setArguments(args);
        return сommentsFragment;
    }

    public CommentsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        meetUid = getArguments().getString("MeetUid", "");
        creatorUid = getArguments().getString("CreatorUid", "");
        database = getArguments().getString("Database", "");

        usersDictionary = new HashMap<String, User>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comments, container, false);
        commentView=view.findViewById(R.id.commentsView);

        if (database.equals("meeting")){
            myMeet = FirebaseDatabase.getInstance().getReference("meeting");
        }
        else {
            myMeet= FirebaseDatabase.getInstance().getReference("archive").child("meeting");
        }

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        messageArrayList =new ArrayList<>();
        messageAdapter= new CommentAdapter(messageArrayList, this);

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
                    if (message1.getUser()=="Комментарий удален") {

                        message1.setUserName("Комментарий удален");
                    }
                    else {
                        User user=usersDictionary.get(message1.getUser());
                        if (user!=null) {
                            message1.setUserName(user.getName());
                            message1.setUserImage(user.getAvatarUri());
                    }}
                    if (message1.getNumberAnswer()==0 && message1.getUser().equals("Комментарий удален")){
                        DatabaseReference ref = myMeet.child(meetUid).child("comments").child(message1.getUid());
                        ref.removeValue();
                    } else {
                        message1.setUid(dataSnapshot.getKey());
                        messageArrayList.add(message1);
                    }
                    for (DataSnapshot answersSnapshot: dataSnapshot.child("answers").getChildren()){
                        Message answer=answersSnapshot.getValue(Message.class);
                        User user1=usersDictionary.get(answer.getUser());
                        if (user1!=null) {
                            answer.setUserName(user1.getName());
                            answer.setUserImage(user1.getAvatarUri());
                        }
                        answer.setMainUid(dataSnapshot.getKey());
                        answer.setUid(answersSnapshot.getKey());
                        messageArrayList.add(answer);
                    }
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
        Message message = messageArrayList.get(position);
        if (message.getUser().equals(uid) || creatorUid.equals(uid)){
            new AlertDialog.Builder(getContext())
                    .setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (message.getMainUid()!=null) {
                                DatabaseReference ref = myMeet.child(meetUid).child("comments").child(message.getMainUid()).
                                        child("answers").child(message.getUid());
                                ref.removeValue();
                                int numberAnswers=message.getNumberAnswer()-1;
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("meeting")
                                        .child(meetUid)
                                        .child("comments")
                                        .child(message.getMainUid())
                                        .child("numberAnswer")
                                        .setValue(numberAnswers);
                            }
                            else if (message.getNumberAnswer()>0){
                                DatabaseReference ref = myMeet.child(meetUid).child("comments").child(message.getUid());
                                ref.child("user").setValue("Комментарий удален");
                                ref.child("message").setValue("Комментарий удален");
                            }
                            else {
                                DatabaseReference ref = myMeet.child(meetUid).child("comments").child(message.getUid());
                                ref.removeValue();
                            }
                            DatabaseReference comments = myMeet.child(meetUid).child("numberComments");
                            comments.setValue(messageArrayList.size() - 1);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void OnButtonClick(int position) {
        Message message = messageArrayList.get(position);
        String userName= message.getUserName();
        String uidComment;
        int numberAnswer = message.getNumberAnswer();
        if (message.getMainUid() != null) {
            uidComment = message.getMainUid();
        } else {
            uidComment = message.getUid();
        }
        Boolean isAnswer = true;
        if (userName!="Комментарий удален") {
            mDataPasser.onDataPass(userName, uidComment, isAnswer, numberAnswer);
        }
    }

    public interface OnDataPass {
        void onDataPass(String name, String uidComment, Boolean isAnswer, int numberAnswer);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDataPass) {
            mDataPasser = (OnDataPass) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragment1DataListener");
        }
    }


}