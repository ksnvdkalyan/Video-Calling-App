package com.example.videomeet.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.videomeet.onboarding.OnBoardingAdapter;
import com.example.videomeet.onboarding.OnBoardingItem;
import com.example.videomeet.R;
import com.example.videomeet.utilities.Constants;
import com.example.videomeet.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class onBoardingScreenActivity extends AppCompatActivity {

    private OnBoardingAdapter onBoardingAdapter;
    private LinearLayout layoutOnBoardingIndicator;
    private Button buttonOnBoardingAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screens);

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());

        preferenceManager.putBoolean(Constants.KEY_FIRST_TIME, true);
        buttonOnBoardingAction = findViewById(R.id.buttonOnBoardingAction);
        layoutOnBoardingIndicator = findViewById(R.id.layoutOnBoardingIndicators);

        setUpOnBoardingItems();

        ViewPager2 onBoardingViewPager = findViewById(R.id.onBoardingViewPager);
        onBoardingViewPager.setAdapter(onBoardingAdapter);

        setUpOnBoardingIndicators();
        setCurrentOnBoardingIndicator(0);

        onBoardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnBoardingIndicator(position);
            }
        });

        buttonOnBoardingAction.setOnClickListener(view -> {
            if (onBoardingViewPager.getCurrentItem() + 1 < onBoardingAdapter.getItemCount()) {
                onBoardingViewPager.setCurrentItem(onBoardingViewPager.getCurrentItem() + 1);
            } else {
                startActivity(new Intent(getApplicationContext(), loginActivity.class));
                finish();
            }
        });
    }

    private void setUpOnBoardingItems() {

        List<OnBoardingItem> onBoardingItems = new ArrayList<>();

        OnBoardingItem itemMeeting = new OnBoardingItem();
        itemMeeting.setTitle("Video and Audio Calling app");
        itemMeeting.setDescription("Better video and audio calling experience");
        itemMeeting.setImage(R.drawable.video_call_1);

        OnBoardingItem itemMeeting2 = new OnBoardingItem();
        itemMeeting2.setTitle("Connect everywhere from anywhere");
        itemMeeting2.setDescription("Easy way to connect and only thing is clicking a button");
        itemMeeting2.setImage(R.drawable.video_call_2);

        OnBoardingItem meetings = new OnBoardingItem();
        meetings.setTitle("Set Meetings and get notified");
        meetings.setDescription("Add a meeting time and date and get notified then");
        meetings.setImage(R.drawable.task);

        onBoardingItems.add(itemMeeting);
        onBoardingItems.add(itemMeeting2);
        onBoardingItems.add(meetings);

        onBoardingAdapter = new OnBoardingAdapter(onBoardingItems);
    }

    private void setUpOnBoardingIndicators() {
        ImageView [] indicators = new ImageView[onBoardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators [i] = new ImageView(getApplicationContext());
            indicators [i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators [i].setLayoutParams(layoutParams);
            layoutOnBoardingIndicator.addView(indicators[i]);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentOnBoardingIndicator(int index) {
        int childCount = layoutOnBoardingIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView =(ImageView) layoutOnBoardingIndicator.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if (index == onBoardingAdapter.getItemCount() - 1) {
            buttonOnBoardingAction.setText("Start");
        } else {
            buttonOnBoardingAction.setText("Next");
        }
    }
}
