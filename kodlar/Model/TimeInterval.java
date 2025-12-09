package com.mainPackage.randevuapp.Model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class TimeInterval {
    @DocumentId
    private String timeIntervalId;
    private String doctorId;
    private String date;
    private String startTime;
    private String bookedByUserId;
    private boolean available; // Corrected field name

    // No-argument constructor for Firestore
    public TimeInterval() {}

    public TimeInterval(String doctorId, String date, String startTime, boolean available) {
        this.doctorId = doctorId;
        this.date = date;
        this.startTime = startTime;
        this.available = available;
    }

    // Getters and Setters
    public String getTimeIntervalId() {
        return timeIntervalId;
    }

    public void setTimeIntervalId(String timeIntervalId) {
        this.timeIntervalId = timeIntervalId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
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

    public String getBookedByUserId() {
        return bookedByUserId;
    }

    public void setBookedByUserId(String bookedByUserId) {
        this.bookedByUserId = bookedByUserId;
    }

    // Use @PropertyName to map Firestore's "available" to this getter
    @PropertyName("available")
    public boolean isAvailable() {
        return available;
    }

    @PropertyName("available")
    public void setAvailable(boolean available) {
        this.available = available;
    }
}
