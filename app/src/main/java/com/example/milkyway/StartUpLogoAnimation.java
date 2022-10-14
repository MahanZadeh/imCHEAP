package com.example.milkyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class StartUpLogoAnimation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_logo_animation);
    }

//    ImageView logo = findViewById(R.id.imageView);

    public void plane(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }


}