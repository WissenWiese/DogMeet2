package com.example.dogmeet.adapter;

import static com.example.dogmeet.Constant.URI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.dogmeet.Activity.MeetingActivity;
import com.example.dogmeet.Constant;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.Pet;

import java.util.ArrayList;
import java.util.List;


public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    private ArrayList<Meeting> mMeetings;


    public static class MeetingViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView address;
        public TextView date;
        public TextView member;
        public ImageView imageView;

        public MeetingViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            title =itemView.findViewById(R.id.TitleTextView);
            address =itemView.findViewById(R.id.AddressTextView);
            date =itemView.findViewById(R.id.DateTextView);
            member =itemView.findViewById(R.id.MemberTextView);
            imageView=itemView.findViewById(R.id.imageView2);

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

    public MeetingAdapter(ArrayList<Meeting> meetings, RecyclerViewInterface recyclerViewInterface) {
        mMeetings=meetings;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_item_view, parent, false);
        MeetingViewHolder evh = new MeetingViewHolder(v, recyclerViewInterface);
        return evh;
    }

    @Override
    public void onBindViewHolder(MeetingViewHolder holder, int position) {
        Meeting meeting = mMeetings.get(position);

        holder.title.setText(meeting.getTitle());
        holder.address.setText(meeting.getAddress());
        holder.date.setText(meeting.getDate());
        holder.member.setText(Integer.toString(meeting.getNumberMember()));

        if (meeting.urlImage!=null){
            String url=meeting.urlImage;
            Glide.with(holder.imageView.getContext()).load(url).into(holder.imageView);
        }
        else {
            Glide.with(holder.imageView.getContext()).load(URI).into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return mMeetings.size();
    }
}
