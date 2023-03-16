package com.example.indiasocial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.indiasocial.databinding.ActivitySplashScreenBinding;

public class Splash_Screen extends AppCompatActivity {

    // Variable for time dilay Splash Screen
    private static int SPLASH_SCREEN = 2000;
    // Variables
    Animation topAnim, bottomAnim;

    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide the Action bar
        getSupportActionBar().hide();

        // change the colour of Status Bar
        getWindow().setStatusBarColor(ContextCompat.getColor(Splash_Screen.this,R.color.colorPrimaryVariant));
        // Remove or hide the Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Animation
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        // Hooks
//        img = findViewById(R.id.logo);
//        bharat = findViewById(R.id.bharat);
//        welcome = findViewById(R.id.welcome);

        // Set Animation
        binding.logo.setAnimation(topAnim);
        binding.appName.setAnimation(bottomAnim);
        binding.welcome.setAnimation(bottomAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Moving from splash screen activity to the sign_Up activity
                Intent intent = new Intent(Splash_Screen.this, Sign_in.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}