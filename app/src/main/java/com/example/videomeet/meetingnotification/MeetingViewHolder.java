package com.example.videomeet.meetingnotification;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videomeet.R;

public class MeetingViewHolder extends RecyclerView.ViewHolder {

    public TextView textViewTitle, textViewDescription, textViewDay, textViewStatus;
    public ImageButton doneButton;

    public MeetingViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewTitle = itemView.findViewById(R.id.tv_title);
        textViewDescription = itemView.findViewById(R.id.tv_description);
        textViewDay = itemView.findViewById(R.id.tv_day);
        textViewStatus = itemView.findViewById(R.id.tv_status);
        doneButton = itemView.findViewById(R.id.ib_done);

    }

}
