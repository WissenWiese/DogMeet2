package com.example.dogmeet.Fragment.Map;

import static com.example.dogmeet.Constant.URI;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.model.Pet;
import com.example.dogmeet.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.PetsViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    private ArrayList<Pet> pets;
    private ArrayList<Integer> selected = new ArrayList<>();
    private Context context;

    public static class PetsViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;


        public PetsViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
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

    public PetsAdapter(ArrayList<Pet> pets, RecyclerViewInterface recyclerViewInterface, ArrayList<Integer> selected) {
        this.pets =pets;
        this.recyclerViewInterface=recyclerViewInterface;
        this.selected=selected;
    }

    @Override
    public PetsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context= parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pets, parent, false);
        PetsViewHolder evh = new PetsViewHolder(v, recyclerViewInterface);
        return evh;
    }

    @Override
    public void onBindViewHolder(PetsViewHolder holder, int position) {
        Pet pet = pets.get(position);

        holder.name.setText(pet.getName());

        if (pet.getAvatar_pet()!=null){
            String url=pet.getAvatar_pet();
            Glide.with(context).load(url).into(holder.imageView);
        }
        else {
            Glide.with(context).load(URI).into(holder.imageView);
        }

        if (selected.contains(position)){
            holder.itemView.setBackgroundColor(Color.GRAY);
        }
        else holder.itemView.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }
}

