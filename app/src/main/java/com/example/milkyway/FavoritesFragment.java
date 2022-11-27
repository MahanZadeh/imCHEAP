package com.example.milkyway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FavoritesFragment extends Fragment implements FavoritesItemClickListener{

    private DatabaseReference databaseReference;
    private String userID;
    private DatabaseReference userFavData;
    private FavoritesRecyclerViewAdapter favoritesRecyclerViewAdapter;

    private final ArrayList<String> cityArray = new ArrayList<>();
    private final ArrayList<String> countryArray = new ArrayList<>();
    private final ArrayList<String> costArray = new ArrayList<>();
    private final ArrayList<String> keyArray = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Fav");
        assert user != null;
        userID = user.getUid();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView2);

        userFavData = FirebaseDatabase.getInstance().getReference("Fav").child(userID);

        String flagUrl = "https://countryflagsapi.com/png/";

        favoritesRecyclerViewAdapter = new FavoritesRecyclerViewAdapter(view.getContext(),
                keyArray, countryArray, cityArray, costArray, flagUrl);
        favoritesRecyclerViewAdapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(favoritesRecyclerViewAdapter);

        getData(view);

        return view;
    }

    private void getData(@SuppressWarnings("UnusedParameters") View view) {
        userFavData.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keyArray.clear();
                cityArray.clear();
                countryArray.clear();
                costArray.clear();
                for (DataSnapshot singleNode: snapshot.getChildren()) {
                    FavInfo favInfo = singleNode.getValue(FavInfo.class);
                    assert favInfo != null;
                    keyArray.add(favInfo.key);
                    cityArray.add(favInfo.city);
                    countryArray.add(favInfo.country);
                    costArray.add(favInfo.cost);
                }
                favoritesRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity().getApplicationContext(),
                        "Failed to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClickCitySummary(View view, int position) {
        // Go to city summary page
        if (countryArray.size() != 0) {
            Bundle bundle = new Bundle();
            bundle.putString("countryName", countryArray.get(position));
            bundle.putString("cityName", cityArray.get(position));
            Intent intent = new Intent(getActivity(), CitySummary.class);
            intent.putExtra("bundle", bundle);
            startActivity(intent);


//            Fragment citySummaryFragment = new CitySummaryFragment();
//            citySummaryFragment.setArguments(bundle);
//            getParentFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, citySummaryFragment)
//                    .addToBackStack("tag")
//                    .commit();
        }
    }

    @Override
    public void onClickDelete(View view, int position) {
        DatabaseReference dr = databaseReference.child(userID);
        String key = keyArray.get(position);
        dr.child(key).removeValue();
    }
}
