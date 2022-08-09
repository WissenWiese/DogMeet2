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
import com.example.dogmeet.entity.Answer;
import com.example.dogmeet.entity.Message;

import java.util.ArrayList;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    public ArrayList<Answer> answersList;

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, messageTextView, dateTextView, answer;
        public ImageView avatar;
        public RecyclerView answerView;

        public AnswerViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.chatUser);
            messageTextView = itemView.findViewById(R.id.lastMessage);
            dateTextView = itemView.findViewById(R.id.chatDate);
            avatar = itemView.findViewById(R.id.chatAvatar);
            answer=itemView.findViewById(R.id.answer);
            answerView=itemView.findViewById(R.id.answersView);
            answerView.setVisibility(View.GONE);

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

    public AnswerAdapter(ArrayList<Answer> answersList, RecyclerViewInterface recyclerViewInterface) {
        this.answersList = answersList;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
        return new AnswerViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder answerViewHolder, int i) {
        Answer answer= answersList.get(i);

        answerViewHolder.nameTextView.setText(answer.getUserName());
        answerViewHolder.messageTextView.setText(answer.getMessage());
        answerViewHolder.dateTextView.setText(DateFormat.format("dd.MM в HH:mm", answer.getTime()));

        if (answer.getUserImage()!=null){
            String url=answer.getUserImage();
            Glide.with(answerViewHolder.avatar.getContext()).load(url).into(answerViewHolder.avatar);
        }
        else {
            Glide.with(answerViewHolder.avatar.getContext()).load(URI).into(answerViewHolder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }


}
