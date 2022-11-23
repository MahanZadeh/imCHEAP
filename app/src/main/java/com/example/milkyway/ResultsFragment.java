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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class ResultsFragment extends Fragment implements ResultsItemClickListener{

    RecyclerView recyclerView;
    View view;
    String[] cities, costs;
    String countryName;
    boolean noResults = false;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get search results
        Log.e("COMING FROM SEARCH FRAGMENT TO RESULTS FRAGMENT NOTICE", "NOTICE DEBUG MESSAGE");
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.getSerializable("City Data") != null) {
            @SuppressWarnings("unchecked") LinkedHashMap<List<String>, Double> sortedData =
                    (LinkedHashMap<List<String>, Double>) bundle.getSerializable("City Data");
            Set<List<String>> keys = sortedData.keySet();
            cities = new String[sortedData.size()];
            costs = new String[sortedData.size()];
            int count = 0;
            for (List<String> key : keys) {
                String cityName = key.get(0);
                cities[count] = (cityName);
                String itemName = key.get(1);
                String cCode = key.get(2);
                String fullDescription = itemName + ", Price: " + sortedData.get(key) + " " + cCode;
                costs[count] = (fullDescription);
                count++;
            }
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView resultsDescription = requireView().findViewById(R.id.resultsTitle);
        countryName = countryName.toUpperCase(Locale.ROOT);
        String pageTitle = "Search Results for " + countryName;
        resultsDescription.setText(pageTitle);
    }

//    @Override
//    public void onClickCitySummary(View view, int position) {
//        // Go to city summary page
//
//        if (countryName != null && !noResults) {
//            Bundle bundle = new Bundle();
//            bundle.putString("countryName", countryName);
//            bundle.putString("cityName", cities[position]);
//
//            Fragment citySummaryFragment = new CitySummaryFragment();
//            citySummaryFragment.setArguments(bundle);
//            getParentFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, citySummaryFragment)
//                    .commit();
//
////            Intent intent = new Intent(getActivity(), CitySummary.class);
////            intent.putExtra("bundle", bundle);
////            startActivity(intent);
//        }
//    }

    @Override
    public void onClickCitySummary(View view, int position) {
        // Go to city summary page
        if (countryName != null && !noResults) {
            Bundle bundle = new Bundle();
            bundle.putString("countryName", countryName);
            bundle.putString("cityName", cities[position]);
            Intent intent = new Intent(getActivity(), CitySummary.class);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
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
