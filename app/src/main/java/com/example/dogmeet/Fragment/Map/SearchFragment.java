package com.example.dogmeet.Fragment.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.dogmeet.Fragment.ListMeet.MeetingData;
import com.example.dogmeet.R;
import com.example.dogmeet.RecyclerViewInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchFragment extends Fragment implements View.OnClickListener, RecyclerViewInterface{

    private View view;
    private Button btnDog, btnBig, btnMiddle, btnSmall, btnBoy, btnGirl,
            btnPlace, btnPark, btnBar, btnRestaurant, btnCafe;
    private ImageButton addBtn;
    private ArrayList<String> sizeList, placeList, addsBreedsList;
    private List<String> breedsList;
    private String gender, type;
    private ConstraintLayout typeDog, typePlace;
    private AutoCompleteTextView editBreed;
    private RecyclerView recyclerView;
    private BreedAdapter breedAdapter;
    private WalkerData walkerData;
    private PlaceData placeData;



    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_search, container, false);
        init();
        return view;
    }

    public void init(){
        btnDog=view.findViewById(R.id.usersFilter);
        btnBig=view.findViewById(R.id.bigBtn2);
        btnMiddle=view.findViewById(R.id.middleBtn2);
        btnSmall=view.findViewById(R.id.smallBtn2);
        btnBoy=view.findViewById(R.id.buttonBoy);
        btnGirl=view.findViewById(R.id.buttonGirl);
        btnPlace=view.findViewById(R.id.placeFilter);
        btnPark=view.findViewById(R.id.buttonPark);
        btnBar=view.findViewById(R.id.buttonBar);
        btnRestaurant=view.findViewById(R.id.buttonRestaurant);
        btnCafe=view.findViewById(R.id.buttonCafe);

        btnDog.setOnClickListener(this);
        btnBig.setOnClickListener(this);
        btnMiddle.setOnClickListener(this);
        btnSmall.setOnClickListener(this);
        btnBoy.setOnClickListener(this);
        btnGirl.setOnClickListener(this);
        btnPlace.setOnClickListener(this);
        btnPark.setOnClickListener(this);
        btnBar.setOnClickListener(this);
        btnRestaurant.setOnClickListener(this);
        btnCafe.setOnClickListener(this);

        editBreed=view.findViewById(R.id.editTextBreed);
        addBtn=view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(this);

        String[] breeds = getResources().getStringArray(R.array.breeds);
        breedsList = Arrays.asList(breeds);
        ArrayAdapter<String> adapterBreed = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, breedsList);
        editBreed.setAdapter(adapterBreed);

        sizeList=new ArrayList<>();
        placeList =new ArrayList<>();
        addsBreedsList=new ArrayList<>();
        gender=null;

        typeDog=view.findViewById(R.id.typeDog);
        typePlace=view.findViewById(R.id.typePlace);

        type=btnDog.getText().toString();

        recyclerView=view.findViewById(R.id.breed_rv);
        recyclerView.setHasFixedSize(true);

        breedAdapter= new BreedAdapter(addsBreedsList, (RecyclerViewInterface) this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(breedAdapter);

        editBreed.setOnKeyListener(new View.OnKeyListener()
                                  {
                                      public boolean onKey(View v, int keyCode, KeyEvent event)
                                      {
                                          if(event.getAction() == KeyEvent.ACTION_DOWN )
                                          {
                                              for (String breed : breedsList){
                                                  if (editBreed.getText().toString().equals(breed)){
                                                      addsBreedsList.add(editBreed.getText().toString());
                                                      breedAdapter.notifyDataSetChanged();
                                                      editBreed.setText(null);
                                                  }
                                              }
                                              return true;
                                          }
                                          return false;
                                      }
                                  }
        );
    }

    @Override
    public void onClick(View view) {
        if (sizeList.size()==3) sizeList.clear();
        if (placeList.size()==4) placeList.clear();
        switch (view.getId()){
            case R.id.usersFilter:
                setTypeFilter(btnDog);
                break;
            case R.id.placeFilter:
                setTypeFilter(btnPlace);
                break;
            case R.id.bigBtn2:
                setSizeDog(btnBig);
                break;
            case R.id.middleBtn2:
                setSizeDog(btnMiddle);
                break;
            case R.id.smallBtn2:
                setSizeDog(btnSmall);
                break;
            case R.id.buttonBoy:
                setGender(btnBoy);
                break;
            case R.id.buttonGirl:
                setGender(btnGirl);
                break;
            case R.id.buttonPark:
                setPlace(btnPark);
                break;
            case R.id.buttonBar:
                setPlace(btnBar);
                break;
            case R.id.buttonRestaurant:
                setPlace(btnRestaurant);
                break;
            case R.id.buttonCafe:
                setPlace(btnCafe);
                break;
            case R.id.addBtn:
                addBreed();
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    public void setTypeFilter(Button button){
        if (button.getTag().equals("false")){
            setTrue(button);
            type=button.getText().toString();
            if (button.getText().equals("Компаньоны")) {
                setFalse(btnPlace);
                sizeList.clear();
                gender=null;
                breedsList.clear();
                typeDog.setVisibility(View.VISIBLE);
                typePlace.setVisibility(View.GONE);
            }
            else {
                setFalse(btnDog);
                placeList.clear();
                typeDog.setVisibility(View.GONE);
                typePlace.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    public void setSizeDog(Button button){
        if (button.getTag().equals("false")){
            setTrue(button);
            if (sizeList.size()<2){
                sizeList.add(button.getText().toString());
            }
            else {
                sizeList.clear();
                setFalse(btnBig);
                setFalse(btnSmall);
                setFalse(btnMiddle);
            }
        }
        else {
            setFalse(button);
            if (sizeList.size()>0){
                sizeList.remove(button.getText().toString());
            }
        }
        walkerData.filterPet(gender, sizeList, addsBreedsList);
    }

    public void setGender(Button button){
        if (button.getTag().equals("false")){
            setTrue(button);
            if (gender==null) {
                gender = button.getText().toString();
            }
            else {
                gender=null;
                setFalse(btnBoy);
                setFalse(btnGirl);
            }
        }
        else {
            setFalse(button);
            gender=null;
        }
        walkerData.filterPet(gender, sizeList, addsBreedsList);
    }

    public void setPlace(Button button){
        if (button.getTag().equals("false")){
            setTrue(button);
            if (placeList.size()<3){
                placeList.add(button.getText().toString());
            }
            else {
                placeList.clear();
                setFalse(btnPark);
                setFalse(btnCafe);
                setFalse(btnBar);
                setFalse(btnRestaurant);
            }
        }
        else {
            setFalse(button);
            if (placeList.size()>0){
                placeList.remove(button.getText().toString());
            }
        }
        placeData.filterPlace(placeList);
    }

    public void addBreed(){
        for (String breed : breedsList){
            if (editBreed.getText().toString().equals(breed)){
                addsBreedsList.add(editBreed.getText().toString());
                breedAdapter.notifyDataSetChanged();
                editBreed.setText(null);
                walkerData.filterPet(gender, sizeList, addsBreedsList);
            }
            else return;
        }
    }

    @SuppressLint("ResourceAsColor")
    public void setTrue(Button button){
        button.setBackground(getResources().getDrawable(R.drawable.incoming));
        button.setTextColor(R.color.text);
        button.setTag("true");
    }

    @SuppressLint("ResourceAsColor")
    public void setFalse(Button button){
        button.setBackground(getResources().getDrawable(R.drawable.btn3));
        button.setTag("false");
        button.setTextColor(R.color.primary_dark);
    }

    @Override
    public void OnItemClick(int position) {

    }

    @Override
    public void OnButtonClick(int position) {
        addsBreedsList.remove(position);
        breedAdapter.notifyDataSetChanged();
        walkerData.filterPet(gender, sizeList, addsBreedsList);
    }

    public void setModel(WalkerData walkerData, PlaceData placeData){
        this.walkerData=walkerData;
        this.placeData=placeData;
    }
}