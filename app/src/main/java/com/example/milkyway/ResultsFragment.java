package com.example.milkyway;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class ResultsFragment extends Fragment implements ResultsItemClickListener{

    private String[] cities, costs;
    private String countryName;
    private boolean noResults = false;

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
            noResults = true;
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
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        String flagUrl = "https://countryflagsapi.com/png/";
        String url = flagUrl + countryName;
        ResultsRecyclerViewAdapter resultsRecyclerViewAdapter = new ResultsRecyclerViewAdapter(getActivity(),
            cities, costs, url);
        resultsRecyclerViewAdapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(resultsRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView resultsDescription = requireView().findViewById(R.id.resultsTitle);
        countryName = countryName.toUpperCase(Locale.ROOT);
        String pageTitle = "Search Results for " + countryName;
        resultsDescription.setText(pageTitle);
    }

    @Override
    public void onClickCitySummary(View view, int position) {
        // Go to city summary page
        if (countryName != null && !noResults) {
            Bundle bundle = new Bundle();
            bundle.putString("countryName", countryName);
            bundle.putString("cityName", cities[position]);

             //use this for CitySummaryFragment.java
            Fragment citySummaryFragment = new CitySummaryFragment();
            citySummaryFragment.setArguments(bundle);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, citySummaryFragment)
                    .addToBackStack("tag")
                    .commit();
        }
    }

    @Override
    public void onClickFavorites(View view, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getUid();
            String userID = user.getUid();
            String city = cities[position];
            String country = countryName;
            String cost = costs[position];

            String key = FirebaseDatabase.getInstance().getReference("Fav")
                    .child(userID).push().getKey();
            FavInfo favInfo = new FavInfo(key, country, city, cost);

            if (key != null) {
                FirebaseDatabase.getInstance().getReference("Fav")
                        .child(userID).child(key)
                        .setValue(favInfo).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireActivity().getApplicationContext(),
                                        "Added to favorites!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireActivity().getApplicationContext(),
                                        "Failed to save to favorites!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(requireActivity().getApplicationContext(),
                        "Failed to retrieve user favorites data!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "Failed to retrieve user data!", Toast.LENGTH_SHORT).show();
        }
    }
}
