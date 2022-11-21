package com.example.dogmeet.Fragment.Map;

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
import com.example.dogmeet.model.User;

import java.util.ArrayList;

public class BreedAdapter extends RecyclerView.Adapter<BreedAdapter.BreedViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    private ArrayList<String> breeds;
    private Context context;


    public static class BreedViewHolder extends RecyclerView.ViewHolder {
        public TextView breed;
        public ImageButton deleteBreed;

        public BreedViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            breed =itemView.findViewById(R.id.breed);
            deleteBreed=itemView.findViewById(R.id.deleteBreed);

            deleteBreed.setOnClickListener(new View.OnClickListener() {
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

    public BreedAdapter(ArrayList<String> breeds, RecyclerViewInterface recyclerViewInterface) {
        this.breeds =breeds;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @Override
    public BreedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context= parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_breed, parent, false);
        BreedViewHolder evh = new BreedViewHolder(v, recyclerViewInterface);
        return evh;
    }

    @Override
    public void onBindViewHolder(BreedViewHolder holder, int position) {
        String breed = breeds.get(position);

        holder.breed.setText(breed);
    }

    @Override
    public int getItemCount() {
        return breeds.size();
    }
}

