package com.example.videomeet.meetingnotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.videomeet.R;
import com.example.videomeet.activities.MeetingsActivity;

public class AlarmService extends Service {

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Intent notifyIntent = new Intent(this, MeetingService.class);
        notifyIntent.putExtra("title", intent.getStringExtra("title"));
        notifyIntent.putExtra("description", intent.getStringExtra("description"));
        PendingIntent pendingIntent = PendingIntent.getService
                (this, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    intent.getLongExtra("millis",0), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, intent.getLongExtra("millis",0), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, intent.getLongExtra("millis",0), pendingIntent);
        }
        startForeground();
        return START_NOT_STICKY;
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MeetingsActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(100, new NotificationCompat.Builder(this,
                "notifyTask") // don't forget create a notification channel first
                .setSmallIcon(R.drawable.ic_video)
                .setContentTitle(getString(R.string.app_name))
                .setNotificationSilent()
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
    }
}