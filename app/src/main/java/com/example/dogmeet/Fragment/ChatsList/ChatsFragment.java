package com.example.dogmeet.Fragment.ChatsList;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dogmeet.Chat.ChatActivity;
import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.entity.Chat;
import com.example.dogmeet.entity.Message;
import com.example.dogmeet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class ChatsFragment extends Fragment implements RecyclerViewInterface {
    String uid;
    private ArrayList<Chat> chats;
    private RecyclerView chatsView;
    private ChatsAdapter chatsAdapter;
    Map<String, User> usersDictionary;
    private View view;
    DatabaseReference users, chatsList;
    boolean isNew;

    public ChatsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messager, container, false);
        chatsView=view.findViewById(R.id.chats_view);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        chats=new ArrayList<>();
        chatsAdapter= new ChatsAdapter(chats, this, uid);

        chatsView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatsView.setHasFixedSize(true);
        chatsView.setItemAnimator(new DefaultItemAnimator());
        chatsView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        chatsView.setAdapter(chatsAdapter);

        users= FirebaseDatabase.getInstance().getReference("Users");
        chatsList = FirebaseDatabase.getInstance().getReference("chats").child(uid);

        getUser();

        return view;
    }

    public void getUser(){
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
                getUidChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        users.addValueEventListener(userListener);
    }

    public void getUidChats(){
        ValueEventListener chatsListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (chats.size()>0) chats.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userUid=dataSnapshot.getKey();
                    assert userUid!=null;
                    Chat chat=new Chat();
                    chat.setRecipient(userUid);
                    User user=usersDictionary.get(userUid);
                    assert user!=null;
                    chat.setName(user.getName());
                    chat.setUrl(user.getAvatarUri());
                    chat.setUid(dataSnapshot.getKey());
                    getLastMessage(chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        chatsList.addValueEventListener(chatsListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getLastMessage(Chat chat){

        Query lastQuery = chatsList.child(chat.getUid()).orderByKey().limitToLast(1);
        ValueEventListener chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message message =dataSnapshot.getValue(Message.class);
                    chat.setLastMessage(message.getMessage());
                    chat.setLastUid(message.getUser());
                    chat.setTime(message.getTime());
                    if (chats.size()>0) {
                        for (Chat chat1 : chats) {
                            if (chat1.getUid().equals(chat.getUid())) {
                                isNew = false;
                                break;
                            }
                            else {
                                isNew=true;
                            }
                        }
                    }
                    else {
                        isNew = true;
                    }

                    if (isNew) {
                        chats.add(chat);
                    Collections.sort(chats, Collections.reverseOrder(Comparator.comparing(Chat::getTime)));
                    chatsAdapter.notifyDataSetChanged();}
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        lastQuery.addValueEventListener(chatListener);
    }

    @Override
    public void OnItemClick(int position) {
        Chat chat=chats.get(position);
        Intent i = new Intent(getContext(), ChatActivity.class);
        i.putExtra(Constant.USER_UID, chat.getRecipient());
        startActivity(i);
    }

    @Override
    public void OnButtonClick(int position) {

    }
}