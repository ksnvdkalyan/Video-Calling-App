package com.example.videomeet.listners;

import com.example.videomeet.models.User;

public interface UserListener {

    void initiateVideoMeeting(User user);

    void initiateAudioMeeting(User user);

    void onMultipleUsersAction(Boolean isMultipleUsersSelected);

}
