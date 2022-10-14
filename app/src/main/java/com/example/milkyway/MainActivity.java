package com.example.milkyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        Button toTest = findViewById(R.id.button);
        toTest.setOnClickListener(view -> {
            Intent intent = new Intent(this, dynamic_test.class);
            startActivity(intent);
        });

        Button toLanding = findViewById(R.id.landing);
        toLanding.setOnClickListener(view -> {
            Intent intent = new Intent(this, StartUpLogoAnimation.class);
            startActivity(intent);
        });
    }
    public void mainActivitySearch(View view) {
        Intent searchIntent = new Intent(this, SearchActivity.class);
        startActivity(searchIntent);
    }

}