package com.example.dogmeet.Fragment.Map;

import static com.example.dogmeet.Constant.URI;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.model.Meeting;

import java.util.ArrayList;


public class ListMeetingAdapter extends RecyclerView.Adapter<ListMeetingAdapter.ListMeetingViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    private ArrayList<Meeting> mMeetings;
    private Context context;


    public static class ListMeetingViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date;
        public ImageView imageView;

        public ListMeetingViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            title =itemView.findViewById(R.id.titel_meet_marker);
            date =itemView.findViewById(R.id.date_meet_marker);
            imageView=itemView.findViewById(R.id.image_meet_marker);

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

    public ListMeetingAdapter(ArrayList<Meeting> meetings, RecyclerViewInterface recyclerViewInterface) {
        mMeetings=meetings;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @Override
    public ListMeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context= parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meeting_mini, parent, false);
        ListMeetingViewHolder evh = new ListMeetingViewHolder(v, recyclerViewInterface);
        return evh;
    }

    @Override
    public void onBindViewHolder(ListMeetingViewHolder holder, int position) {
        Meeting meeting = mMeetings.get(position);

        holder.title.setText(meeting.getTitle());
        holder.date.setText(DateFormat.format("dd.MM, HH:mm", meeting.getDate()));

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

    public void setList(ArrayList<Meeting> meetings) {
        mMeetings=meetings;
        notifyDataSetChanged();
    }
}
