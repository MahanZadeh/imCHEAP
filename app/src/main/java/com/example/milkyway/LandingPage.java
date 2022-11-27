package com.example.milkyway;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LandingPage extends AppCompatActivity{
    private ProgressBar progressBar;
    private TextView loggingYouInMessage;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        progressBar = findViewById(R.id.progressBar);
        loggingYouInMessage = findViewById(R.id.loggingYouIn);
        progressBar.setVisibility(View.VISIBLE);


        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SearchFragment())
                    .commitAllowingStateLoss();
            progressBar.setVisibility(View.GONE);
            loggingYouInMessage.setVisibility(View.GONE);
        }, 2000);

        BottomNavigationView bottomNavigationView= findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.search_panel:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.profile_panel:
                    selectedFragment = new ProfileFragment();
                    break;
                default:
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack("tag")
                        .commit();
            }
            return false;
        });
    }

}
