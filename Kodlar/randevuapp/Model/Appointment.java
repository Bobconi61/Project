package com.mainPackage.randevuapp.Model;

public class Appointment {
    private int timeIntervalId;
    private String doctorName;
    private String department;
    private String hospitalName;
    private String date;
    private String time;

    public Appointment(int timeIntervalId, String doctorName, String department, String hospitalName, String date, String time) {
        this.timeIntervalId = timeIntervalId;
        this.doctorName = doctorName;
        this.department = department;
        this.hospitalName = hospitalName;
        this.date = date;
        this.time = time;
    }

    // Getters
    public int getTimeIntervalId() {
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
}
