package com.example.dogmeet;

import androidx.annotation.NonNull;

import com.example.dogmeet.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Dictionary{
    Map<String, User>  usersDictionary;
    public Dictionary(){
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

    public User getUser(String key){return usersDictionary.get(key);}


}
