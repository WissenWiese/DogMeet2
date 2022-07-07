package com.example.dogmeet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.entity.User;
import com.example.dogmeet.mainActivity.AddActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dogmeet.databinding.ActivityNavigatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;

public class NavigatActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigatBinding binding;
    private FloatingActionButton addbtn;
    private ImageView avatar;
    private TextView name;
    DatabaseReference database;
    DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addbtn=findViewById(R.id.fab);

        setSupportActionBar(binding.appBarNavigat.toolbar);
        binding.appBarNavigat.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_list_meet, R.id.nav_map, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigat);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header=navigationView.getHeaderView(0);
        avatar=header.findViewById(R.id.imageView);
        name=header.findViewById(R.id.textView);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference();
        user = database.child("Users").child(auth.getUid());
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user1 = dataSnapshot.getValue(User.class);
                name.setText(user1.getName());
                if (user1.getAvatarUri()!=null){
                    Glide.with(avatar.getContext()).load(user1.getAvatarUri()).into(avatar);
                }
                else {
                    Glide.with(avatar.getContext()).load(Constant.URI).into(avatar);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(NavigatActivity.this, AddActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigat);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}