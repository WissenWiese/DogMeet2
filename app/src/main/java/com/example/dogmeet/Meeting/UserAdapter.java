package com.example.dogmeet.Meeting;

import static com.example.dogmeet.Constant.URI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.model.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    private ArrayList<User> mUsers;
    private Context context;


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;

        public UserViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            name =itemView.findViewById(R.id.chatUser);
            imageView=itemView.findViewById(R.id.chatAvatar);

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

    public UserAdapter(ArrayList<User> users, RecyclerViewInterface recyclerViewInterface) {
        mUsers =users;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context= parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        UserViewHolder evh = new UserViewHolder(v, recyclerViewInterface);
        return evh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = mUsers.get(position);

        holder.name.setText(user.getName());

        if (user.getAvatarUri()!=null){
            String url=user.getAvatarUri();
            Glide.with(context).load(url).into(holder.imageView);
        }
        else {
            Glide.with(context).load(URI).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}

