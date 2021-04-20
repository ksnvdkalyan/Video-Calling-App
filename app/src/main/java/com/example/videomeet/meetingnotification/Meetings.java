package com.example.videomeet.meetingnotification;

public class Meetings {

    private String meetingName, meetingDescription;
    private String status = "pending";
    private long timeInMs;
    private boolean completed = false;

    public Meetings() {

    }

    public Meetings(String meetingName, String meetingDescription, long timeInMs) {
        this.meetingName = meetingName;
        this.meetingDescription = meetingDescription;
        this.timeInMs = timeInMs;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingDescription() {
        return meetingDescription;
    }

    public void setMeetingDescription(String meetingDescription) {
        this.meetingDescription = meetingDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimeInMs() {
        return timeInMs;
    }

    public void setTimeInMs(long timeInMs) {
        this.timeInMs = timeInMs;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
