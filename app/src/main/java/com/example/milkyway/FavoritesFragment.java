package com.example.milkyway;

import android.annotation.SuppressLint;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FavoritesFragment extends Fragment implements FavoritesItemClickListener{

    RecyclerView recyclerView;
    private FirebaseUser user;
    private DatabaseReference databaseReference ;
    private String userID;
    private DatabaseReference userFavData;
    private TextView hiddenInfoKey;
    FavoritesRecyclerViewAdapter favoritesRecyclerViewAdapter;
//    View.OnClickListener onClickListener = null;

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

        String flagUrl = "https://countryflagsapi.com/png/";

        favoritesRecyclerViewAdapter = new FavoritesRecyclerViewAdapter(view.getContext(),
                keyArray, countryArray, cityArray, costArray, flagUrl);
        favoritesRecyclerViewAdapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(favoritesRecyclerViewAdapter);

        getData(view);

        return view;
    }

//    public FavoritesFragment(View.OnClickListener listener) {
//        this.onClickListener = listener;
//    }

    private void getData(View view) {
        userFavData.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
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
        if (countryArray != null) {
            Bundle bundle = new Bundle();
            bundle.putString("countryName", countryArray.get(position));
            bundle.putString("cityName", cityArray.get(position));
            Intent intent = new Intent(getActivity(), CitySummary.class);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onClickDelete(View view, int position) {
        DatabaseReference dr = databaseReference.child(userID);
        String key = keyArray.get(position);
        dr.child(key).removeValue();
    }
}
