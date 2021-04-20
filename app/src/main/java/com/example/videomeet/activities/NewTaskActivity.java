package com.example.videomeet.activities;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.videomeet.meetingnotification.AlarmService;
import com.example.videomeet.meetingnotification.Meetings;
import com.example.videomeet.R;
import com.example.videomeet.utilities.Constants;
import com.example.videomeet.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import es.dmoral.toasty.Toasty;

public class NewTaskActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    EditText editTextTaskName, editTextTaskDescription, editTextDate, editTextTime;
    Button dateButton, timeButton, createTaskButton, backButton;
    private int mYear, mMonth, mDay, mHour, mMinute;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        preferenceManager = new PreferenceManager(getApplicationContext());

        createNotificationChannel();

        editTextTaskName = findViewById(R.id.et_task_name);
        editTextTaskDescription = findViewById(R.id.et_task_description);
        editTextDate = findViewById(R.id.et_date);
        editTextTime = findViewById(R.id.et_time);

        dateButton = findViewById(R.id.btn_date);
        timeButton = findViewById(R.id.btn_time);
        createTaskButton = findViewById(R.id.btn_new_task);
        backButton = findViewById(R.id.btn_back);

        database = FirebaseFirestore.getInstance();

        dateButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> editTextDate.setText(new DecimalFormat("00").format(dayOfMonth) + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        timeButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            editTextTime.setText(new DecimalFormat("00").format(hourOfDay) + ":" + new DecimalFormat("00").format(minute) + ":" + new DecimalFormat("00").format(0));
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MeetingsActivity.class));
            }
        });

        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String taskName = editTextTaskName.getText().toString().trim();
                String taskDescription = editTextTaskDescription.getText().toString().trim();
                String date = editTextDate.getText().toString().trim();
                String time = editTextTime.getText().toString().trim();

                if (TextUtils.isEmpty(taskName)) {
                    editTextTaskName.setError("Required Task Name");
                    return;
                }

                if (TextUtils.isEmpty(taskDescription)) {
                    editTextTaskDescription.setError("Required Task Description");
                    return;
                }

                if (TextUtils.isEmpty(date)) {
                    editTextDate.setError("Required Date");
                    return;
                }

                if (TextUtils.isEmpty(time)) {
                    editTextTime.setError("Required Time");
                    return;
                }

                String myDate = date + " " + time;
                LocalDateTime localDateTime = LocalDateTime.parse(myDate,
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                long millis = localDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli();

                Intent alarmIntent = new Intent(NewTaskActivity.this, AlarmService.class);

                alarmIntent.putExtra("millis", millis);
                alarmIntent.putExtra("title", taskName);
                alarmIntent.putExtra("description", taskDescription);

                startService(alarmIntent);


                Meetings taskData = new Meetings(taskName, taskDescription, millis);
                database.collection(Constants.KEY_COLLECTION_MEETINGS)
                        .document("doc")
                        .collection(preferenceManager.getString(Constants.KEY_EMAIL))
                        .document()
                        .set(taskData)
                        .addOnCompleteListener(task -> {
                            Toasty.success(NewTaskActivity.this, "Meeting created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MeetingsActivity.class));
                        });
            }
        });
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT ;
            NotificationChannel notificationChannel = new NotificationChannel( "notifyTask" , "TaskReminderChannel" , importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
