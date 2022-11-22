package com.example.dogmeet.Fragment.Map;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dogmeet.R;
import com.example.dogmeet.entity.Doghanting;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoghantingMarker extends Fragment {
    private String uidDoghanting;
    private TextView message, date;
    private ImageView image;
    private DatabaseReference doghanting;
    private View view;

    public DoghantingMarker() {
    }

    public static DoghantingMarker newInstance(String uidDoghanting) {
        DoghantingMarker fragment = new DoghantingMarker();
        Bundle args = new Bundle();
        args.putString("UID", uidDoghanting);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uidDoghanting = getArguments().getString("UID", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_doghanting_marker, container, false);

        doghanting= FirebaseDatabase.getInstance().getReference("doghanter").child(uidDoghanting);
        image=view.findViewById(R.id.photoWalker);
        message=view.findViewById(R.id.nameWalker);
        date=view.findViewById(R.id.dateCreate);

        ValueEventListener dListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                {
                    Doghanting doghanting =snapshot.getValue(Doghanting.class);
                    if (doghanting !=null){
                        Glide.with(image.getContext()).load(doghanting.getImage()).into(image);
                        message.setText(doghanting.getMessage());
                        date.setText(DateFormat.format("dd-MM (HH:mm:ss)", doghanting.getTime()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        doghanting.addValueEventListener(dListener);

        return view;
    }
}