package com.example.milkyway;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

public class ResultsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Bundle bundle = getIntent().getExtras();

        TextView resultsDescription = findViewById(R.id.resultsTitle);
        String countryName = bundle.getString("Country Name").toUpperCase(Locale.ROOT);
        String pageTitle = "Search Results for " + countryName;
        resultsDescription.setText(pageTitle);

        Fragment results = new FragmentResults();
        results.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.ctnFragment, results);
        fragmentTransaction.commit();

        Button backButton = findViewById(R.id.resultsBack);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

    }
}
