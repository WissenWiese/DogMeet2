package com.example.dogmeet.Fragment.Map;

import static com.example.dogmeet.Constant.URI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Activity.ProfileUsersActivity;
import com.example.dogmeet.Chat.ChatActivity;
import com.example.dogmeet.Constant;
import com.example.dogmeet.Meeting.UserAdapter;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.model.Message;
import com.example.dogmeet.model.Pet;
import com.example.dogmeet.model.User;
import com.example.dogmeet.model.Walker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class WalkerMarker extends Fragment{
    private View view;
    private String walkerUid;
    private ImageView photo;
    private TextView name, message;
    private Button chat;
    private RecyclerView recyclerView;
    private DatabaseReference walkers;
    private ArrayList<Pet> mPets;
    private PetAdapterMini petAdapterMini;
    private ArrayList<Integer> selected = new ArrayList<>();

    public WalkerMarker() {

    }

    public static WalkerMarker newInstance(String walkerUid) {
        WalkerMarker fragment = new WalkerMarker();
        Bundle args = new Bundle();
        args.putString("UID", walkerUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        walkerUid=getArguments().getString("UID", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_walker_marker, container, false);
        photo= view.findViewById(R.id.photoWalker);
        name= view.findViewById(R.id.nameWalker);
        message= view.findViewById(R.id.textViewMessage);
        chat=view.findViewById(R.id.buttonMessage);

        mPets = new ArrayList<>();

        recyclerView=view.findViewById(R.id.rvPetsWalk);
        recyclerView.setHasFixedSize(true);
        petAdapterMini= new PetAdapterMini(mPets);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(petAdapterMini);

        walkers= FirebaseDatabase.getInstance().getReference("walker").child(walkerUid);
        WalkerData walkerData=new WalkerData(walkers);
        walkerData.getWalker(this);

        /*ValueEventListener dListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                {
                    Walker walker=snapshot.getValue(Walker.class);
                    if (walker!=null){
                        if(walker.getUserUri()!=null){
                            Glide.with(photo.getContext()).load(walker.getUserUri()).into(photo);
                        }
                        else {
                            Glide.with(photo.getContext()).load(URI).into(photo);
                        }
                        if (walker.getMassage()!=null){
                            message.setVisibility(View.VISIBLE);
                            message.setText(walker.getMassage());
                        }
                        name.setText(walker.getUserName());
                    }
                    if(mPets.size() > 0)mPets.clear();
                    for (DataSnapshot dataSnapshot: snapshot.child("pets").getChildren()){
                        Pet pet =dataSnapshot.getValue(Pet.class);
                        assert pet != null;
                        pet.setPetUid(dataSnapshot.getKey());
                        mPets.add(pet);
                    }
                    petAdapterMini.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        walkers.addListenerForSingleValueEvent(dListener);*/

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ChatActivity.class);
                i.putExtra(Constant.USER_UID, walkerUid);
                startActivity(i);
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ProfileUsersActivity.class);
                i.putExtra(Constant.USER_UID, walkerUid);
                startActivity(i);
            }
        });
        return view;
    }

    public void setWalker(Walker walker){
        if(walker.getUserUri()!=null){
            Glide.with(photo.getContext()).load(walker.getUserUri()).into(photo);
        }
        else {
            Glide.with(photo.getContext()).load(URI).into(photo);
        }
        if (walker.getMassage()!=null){
            message.setVisibility(View.VISIBLE);
            message.setText(walker.getMassage());
        }
        name.setText(walker.getUserName());
        petAdapterMini.setList(walker.getPets());
        mPets= walker.getPets();
    }
}