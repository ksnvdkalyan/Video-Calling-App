package com.example.videomeet.meetingnotification;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.videomeet.R;

public class MeetingService extends Service {

    public MeetingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        sendNotification(intent);
        Intent alarmIntent = new Intent(this, AlarmService.class);
        stopService(alarmIntent);
        return START_STICKY;
    }

    public void sendNotification(Intent intent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notifyTask")
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("description"))
                .setSmallIcon(R.drawable.ic_video)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(200, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
