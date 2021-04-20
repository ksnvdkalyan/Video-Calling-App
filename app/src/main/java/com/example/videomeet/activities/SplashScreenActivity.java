package com.example.videomeet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.videomeet.R;
import com.example.videomeet.utilities.Constants;
import com.example.videomeet.utilities.PreferenceManager;

public class SplashScreenActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_FIRST_TIME)) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startLoginActivity();
                }
            },2000);
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startOnBoardScreen();
                }
            },2000);
        }
    }

    public void startOnBoardScreen() {
        Intent intent = new Intent(this, onBoardingScreenActivity.class);
        startActivity(intent);
    }

    public void startLoginActivity() {
        startActivity(new Intent(getApplicationContext(), loginActivity.class));
    }
}