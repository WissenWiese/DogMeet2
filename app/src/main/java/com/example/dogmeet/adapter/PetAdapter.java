package com.example.dogmeet.adapter;

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
import com.example.dogmeet.entity.Pet;
import com.example.dogmeet.entity.User;

import java.util.ArrayList;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    private ArrayList<Pet> mPets;
    private Context context;


    public static class PetViewHolder extends RecyclerView.ViewHolder {
        public TextView name, age, breed, gender;
        public ImageView imageView;

        public PetViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            name =itemView.findViewById(R.id.name);
            age =itemView.findViewById(R.id.age);
            breed =itemView.findViewById(R.id.breed);
            gender =itemView.findViewById(R.id.gender);
            imageView=itemView.findViewById(R.id.avatar);

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

    public PetAdapter(ArrayList<Pet> pets, RecyclerViewInterface recyclerViewInterface) {
        mPets =pets;
        this.recyclerViewInterface=recyclerViewInterface;
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
        holder.age.setText(pet.getAge());
        holder.breed.setText(pet.getBreed());
        holder.gender.setText(pet.getGender());

        if (pet.getAvatar_pet()!=null){
            String url=pet.getAvatar_pet();
            Glide.with(context).load(url).into(holder.imageView);
        }
        else {
            Glide.with(context).load(URI).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mPets.size();
    }
}
