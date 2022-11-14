package com.example.milkyway;

import android.content.Intent;
import android.os.Bundle;
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


public class ResultsFragment extends Fragment implements ResultsItemClickListener{

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Get search results
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cities = bundle.getStringArrayList("Sorted Cities").toArray(new String[0]);
            costs = bundle.getStringArrayList("Sorted Price Descriptions")
                    .toArray(new String[0]);
            countryName = bundle.getString("Country Name");
        }

        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(),
                cities, costs, images);
        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(myRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onClickCitySummary(View view, int position) {
        // Go to city summary page
        Intent intent = new Intent(getActivity(), dynamic_test.class);
        startActivity(intent);
    }

    @Override
    public void onClickFavorites(View view, int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Fav");
        userID = user.getUid();
        String city = cities[position];
        String country = countryName;
        String cost = costs[position];
        FavInfo favInfo = new FavInfo(country, city, cost);

        String key = FirebaseDatabase.getInstance().getReference("Fav").child(userID).push().getKey();

        FirebaseDatabase.getInstance().getReference("Fav")
                .child(userID).child(key)
                .setValue(favInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity().getApplicationContext(), "Added to favorites!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity().getApplicationContext(),"Failed to save to favorites!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
