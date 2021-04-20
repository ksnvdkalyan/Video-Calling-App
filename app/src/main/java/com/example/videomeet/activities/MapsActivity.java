package com.example.videomeet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videomeet.R;
import com.example.videomeet.adapters.MapsAdapter;
import com.example.videomeet.models.User;
import com.example.videomeet.utilities.Constants;
import com.example.videomeet.utilities.PreferenceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MapsActivity extends AppCompatActivity{

    ChipNavigationBar chipNavigationBar;
    private List<User> users;
    private MapsAdapter userAdapter;
    private PreferenceManager preferenceManager;
    private TextView textErrorMessage;
    private ImageView noUser;
    private SwipeRefreshLayout swipeRefreshLayout;

    FusedLocationProviderClient mFusedLocationClient;
    FirebaseFirestore database;

    int PERMISSION_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        chipNavigationBar = findViewById(R.id.bottomNav);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_map, true);
        bottomMenu();

        preferenceManager = new PreferenceManager(getApplicationContext());

        noUser = findViewById(R.id.noUser);

        TextView textTile = findViewById(R.id.textTitle);
        textTile.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)
        ));

        RecyclerView userRecyclerView = findViewById(R.id.usersRecyclerView);
        textErrorMessage = findViewById(R.id.textErrorMessage);

        users = new ArrayList<>();
        userAdapter = new MapsAdapter(users);
        userRecyclerView.setAdapter(userAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::getUsers);

        getUsers();
    }

    @SuppressLint("NonConstantResourceId")
    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case R.id.bottom_nav_dashboard:
                    callHomeActivity();
                    break;
                case R.id.bottom_nav_meetings:
                    callMeetingsActivity();
                    break;
                case R.id.bottom_nav_map:
                    Toasty.success(MapsActivity.this, "You are in Maps", Toast.LENGTH_SHORT).show();
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

    public void callHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void callMeetingsActivity() {
        Intent intent = new Intent(getApplicationContext(), MeetingsActivity.class);
        startActivity(intent);
    }

    private void getUsers() {
        swipeRefreshLayout.setRefreshing(true);
        database = FirebaseFirestore.getInstance();
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

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient
                        .getLastLocation()
                        .addOnCompleteListener(
                                task -> {
                                    Location location = task.getResult();
                                    if (location == null) {
                                        requestNewLocationData();
                                    }
                                    else {
                                        DocumentReference documentReference =
                                                database.collection(Constants.KEY_COLLECTION_USERS).document(
                                                        preferenceManager.getString(Constants.KEY_USER_ID)
                                                );
                                        documentReference.update("longitude", String.valueOf(location.getLongitude()))
                                                .addOnFailureListener(e -> Toasty.error(MapsActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                                        documentReference.update("latitude", String.valueOf(location.getLatitude()))
                                                .addOnFailureListener(e -> Toasty.error(MapsActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    }
                                });
            }
            else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
        else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData()
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(
                LocationResult locationResult)
        {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            Location mLastLocation = locationResult.getLastLocation();
            DocumentReference documentReference =
                    database.collection(Constants.KEY_COLLECTION_USERS).document(
                            preferenceManager.getString(Constants.KEY_USER_ID)
                    );
            documentReference.update("longitude", String.valueOf(mLastLocation.getLongitude()))
                    .addOnFailureListener(e -> Toasty.error(MapsActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            mLastLocation.getLatitude();

            documentReference.update("latitude", String.valueOf(mLastLocation.getLatitude()))
                    .addOnFailureListener(e -> Toasty.error(MapsActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION },
                PERMISSION_ID);
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}