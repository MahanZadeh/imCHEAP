package com.example.milkyway;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentResults extends Fragment implements ResultsItemClickListener{

    RecyclerView recyclerView;
    String[] cities, costs;
    int[] images = {R.drawable.usa_flag};

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
        cities = getResources().getStringArray(R.array.cities);
        costs = getResources().getStringArray(R.array.costs);

        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(),
                cities, costs, images);
        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(myRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(getActivity(), dynamic_test.class);
        startActivity(intent);
    }
}
