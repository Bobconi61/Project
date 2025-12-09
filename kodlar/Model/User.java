package com.mainPackage.randevuapp.Model;

import com.google.firebase.firestore.DocumentId;

public class User {
    @DocumentId
    private String uid; // This will hold the Firebase Auth User ID
    private String idNumber;

    // No-argument constructor for Firestore
    public User() {}

    public User(String idNumber) {
        this.idNumber = idNumber;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
