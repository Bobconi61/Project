package com.mainPackage.randevuapp.Model;

public class Doctor {
    private int doctorId;
    private String hospitalName;
    private String doctorName;
    private String department;

    public Doctor(String hospitalName, String doctorName, String department) {
        this.hospitalName = hospitalName;
        this.doctorName = doctorName;
        this.department = department;
    }

    // Getters and Setters
    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
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
