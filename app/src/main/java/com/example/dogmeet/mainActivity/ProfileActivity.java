package com.example.dogmeet.mainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dogmeet.R;
import com.example.dogmeet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    ImageButton buttonEdit, buttonAdd, buttonSave;
    EditText about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        TextView bio=findViewById(R.id.textView);
        buttonEdit=findViewById(R.id.editBtn);
        buttonSave=findViewById(R.id.saveBtn);
        about=findViewById(R.id.editTextAbout);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users").child(auth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                bio.setText(user.getName().toString()+", "+user.getAge().toString());
                if (user.getInfo()!=null){
                    about.setText(user.getInfo());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about.setEnabled(true);
                if (about!=null){
                    buttonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ref.child("info").setValue(about.getText().toString());
                            about.setEnabled(false);
                        }
                    });
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
}