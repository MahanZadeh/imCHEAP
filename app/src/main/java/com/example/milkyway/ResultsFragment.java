package com.example.milkyway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class ResultsFragment extends Fragment implements ResultsItemClickListener{

    RecyclerView recyclerView;
    View view;
    ResultsFragment resultsFragment;
    String[] cities, costs;
    String countryName;
    Bitmap flag;

    private String flagUrl = "https://countryflagsapi.com/png/";

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get search results
        Log.e("COMING FROM SEARCH FRAGMENT TO RESULTS FRAGMENT NOTICE", "NOTICE DEBUG MESSAGE");
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.getStringArrayList("Sorted Cities") != null) {
            cities = bundle.getStringArrayList("Sorted Cities").toArray(new String[0]);
            costs = bundle.getStringArrayList("Sorted Price Descriptions")
                    .toArray(new String[0]);
            countryName = bundle.getString("Country Name");
            resultsFragment = this;
            AsyncTaskRunnerFlag runnerFlag = new AsyncTaskRunnerFlag();
            String completeUrl = flagUrl + countryName;
            runnerFlag.execute(completeUrl);
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

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunnerFlag extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                InputStream in = (InputStream) new URL(strings[0]).getContent(); //Reads whatever content found with the given URL Asynchronously And returns.
                flag = BitmapFactory.decodeStream(in); //Decodes the stream returned from getContent and converts It into a Bitmap Format
                System.out.println(flag.getRowBytes());
                System.out.println(flag.getHeight());
                in.close(); //Closes the InputStream
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(),
                cities, costs, flag);
            myRecyclerViewAdapter.setClickListener(resultsFragment);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(myRecyclerViewAdapter);
        }
    }
}
