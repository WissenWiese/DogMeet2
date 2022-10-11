package com.example.dogmeet.Chat;

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
import com.example.dogmeet.model.Message;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public ArrayList<Message> messageList;
    public String uid;
    public String lastdate;

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView incomingMessage, incomingDate, outgoidDate, outgoidMessage, date;
        public ImageView avatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            incomingMessage=itemView.findViewById(R.id.incoming_message);
            incomingDate=itemView.findViewById(R.id.incoming_date);
            outgoidDate=itemView.findViewById(R.id.outgoid_date);
            outgoidMessage=itemView.findViewById(R.id.outgoid_message);
            avatar = itemView.findViewById(R.id.incoming_avatar);
            date = itemView.findViewById(R.id.date);
        }
    }

    public ChatAdapter(ArrayList<Message> messageArrayList, String userUid) {
        messageList = messageArrayList;
        uid=userUid;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat, viewGroup, false);

        return new ChatAdapter.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder chatViewHolder, int i) {
        Message message = messageList.get(i);
        if (!uid.equals(messageList.get(i).getUser())){
            chatViewHolder.incomingMessage.setVisibility(View.VISIBLE);
            chatViewHolder.incomingDate.setVisibility(View.VISIBLE);
            chatViewHolder.outgoidDate.setVisibility(View.GONE);
            chatViewHolder.outgoidMessage.setVisibility(View.GONE);
            if (message.getUserImage()!=null){
                String url= message.getUserImage();
                Glide.with(chatViewHolder.avatar.getContext()).load(url).into(chatViewHolder.avatar);
            }
            else {
                Glide.with(chatViewHolder.avatar.getContext()).load(URI).into(chatViewHolder.avatar);
            }
            chatViewHolder.incomingMessage.setText(message.getMessage());
            chatViewHolder.incomingDate.setText(DateFormat.format("HH:mm", message.getTime()));
        }
        else{
            chatViewHolder.avatar.setImageResource(0);
            chatViewHolder.incomingMessage.setVisibility(View.GONE);
            chatViewHolder.incomingDate.setVisibility(View.GONE);
            chatViewHolder.outgoidDate.setVisibility(View.VISIBLE);
            chatViewHolder.outgoidMessage.setVisibility(View.VISIBLE);
            chatViewHolder.outgoidDate.setText(DateFormat.format("HH:mm", message.getTime()));
            chatViewHolder.outgoidMessage.setText(message.getMessage());
        }
        String thisdate=DateFormat.format("dd-MM", message.getTime()).toString();

        if (i==0 || !lastdate.equals(thisdate)){
            chatViewHolder.date.setVisibility(View.VISIBLE);
            chatViewHolder.date.setText(thisdate);
            lastdate=thisdate;
        }
        else  {
            chatViewHolder.date.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


}

