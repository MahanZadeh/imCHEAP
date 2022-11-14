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


public class FavoritesFragment extends Fragment {

    RecyclerView recyclerView;
    String[] cities, costs;
    String countryName;
    int[] images = {R.drawable.usa_flag};
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Fav");
        userID = user.getUid();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = view.findViewById(R.id.recyclerView2);

        FirebaseDatabase.getInstance().getReference("Fav")
                .child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    Toast.makeText(getActivity().getApplicationContext(), "Filed to retrieve data from db!", Toast.LENGTH_SHORT).show();

                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    //toast text: task.getResult().getValue().toString()
                    Toast.makeText(getActivity().getApplicationContext(), "Success getting data from db", Toast.LENGTH_SHORT).show();




//                    MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(),
//                            cities, costs, images);
////                    myRecyclerViewAdapter.setClickListener(this);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//                    recyclerView.setAdapter(myRecyclerViewAdapter);
                }
            }
        });


//        cities = new String[2];
//        cities[0] = "berlin";
//        cities[1] = "test";
//        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(),
//                cities, costs, images); //the info to be displayed comes here
//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        recyclerView.setAdapter(myRecyclerViewAdapter);

        return view;
    }
}