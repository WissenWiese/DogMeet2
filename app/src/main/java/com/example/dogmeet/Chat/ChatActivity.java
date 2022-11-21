package com.example.dogmeet.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dogmeet.Constant;
import com.example.dogmeet.MyFirebaseMessagingService;
import com.example.dogmeet.R;
import com.example.dogmeet.model.Message;
import com.example.dogmeet.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String uidUser, uid;
    private ArrayList<Message> messages;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ImageButton spendMessage;
    private EditText editMessage;
    DatabaseReference chats;
    Map<String, User> usersDictionary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.this.finish();// возврат на предыдущий activity
            }
        });

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        Intent i = getIntent();
        if(i != null) {
            uidUser = i.getStringExtra(Constant.USER_UID);
            DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users").child(uidUser);
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    toolbar.setTitle(user.getName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        chats= FirebaseDatabase.getInstance().getReference("chats");

        getUser();
        spendMessage();
    }

    public void spendMessage(){
        editMessage =findViewById(R.id.editDescription);
        spendMessage=findViewById(R.id.imageButton);

        Message message =new Message();

        spendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long date=new Date().getTime();

                if(TextUtils.isEmpty(editMessage.getText().toString())) {
                    Toast.makeText(ChatActivity.this, "Введите сообщение", Toast.LENGTH_SHORT).show();
                    return;
                }


                message.setUser(FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid());
                message.setTime(date);
                message.setMessage(editMessage.getText().toString());

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("chats")
                        .child(uid)
                        .child(uidUser)
                        .push()
                        .setValue(message);

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("chats")
                        .child(uidUser)
                        .child(uid)
                        .push()
                        .setValue(message);

                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(uidUser)
                        .addData("Новое сообщение", message.getMessage()).build());

                editMessage.setText(null);
            }
        });

    }

    public void loadingChat(){
        messages =new ArrayList<>();
        chatAdapter= new ChatAdapter(messages, uid);

        recyclerView=findViewById(R.id.chat);

        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);

        ValueEventListener chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(messages.size() > 0) messages.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message =dataSnapshot.getValue(Message.class);
                    User user=usersDictionary.get(message.getUser());
                    if (user!=null) {
                        message.setUserName(user.getName());
                        message.setUserImage(user.getAvatarUri());
                    }
                    messages.add(message);
                }
                chatAdapter.notifyDataSetChanged();
                if (messages.size()>3) {
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        chats.child(uid).child(uidUser).addValueEventListener(chatListener);
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
                loadingChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        users.addValueEventListener(userListener);
    }
}