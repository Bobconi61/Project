package com.mainPackage.randevuapp.Model;

public class TimeInterval {
    private int timeIntervalId;
    private int doctorId;
    private String date;
    private String startTime;
    private int userId;
    private boolean available;

    // Constructor
    public TimeInterval(int doctorId, String date, String startTime, boolean available) {
        this.doctorId = doctorId;
        this.date = date;
        this.startTime = startTime;
        this.available = available;
    }

    // Getters and Setters
    public int getTimeIntervalId() {
        return timeIntervalId;
    }

    public void setTimeIntervalId(int timeIntervalId) {
        this.timeIntervalId = timeIntervalId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
