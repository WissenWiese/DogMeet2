package com.example.dogmeet.Activity;

import static com.example.dogmeet.Constant.URI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.adapter.PetAdapter;
import com.example.dogmeet.entity.Pet;
import com.example.dogmeet.entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileUsersActivity extends AppCompatActivity implements RecyclerViewInterface {
    String uid;
    ImageView avatar;
    TextView bio;
    TextView name;
    Toolbar toolbar;
    private ArrayList<Pet> mPets;
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_users);

        toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileUsersActivity.this.finish();// возврат на предыдущий activity
            }
        });

        init();
        getIntentMain();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        MenuItem messageMenuItem = menu.findItem(R.id.action_message);
        messageMenuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_message:
                Intent i = new Intent(ProfileUsersActivity.this, ChatActivity.class);
                i.putExtra(Constant.USER_UID, uid);
                startActivity(i);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init(){
        avatar=findViewById(R.id.chatAvatar);
        name=findViewById(R.id.text_name);
        bio=findViewById(R.id.text_about_me);
        recyclerView=findViewById(R.id.r_v_pet);

        mPets = new ArrayList<>();

        petAdapter= new PetAdapter(mPets, this, false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(ProfileUsersActivity.this, LinearLayoutManager.HORIZONTAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileUsersActivity.this,LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(petAdapter);
    }

    private void getIntentMain(){
        Intent i = getIntent();
        if(i != null) {
            uid = i.getStringExtra(Constant.USER_UID);
            DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    name.setText(user.getName().toString());
                    bio.setText(user.getInfo());
                    String url = user.getAvatarUri();
                    if (url != null) {
                        Glide.with(avatar.getContext()).load(url).into(avatar);
                    } else {
                        Glide.with(avatar.getContext()).load(URI).into(avatar);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference pets = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("pets");
            pets.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(mPets.size() > 0)mPets.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Pet pet =dataSnapshot.getValue(Pet.class);
                        assert pet != null;
                        mPets.add(pet);
                    }
                    petAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void OnItemClick(int position) {

    }

    @Override
    public void OnButtonClick(int position) {

    }
}