package com.mainPackage.randevuapp.Model;

public class Appointment {
    private String timeIntervalId; // Changed to String to hold Firestore document ID
    private String doctorName;
    private String department;
    private String hospitalName;
    private String date;
    private String time;

    // No-argument constructor for Firestore (or other frameworks)
    public Appointment() {}

    public Appointment(String timeIntervalId, String doctorName, String department, String hospitalName, String date, String time) {
        this.timeIntervalId = timeIntervalId;
        this.doctorName = doctorName;
        this.department = department;
        this.hospitalName = hospitalName;
        this.date = date;
        this.time = time;
    }

    // Getters
    public String getTimeIntervalId() {
        return timeIntervalId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDepartment() {
        return department;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    // Setters
    public void setTimeIntervalId(String timeIntervalId) {
        this.timeIntervalId = timeIntervalId;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
