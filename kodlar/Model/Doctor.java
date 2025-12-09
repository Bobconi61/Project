package com.mainPackage.randevuapp.Model;

import com.google.firebase.firestore.DocumentId;

public class Doctor {
    @DocumentId
    private String id;
    private String hospitalName;
    private String doctorName;
    private String department;

    // No-argument constructor for Firestore
    public Doctor() {}

    public Doctor(String hospitalName, String doctorName, String department) {
        this.hospitalName = hospitalName;
        this.doctorName = doctorName;
        this.department = department;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
