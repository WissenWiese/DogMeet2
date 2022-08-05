package com.example.dogmeet.adapter;

import static com.example.dogmeet.Constant.URI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;
import com.example.dogmeet.entity.Pet;
import com.example.dogmeet.entity.User;

import java.util.ArrayList;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    private ArrayList<Pet> mPets;
    private Context context;
    private Boolean editPet;


    public static class PetViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;
        public ImageButton deleteBtn;

        public PetViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            name =itemView.findViewById(R.id.name);
            imageView=itemView.findViewById(R.id.avatar);
            deleteBtn=itemView.findViewById(R.id.delete_pet);

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

            deleteBtn.setOnClickListener(new View.OnClickListener() {
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

    public PetAdapter(ArrayList<Pet> pets, RecyclerViewInterface recyclerViewInterface, Boolean editPet) {
        mPets =pets;
        this.recyclerViewInterface=recyclerViewInterface;
        this.editPet=editPet;
    }

    @Override
    public PetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context= parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        PetViewHolder evh = new PetViewHolder(v, recyclerViewInterface);
        return evh;
    }

    @Override
    public void onBindViewHolder(PetViewHolder holder, int position) {
        Pet pet = mPets.get(position);

        holder.name.setText(pet.getName());

        if (pet.getAvatar_pet()!=null){
            String url=pet.getAvatar_pet();
            Glide.with(context).load(url).into(holder.imageView);
        }
        else {
            Glide.with(context).load(URI).into(holder.imageView);
        }

        if (editPet){
            holder.deleteBtn.setVisibility(View.VISIBLE);
        }
        else{
            holder.deleteBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mPets.size();
    }
}
