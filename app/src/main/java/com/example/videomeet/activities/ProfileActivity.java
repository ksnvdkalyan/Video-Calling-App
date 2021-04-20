package com.example.videomeet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videomeet.R;
import com.example.videomeet.utilities.Constants;
import com.example.videomeet.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    private PreferenceManager preferenceManager;
    LinearLayout homePage, meetingsPage, infoPage, mapsPage;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferenceManager = new PreferenceManager(getApplicationContext());

        homePage = findViewById(R.id.homePage);
        homePage.setOnClickListener(view -> callDashboardActivity());

        mapsPage = findViewById(R.id.mapsPage);
        mapsPage.setOnClickListener(view -> callMapActivity());

        meetingsPage = findViewById(R.id.meetingsPage);
        meetingsPage.setOnClickListener(view -> callMeetingsActivity());

        infoPage = findViewById(R.id.infoPage);
        infoPage.setOnClickListener(view -> callInfoActivity());

        chipNavigationBar = findViewById(R.id.bottomNav);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_profile, true);
        bottomMenu();

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> logOut());

        TextView profileEmail = findViewById(R.id.profileEmail);
        TextView profileFirstName = findViewById(R.id.profileFirstName);
        TextView profileFirstChar = findViewById(R.id.profileFirstChar);

        profileFirstChar.setText(preferenceManager.getString(Constants.KEY_FIRST_NAME).substring(0, 1));
        profileEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        profileFirstName.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)
        ));
    }

    @SuppressLint("NonConstantResourceId")
    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case R.id.bottom_nav_dashboard:
                    callDashboardActivity();
                    break;
                case R.id.bottom_nav_meetings:
                    callMeetingsActivity();
                    break;
                case R.id.bottom_nav_map:
                    callMapActivity();
                    break;
                case R.id.bottom_nav_profile:
                    Toasty.success(ProfileActivity.this, "You are in Profile", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    public void callDashboardActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void callMapActivity() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    public void callInfoActivity() {
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
    }

    public void callMeetingsActivity() {
        Intent intent = new Intent(getApplicationContext(), MeetingsActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        Toasty.success(this, "Signing Out..", Toast.LENGTH_SHORT).show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(aVoid -> {
                    preferenceManager.clearPreferences();
                    startActivity(new Intent(getApplicationContext(), loginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toasty.error(ProfileActivity.this, "Unable to log out", Toast.LENGTH_SHORT).show());
    }
}