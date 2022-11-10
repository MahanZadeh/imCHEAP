package com.example.milkyway;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button searchButton = getView().findViewById(R.id.profileSearchButton);
        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);

        });

        // Change fragment when favorites button is clicked
        Button btn_favorites = getView().findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(nextView -> {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.profileFragmentContainerView, FavoritesFragment.class, null);
            ft.setReorderingAllowed(true);
            ft.addToBackStack("name"); // name can be null
            ft.commit();
        });

        // Change fragment when history button is clicked
        Button btn_history = getView().findViewById(R.id.btn_history);
        btn_history.setOnClickListener(nextView -> {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.profileFragmentContainerView, HistoryFragment.class, null);
            ft.setReorderingAllowed(true);
            ft.addToBackStack("name"); // name can be null
            ft.commit();
        });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

}