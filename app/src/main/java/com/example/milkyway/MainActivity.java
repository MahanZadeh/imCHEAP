package com.example.milkyway;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start_up_logo_animation);

        Animation fadOut = new AlphaAnimation(1, 0);
        fadOut.setInterpolator(new AccelerateInterpolator());
        fadOut.setStartOffset(500);
        fadOut.setDuration(2000);
        ImageView image = findViewById(R.id.imageView2);
        image.setAnimation(fadOut);
        int SPLASH_SCREEN_TIMEOUT = 2500;
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN_TIMEOUT);

    }
}