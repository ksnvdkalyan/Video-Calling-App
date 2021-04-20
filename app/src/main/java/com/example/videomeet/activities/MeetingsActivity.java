package com.example.videomeet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videomeet.meetingnotification.MeetingViewHolder;
import com.example.videomeet.meetingnotification.Meetings;
import com.example.videomeet.R;
import com.example.videomeet.utilities.Constants;
import com.example.videomeet.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MeetingsActivity extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    private PreferenceManager preferenceManager;
    ImageButton addButton;
    TextView userName;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter<Meetings, MeetingViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);

        preferenceManager = new PreferenceManager(getApplicationContext());

        recyclerView = findViewById(R.id.meetingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userName = findViewById(R.id.userName);
        userName.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)
        ));

        addButton = findViewById(R.id.buttonNewTask);
        addButton.setOnClickListener(view -> NewTaskActivity());

        chipNavigationBar = findViewById(R.id.bottomNav);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_meetings, true);
        bottomMenu();

        displayMeetings();
    }

    @SuppressLint("NonConstantResourceId")
    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case R.id.bottom_nav_dashboard:
                    callDashboardActivity();
                    break;
                case R.id.bottom_nav_meetings:
                    Toasty.success(MeetingsActivity.this, "You are in Meetings", Toast.LENGTH_SHORT).show();
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

    public void NewTaskActivity() {
        Intent intent = new Intent(getApplicationContext(), NewTaskActivity.class);
        startActivity(intent);
    }

    public void callMapActivity() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    public void callDashboardActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void callProfileActivity() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    public void displayMeetings() {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection(Constants.KEY_COLLECTION_MEETINGS)
                .document("doc")
                .collection(preferenceManager.getString(Constants.KEY_EMAIL))
                .orderBy("timeInMs", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Meetings> options = new FirestoreRecyclerOptions.Builder<Meetings>()
                .setQuery(query, Meetings.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Meetings, MeetingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MeetingViewHolder holder, int position, @NonNull Meetings model) {
                @SuppressLint("SimpleDateFormat")
                DateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
                DateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("EN", "INDIA"));
                Date date = new Date(model.getTimeInMs());
                String dayString = dayFormat.format(date)+" "+ timeFormat.format(date);
                holder.textViewTitle.setText(model.getMeetingName());
                holder.textViewDescription.setText(model.getMeetingDescription());
                holder.textViewDay.setText(dayString);
                holder.textViewStatus.setText(model.getStatus());
                if(!model.isCompleted()) {
                    holder.doneButton.setOnClickListener(v -> {
                        model.setStatus("done");
                        model.setCompleted(true);
                        String keyName = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                        rootRef.collection(Constants.KEY_COLLECTION_MEETINGS)
                                .document("doc")
                                .collection(preferenceManager.getString(Constants.KEY_EMAIL))
                                .document(keyName).set(model);
                    });
                }
            }

            @Override
            public int getItemViewType(int position) {
                Meetings model = getItem(position);
                if(!model.isCompleted()){
                    return 0;
                }
                else
                {
                    return 1;
                }
            }

            @NonNull
            @Override
            public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                switch (viewType) {
                    case 1: View view_done = layoutInflater.inflate(R.layout.item_done_layout, parent, false);
                        return new MeetingViewHolder(view_done);
                    default: View view_pending = layoutInflater.inflate(R.layout.item_pending_layout, parent, false);
                        return new MeetingViewHolder(view_pending);
                }
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}