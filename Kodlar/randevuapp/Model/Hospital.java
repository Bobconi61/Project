package com.mainPackage.randevuapp.Model;

public class Hospital {
    private int id;
    private String county;
    private String district;
    private String hospitalName;

    public Hospital(String county, String district, String hospitalName) {
        this.county = county;
        this.district = district;
        this.hospitalName = hospitalName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
}
