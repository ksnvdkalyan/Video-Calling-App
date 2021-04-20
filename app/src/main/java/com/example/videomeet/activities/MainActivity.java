package com.example.videomeet.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videomeet.R;
import com.example.videomeet.adapters.UserAdapter;
import com.example.videomeet.listners.UserListener;
import com.example.videomeet.models.User;
import com.example.videomeet.utilities.Constants;
import com.example.videomeet.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements UserListener {

    private PreferenceManager preferenceManager;
    private List<User> users;
    private UserAdapter userAdapter;
    private TextView textErrorMessage;
    private ImageView noUser;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageConference;
    ChipNavigationBar chipNavigationBar;

    private int REQUEST_CODE_BATTERY_OPTIMIZATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chipNavigationBar = findViewById(R.id.bottomNav);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_dashboard, true);
        bottomMenu();

        preferenceManager = new PreferenceManager(getApplicationContext());

        noUser = findViewById(R.id.noUser);
        imageConference = findViewById(R.id.imageConference);

        TextView textTile = findViewById(R.id.textTitle);
        textTile.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)
        ));

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                sendFCMTokenToDatabase(task.getResult().getToken());
            }
        });

        RecyclerView userRecyclerView = findViewById(R.id.usersRecyclerView);
        textErrorMessage = findViewById(R.id.textErrorMessage);

        users = new ArrayList<>();
        userAdapter = new UserAdapter(users, this);
        userRecyclerView.setAdapter(userAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::getUsers);

        getUsers();
        checkForBatteryOptimizations();
    }

    @SuppressLint("NonConstantResourceId")
    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case R.id.bottom_nav_dashboard:
                    Toasty.success(MainActivity.this, "You are in Home", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.bottom_nav_meetings:
                    callMeetingsActivity();
                    break;
                case R.id.bottom_nav_map:
                    callMapActivity();
                    break;
                case R.id.bottom_nav_profile:
                    callProfileActivity();
                    break;
            }
        });
    }

    public void callProfileActivity() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    public void callMapActivity() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    public void callMeetingsActivity() {
        Intent intent = new Intent(getApplicationContext(), MeetingsActivity.class);
        startActivity(intent);
    }

    private void getUsers() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        // To remove previous data
                        users.clear();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if (myUserId.equals(documentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
                            user.lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
                            user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                            user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.latitude =documentSnapshot.getString("latitude");
                            user.longitude =documentSnapshot.getString("longitude");
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            userAdapter.notifyDataSetChanged();
                        } else {
                            noUser.setVisibility(View.VISIBLE);
                            textErrorMessage.setText(String.format("%s", "No users available"));
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }
                    } else {
                        textErrorMessage.setText(String.format("%s", "No users available"));
                        textErrorMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void sendFCMTokenToDatabase(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> Toasty.error(MainActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        documentReference.update("longitude", String.valueOf(0))
                .addOnFailureListener(e -> Toasty.error(MainActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        documentReference.update("latitude", String.valueOf(0))
                .addOnFailureListener(e -> Toasty.error(MainActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void initiateVideoMeeting(User user) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toasty.error(this, user.firstName + " " + user.lastName + " is not available for meeting", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "video");
            startActivity(intent);
        }
    }

    @Override
    public void initiateAudioMeeting(User user) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toasty.error(this, user.firstName + " " + user.lastName + " is not available for meeting", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "audio");
            startActivity(intent);
        }
    }

    @Override
    public void onMultipleUsersAction(Boolean isMultipleUsersSelected) {
        if (isMultipleUsersSelected) {
            imageConference.setVisibility(View.VISIBLE);
            imageConference.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                intent.putExtra("selectedUsers", new Gson().toJson(userAdapter.getSelectedUsers()));
                intent.putExtra("type", "video");
                intent.putExtra("isMultiple", true);
                startActivity(intent);
            });
        } else {
            imageConference.setVisibility(View.GONE);
        }
    }

    private void checkForBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("Battery optimization is enabled. It can interrupt running background services.");
                builder.setPositiveButton("Disable", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivityForResult(intent, REQUEST_CODE_BATTERY_OPTIMIZATION);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BATTERY_OPTIMIZATION) {
            checkForBatteryOptimizations();
        }
    }
}