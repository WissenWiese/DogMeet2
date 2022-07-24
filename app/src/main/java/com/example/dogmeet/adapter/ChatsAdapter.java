package com.example.dogmeet.adapter;

import static com.example.dogmeet.Constant.URI;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.entity.Chat;
import com.example.dogmeet.entity.Message;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    public ArrayList<Chat> chats;
    public String uid;

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        public TextView userChat, lastMessage, date;
        public ImageView avatarChat;

        public ChatsViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            userChat = itemView.findViewById(R.id.chatUser);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            date = itemView.findViewById(R.id.chatDate);
            avatarChat = itemView.findViewById(R.id.chatAvatar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface!=null){
                        int pos=getAdapterPosition();

                        if (pos!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.OnItemClick(pos);
                        }
                    }
                }
            });
        }
    }

    public ChatsAdapter(ArrayList<Chat> chatsArrayList, RecyclerViewInterface recyclerViewInterface, String userUid) {
        chats = chatsArrayList;
        this.recyclerViewInterface=recyclerViewInterface;
        uid=userUid;
    }

    @NonNull
    @Override
    public ChatsAdapter.ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chats, viewGroup, false);
        return new ChatsAdapter.ChatsViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.ChatsViewHolder chatsViewHolder, int i) {
        Chat chat= chats.get(i);

        chatsViewHolder.userChat.setText(chat.getName());
        Message message=chat.getLastMessage();
        if (uid.equals(message.getUser())) {
            chatsViewHolder.lastMessage.setText("Вы:"+message.getMessage());
        }
        else{
            chatsViewHolder.lastMessage.setText(chat.getName()+":"+message.getMessage());
        }
        chatsViewHolder.date.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTime()));

        if (chat.getUrl()!=null){
            String url=chat.getUrl();
            Glide.with(chatsViewHolder.avatarChat.getContext()).load(url).into(chatsViewHolder.avatarChat);
        }
        else {
            Glide.with(chatsViewHolder.avatarChat.getContext()).load(URI).into(chatsViewHolder.avatarChat);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}