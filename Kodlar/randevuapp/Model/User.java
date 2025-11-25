package com.mainPackage.randevuapp.Model;

public class User {
    private int id;
    private String idNumber;
    private String password;

    public User(String idNumber, String password) {
        this.idNumber = idNumber;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
