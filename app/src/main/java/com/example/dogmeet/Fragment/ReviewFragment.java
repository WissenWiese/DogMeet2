package com.example.dogmeet.Fragment;

import static com.example.dogmeet.Constant.URI;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Dictionary;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.adapter.UserAdapter;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewFragment extends Fragment implements RecyclerViewInterface {
    private TextView meetDescription, meetNumber, meetCreator;
    private Button button;
    private CardView cardView;
    ImageView meetCreat;
    private int member_number;
    String meetUid, uid, creatorUid;
    private ArrayList<User> mUsers;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    Meeting meeting;
    View view;
    private DatabaseReference myMeet, ref;
    User mUser=new User();

    public static ReviewFragment newInstance(String meetUid, String creatorUid) {
        ReviewFragment reviewFragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString("MeetUid", meetUid);
        args.putString("CreatorUid", creatorUid);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_review, container, false);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        getUser();

        myMeet = FirebaseDatabase.getInstance().getReference("meeting");

        button=view.findViewById(R.id.button);
        meetCreator = view.findViewById(R.id.meetCreator);
        meetDescription=view.findViewById(R.id.meetDescription);
        meetNumber=view.findViewById(R.id.meetNumber);
        cardView=view.findViewById(R.id.cardView);
        meetCreat=view.findViewById(R.id.imageCreat);

        getData();
        setButton();
        getListMember();

        mUsers = new ArrayList<>();

        recyclerView=view.findViewById(R.id.recycler_view_user);
        recyclerView.setHasFixedSize(true);

        userAdapter= new UserAdapter(mUsers, this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(userAdapter);

        return view;
    }

    @Override
    public void OnItemClick(int position) {

    }

    public void getData() {

        ValueEventListener meetingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                meeting = snapshot.getValue(Meeting.class);
                assert meeting!=null;
                User creator=meeting.creator;
                assert creator!=null;
                meetCreator.setText(creator.getName());
                if (creator.getAvatarUri()!=null) {
                    Glide.with(meetCreat.getContext()).load(creator.getAvatarUri()).into(meetCreat);
                }
                else {
                    Glide.with(meetCreat.getContext()).load(URI).into(meetCreat);
                }
                meetDescription.setText(meeting.description);
                member_number=meeting.numberMember;
                meetNumber.setText(Integer.toString(member_number));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMeet.child(meetUid).addValueEventListener(meetingListener);
    }

    public void setButton(){
        if (creatorUid.equals(uid)){
            button.setVisibility(View.GONE);
        }
        else {
            button.setText("Присоединиться");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button.getText().equals("Присоединиться")){
                    myMeet.child(meetUid).child("members").child(uid).setValue(mUser);
                    int member_number1=member_number+1;
                    myMeet.child(meetUid).child("numberMember").setValue(member_number1);
                    button.setText("Покинуть");
                    button.setBackground(getResources().getDrawable(R.drawable.btn2));
                }
                else if (button.getText().equals("Покинуть")){
                    ref.removeValue();
                    int member_number1=member_number-1;
                    myMeet.child(meetUid).child("numberMember").setValue(member_number1);
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
                    if (dataSnapshot.getKey().equals(uid) ) {
                        ref = dataSnapshot.getRef();
                        button.setText("Покинуть");
                        button.setBackground(getResources().getDrawable(R.drawable.btn2));

                    }
                    User member=dataSnapshot.getValue(User.class);
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
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("Users");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    mUser.setName(user.getName());
                    mUser.setAvatarUri(user.getAvatarUri());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        users.child(uid).addListenerForSingleValueEvent(userListener);
    }
}
