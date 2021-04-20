package com.example.videomeet.meetingnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MeetingReceiver extends BroadcastReceiver {

    public MeetingReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context, MeetingService.class);
        newIntent.putExtra("title", intent.getStringExtra("title"));
        newIntent.putExtra("description", intent.getStringExtra("description"));
        context.startService(newIntent);
    }
}
