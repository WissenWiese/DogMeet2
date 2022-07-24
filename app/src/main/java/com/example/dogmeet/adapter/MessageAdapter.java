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
import com.example.dogmeet.entity.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    public ArrayList<Message> messageList;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, messageTextView, dateTextView;
        public ImageView avatar;

        public MessageViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.chatUser);
            messageTextView = itemView.findViewById(R.id.lastMessage);
            dateTextView = itemView.findViewById(R.id.chatDate);
            avatar = itemView.findViewById(R.id.chatAvatar);

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

    public MessageAdapter(ArrayList<Message> messageArrayList, RecyclerViewInterface recyclerViewInterface) {
        messageList = messageArrayList;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
        return new MessageViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        Message message=messageList.get(i);

        messageViewHolder.nameTextView.setText(message.getUserName());
        messageViewHolder.messageTextView.setText(message.getMessage());
        messageViewHolder.dateTextView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTime()));

        if (message.getUserImage()!=null){
            String url=message.getUserImage();
            Glide.with(messageViewHolder.avatar.getContext()).load(url).into(messageViewHolder.avatar);
        }
        else {
            Glide.with(messageViewHolder.avatar.getContext()).load(URI).into(messageViewHolder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


}
