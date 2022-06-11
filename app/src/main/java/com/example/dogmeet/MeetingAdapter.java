package com.example.dogmeet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;


public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>{
    private List<Meeting> meetings;

    public MeetingAdapter(){
        meetings=new ArrayList<>();
    }

    public void setMeetings(List<Meeting> meetings){
        if(!this.meetings.isEmpty()) this.meetings.clear();

        this.meetings.addAll(meetings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.meeting_item_view, parent, false);
        return new MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int i) {
        Meeting meeting=meetings.get(i);
        holder.bind(meeting);

    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }

    class MeetingViewHolder extends RecyclerView.ViewHolder {
        private TextView nameMeet;
        private TextView addressMeet;
        private TextView dateMeet;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);

            nameMeet=itemView.findViewById(R.id.NameTextView);
            addressMeet=itemView.findViewById(R.id.AddressTextView);
            dateMeet=itemView.findViewById(R.id.DateTextView);
        }

        public void bind(Meeting meeting) {
            nameMeet.setText(meeting.getTitle());
            addressMeet.setText(meeting.getAddress());
            dateMeet.setText(meeting.getDate());
        }
    }



}
