package com.example.milkyway;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class FavoritesFragment extends Fragment {

    RecyclerView recyclerView;
    private FirebaseUser user;
    private DatabaseReference databaseReference ;
    private String userID;
    private DatabaseReference userFavData;

    ArrayList<String> cityArray = new ArrayList<>();
    ArrayList<String> countryArray = new ArrayList<>();
    ArrayList<String> costArray = new ArrayList<>();
    ArrayList<String> keyArray = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Fav");
        userID = user.getUid();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = view.findViewById(R.id.recyclerView2);


        userFavData = FirebaseDatabase.getInstance().getReference("Fav").child(userID);

        getData(view);


        return view;
    }

    private void getData(View view) {
        userFavData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keyArray.removeAll(keyArray);
                cityArray.removeAll(cityArray);
                countryArray.removeAll(countryArray);
                costArray.removeAll(costArray);
                for (DataSnapshot singleNode: snapshot.getChildren()) {
                    FavInfo favInfo = singleNode.getValue(FavInfo.class);
                    keyArray.add(favInfo.key);
                    cityArray.add(favInfo.city);
                    countryArray.add(favInfo.country);
                    costArray.add(favInfo.cost);

                }

                String flagUrl = "https://countryflagsapi.com/png/";

                FavoritesRecyclerViewAdapter favoritesRecyclerViewAdapter =
                        new FavoritesRecyclerViewAdapter(getActivity(),keyArray, countryArray,
                                cityArray, costArray, flagUrl);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(favoritesRecyclerViewAdapter);

                System.out.println(cityArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(requireActivity().getApplicationContext(),
                        "Failed to get data.", Toast.LENGTH_SHORT).show();
            }


        });
    }
}
