package com.example.dogmeet.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.dogmeet.R;
import com.example.dogmeet.entity.Meeting;
import com.example.dogmeet.entity.Pet;

import java.util.ArrayList;
import java.util.List;


public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder>{
    private List<Pet> pets;

    public PetAdapter(){
        pets=new ArrayList<>();
    }

    public void setMeetings(List<Meeting> meetings){
        if(!this.pets.isEmpty()) this.pets.clear();

        this.pets.addAll(pets);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.meeting_item_view, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int i) {
        Pet pet=pets.get(i);
        holder.bind(pet);

    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    class PetViewHolder extends RecyclerView.ViewHolder {
        private TextView namePet;
        private TextView agePet;
        private TextView breedPet;
        private TextView genderPet;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);

            namePet=itemView.findViewById(R.id.editTextName);
            agePet=itemView.findViewById(R.id.editTextAge);
            breedPet=itemView.findViewById(R.id.editTextBreed);
            genderPet=itemView.findViewById(R.id.editTextGender);
        }

        public void bind(Pet pet) {
            namePet.setText(pet.getName());
            agePet.setText(pet.getAge());
            breedPet.setText(pet.getBreed());
            genderPet.setText(pet.getGender());
        }
    }



}
