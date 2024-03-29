package com.example.dogmeet.Fragment.ListMeet;

import static com.example.dogmeet.Constant.URI;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.entity.Meeting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    private ArrayList<Meeting> mMeetings;
    private Context context;


    public static class MeetingViewHolder extends RecyclerView.ViewHolder {
        public TextView title, address, date, member, comments;
        public ImageView imageView;
        public ImageButton imageButton;

        public MeetingViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            title =itemView.findViewById(R.id.TitleTextView);
            address =itemView.findViewById(R.id.AddressTextView);
            date =itemView.findViewById(R.id.DateTextView);
            member =itemView.findViewById(R.id.MemberTextView);
            comments=itemView.findViewById(R.id.nubComments);
            imageView=itemView.findViewById(R.id.imageView2);
            imageButton=itemView.findViewById(R.id.imageButtonMessage);

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

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface!=null){
                        int pos=getAdapterPosition();

                        if (pos!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.OnButtonClick(pos);
                        }
                    }
                }
            });

        }
    }

    public MeetingAdapter(RecyclerViewInterface recyclerViewInterface, ArrayList<Meeting> meetings) {
        this.recyclerViewInterface=recyclerViewInterface;
        mMeetings=meetings;
    }

    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context= parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meeting, parent, false);
        MeetingViewHolder evh = new MeetingViewHolder(v, recyclerViewInterface);
        return evh;
    }

    @Override
    public void onBindViewHolder(MeetingViewHolder holder, int position) {
        Meeting meeting = mMeetings.get(position);

        holder.title.setText(meeting.getTitle());
        holder.address.setText(meeting.getAddress());
        holder.date.setText(DateFormat.format("dd.MM, HH:mm", meeting.getDate()));
        holder.member.setText(Integer.toString(meeting.getNumberMember()));
        holder.comments.setText(Integer.toString(meeting.getNumberComments()));

        if (meeting.urlImage!=null){
            String url=meeting.urlImage;
            Glide.with(context).load(url).into(holder.imageView);
        }
        else {
            Glide.with(context).load(URI).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mMeetings.size();
    }

    public void filterList(ArrayList<Meeting> filterlist) {
        mMeetings = filterlist;
        notifyDataSetChanged();
    }

    public void setList(ArrayList<Meeting> meetingslist) {
        mMeetings = meetingslist;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public  void sortByPopular(){
        Collections.sort(mMeetings, Comparator.comparing(Meeting::getNumberMember)
                .thenComparing(Meeting::getNumberComments));
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByDate(){
        Collections.sort(mMeetings, Comparator.comparing(Meeting::getDate));
        Collections.reverse(mMeetings);
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sort(){
        Collections.sort(mMeetings, Comparator.comparing(Meeting::getUid));
        notifyDataSetChanged();
    }
}
