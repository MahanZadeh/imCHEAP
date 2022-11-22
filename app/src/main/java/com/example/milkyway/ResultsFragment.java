package com.example.milkyway;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultsFragment extends Fragment implements ResultsItemClickListener{

    RecyclerView recyclerView;
    View view;
    String[] cities, costs;
    String countryName;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get search results
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.getStringArrayList("Sorted Cities") != null) {
            cities = bundle.getStringArrayList("Sorted Cities").toArray(new String[0]);
            costs = bundle.getStringArrayList("Sorted Price Descriptions")
                    .toArray(new String[0]);
            countryName = bundle.getString("Country Name");
        } else if (bundle != null && bundle.getString("Country Name") != null) {
            String[] noResultsTitle = new String[1];
            noResultsTitle[0] = "No results were found.";
            String[] noResultsDescription = new String[1];
            noResultsDescription[0] = "Please try again or try searching another country.";
            cities = noResultsTitle;
            costs = noResultsDescription;
            countryName = bundle.getString("Country Name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_results, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        String flagUrl = "https://countryflagsapi.com/png/";
        String url = flagUrl + countryName;
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(),
            cities, costs, url);
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

        String key = FirebaseDatabase.getInstance().getReference("Fav").child(userID).push().getKey();
        FavInfo favInfo = new FavInfo(key, country, city, cost);

        assert key != null;
        FirebaseDatabase.getInstance().getReference("Fav")
                .child(userID).child(key)
                .setValue(favInfo).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(requireActivity().getApplicationContext(),
                                "Added to favorites!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(requireActivity().getApplicationContext(),
                                "Failed to save to favorites!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
