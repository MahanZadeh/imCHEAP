package com.example.milkyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    }


}