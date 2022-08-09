package com.example.dogmeet.adapter;

import static com.example.dogmeet.Constant.URI;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.OnAnswerPass;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.entity.Answer;
import com.example.dogmeet.entity.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> implements RecyclerViewInterface {
    private final RecyclerViewInterface recyclerViewInterface;

    public ArrayList<Message> messageList;
    public AnswerAdapter answerAdapter;
    public ArrayList<Answer> answersList;
    private Context context;
    //public String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
    private OnAnswerPass onAnswerPass;

    @Override
    public void OnItemClick(int position) {

    }

    @Override
    public void OnButtonClick(int position) {
        Answer answer=answersList.get(position);
        String userName=answer.getUserName()+",";
        String uidComment=answer.getMainUid();
        Boolean isAnswer=true;
        onAnswerPass.onAnswerPass(userName, uidComment, isAnswer);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, messageTextView, dateTextView, answer;
        public ImageView avatar;
        public RecyclerView answerView;

        public MessageViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.chatUser);
            messageTextView = itemView.findViewById(R.id.lastMessage);
            dateTextView = itemView.findViewById(R.id.chatDate);
            avatar = itemView.findViewById(R.id.chatAvatar);
            answer=itemView.findViewById(R.id.answer);
            answerView=itemView.findViewById(R.id.answersView);

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

            View.OnClickListener buttonClickListener=new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface!=null){
                        int pos=getAdapterPosition();

                        if (pos!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.OnButtonClick(pos);
                        }
                    }
                }
            };

            answer.setClickable(true);
            answer.setOnClickListener(buttonClickListener);
        }
    }

    public MessageAdapter(ArrayList<Message> messageArrayList, RecyclerViewInterface recyclerViewInterface) {
        messageList = messageArrayList;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context= viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
        return new MessageViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        Message message=messageList.get(i);

        messageViewHolder.nameTextView.setText(message.getUserName());
        messageViewHolder.messageTextView.setText(message.getMessage());
        messageViewHolder.dateTextView.setText(DateFormat.format("dd.MM в HH:mm", message.getTime()));

        if (message.getUserImage()!=null){
            String url=message.getUserImage();
            Glide.with(messageViewHolder.avatar.getContext()).load(url).into(messageViewHolder.avatar);
        }
        else {
            Glide.with(messageViewHolder.avatar.getContext()).load(URI).into(messageViewHolder.avatar);
        }

        if (message.getAnswerArrayList()!=null){
            messageViewHolder.answerView.setVisibility(View.VISIBLE);
            answersList=message.getAnswerArrayList();
            answerAdapter= new AnswerAdapter(answersList, this);

            messageViewHolder.answerView.setLayoutManager(new LinearLayoutManager(context));
            messageViewHolder.answerView.setHasFixedSize(true);
            messageViewHolder.answerView.setItemAnimator(new DefaultItemAnimator());
            messageViewHolder.answerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
            messageViewHolder.answerView.setAdapter(answerAdapter);
        }
        else {
            messageViewHolder.answerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override    public int getItemCount() {
        return messageList.size();
    }


}
