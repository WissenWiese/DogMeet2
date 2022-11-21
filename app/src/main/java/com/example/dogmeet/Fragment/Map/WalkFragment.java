package com.example.dogmeet.Fragment.Map;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dogmeet.Fragment.ListMeet.MeetingData;
import com.example.dogmeet.Meeting.CommentsFragment;
import com.example.dogmeet.Meeting.UserAdapter;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.mainActivity.AddActivity;
import com.example.dogmeet.model.Pet;
import com.example.dogmeet.model.User;
import com.example.dogmeet.model.Walker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;


public class WalkFragment extends Fragment implements RecyclerViewInterface {
    private Double latitude, longitude;
    private Boolean isWalk;
    private Button walkAction;
    private View view;
    private Walker walker;
    private DatabaseReference users, pets;
    private RecyclerView recyclerView;
    private ArrayList<Pet> mPets, selectedPets;
    private PetsAdapter petsAdapter;
    private TextView textPets;
    private EditText editMessage;
    private ArrayList<Integer> selected = new ArrayList<>();
    private String uid;

    public WalkFragment() {

    }

    public static WalkFragment newInstance(Double latitude, Double longitude, Boolean isWalk) {
        WalkFragment fragment = new WalkFragment();
        Bundle args = new Bundle();
        args.putDouble("Latitude", latitude);
        args.putDouble("Longitude", longitude);
        args.putBoolean("isWalk", isWalk);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latitude = getArguments().getDouble("Latitude", 0);
        longitude = getArguments().getDouble("Longitude", 0);
        isWalk=getArguments().getBoolean("isWalk", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_walk, container, false);
        walkAction=view.findViewById(R.id.actionWalk);
        textPets=view.findViewById(R.id.textViewPets);
        editMessage=view.findViewById(R.id.editWalkerMessage);
        mPets = new ArrayList<>();
        selectedPets=new ArrayList<>();

        recyclerView=view.findViewById(R.id.recycler_view_pets);
        recyclerView.setHasFixedSize(true);

        if (isWalk){
            walkAction.setText("Завершить прогулку");
            recyclerView.setVisibility(View.GONE);
            textPets.setVisibility(View.GONE);
            editMessage.setVisibility(View.GONE);
        }
        else {
            walkAction.setText("Начать прогулку");
            editMessage.setVisibility(View.VISIBLE);
        }

        petsAdapter= new PetsAdapter(mPets, this, selected);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(petsAdapter);

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        pets=FirebaseDatabase.getInstance().getReference("Users").child(uid).child("pets");

        pets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mPets.size() > 0)mPets.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Pet pet =dataSnapshot.getValue(Pet.class);
                    assert pet != null;
                    pet.setPetUid(dataSnapshot.getKey());
                    mPets.add(pet);
                }
                petsAdapter.notifyDataSetChanged();
                if (mPets.size()>1 && walkAction.getText().equals("Начать прогулку")){
                    textPets.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                else {
                    textPets.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        walkAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (walkAction.getText().equals("Начать прогулку")){
                    walker=new Walker();

                    walker.setLatitude(String.valueOf(latitude));
                    walker.setLongitude(String.valueOf(longitude));
                    isWalk=true;

                    users = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            assert user!=null;
                            walker.setUserName(user.getName());
                            walker.setUserUri(user.getAvatarUri());
                            if (!TextUtils.isEmpty(editMessage.getText().toString())){
                                walker.setMassage(editMessage.getText().toString());
                            }
                            if (selectedPets.size()>0){
                                walker.setPets(selectedPets);
                            }
                            else if (mPets.size()==1) {
                                walker.setPets(mPets);
                            }
                            else {
                                Toast.makeText(getContext(), "Выберите питомца", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("walker")
                                    .child(uid)
                                    .setValue(walker).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                walkAction.setText("Завершить прогулку");
                                                recyclerView.setVisibility(View.GONE);
                                                textPets.setVisibility(View.GONE);
                                                editMessage.setVisibility(View.GONE);
                                                Bundle result = new Bundle();
                                                result.putBoolean("isWalk", isWalk);
                                                getParentFragmentManager().setFragmentResult("requestKey", result);
                                            }
                                        }
                                    });

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else if (walkAction.getText().equals("Завершить прогулку")){
                    walkAction.setText("Начать прогулку");
                    editMessage.setVisibility(View.VISIBLE);
                    if (mPets.size()>1){
                        textPets.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    isWalk=false;
                    Bundle result = new Bundle();
                    result.putBoolean("isWalk", isWalk);
                    getParentFragmentManager().setFragmentResult("requestKey", result);
                }

            }
        });

        return view;
    }

    @Override
    public void OnItemClick(int position) {
        if (selected.contains(position)){
            selected.remove(position);
            selectedPets.remove(mPets.get(position));
        }
        else {
            selected.add(position);
            selectedPets.add(mPets.get(position));
        }
        petsAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnButtonClick(int position) {

    }
}