package com.mainPackage.randevuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mainPackage.randevuapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private EditText hospitalCounty, hospitalDistrict, hospitalName;
    private Button addHospitalButton;

    private EditText doctorHospitalName, doctorName, doctorDepartment;
    private Button addDoctorButton;

    private Button goToRemovePageButton;
    private FirebaseFirestore db;
    private static final String TAG = "AdminActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        // Hospital views
        hospitalCounty = findViewById(R.id.hospitalCounty);
        hospitalDistrict = findViewById(R.id.hospitalDistrict);
        hospitalName = findViewById(R.id.hospitalName);
        addHospitalButton = findViewById(R.id.addHospitalButton);

        // Doctor views
        doctorHospitalName = findViewById(R.id.doctorHospitalName);
        doctorName = findViewById(R.id.doctorName);
        doctorDepartment = findViewById(R.id.doctorDepartment);
        addDoctorButton = findViewById(R.id.addDoctorButton);

        goToRemovePageButton = findViewById(R.id.goToRemovePageButton);

        addHospitalButton.setOnClickListener(v -> addHospital());
        addDoctorButton.setOnClickListener(v -> addDoctor());

        goToRemovePageButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, RemoveActivity.class);
            startActivity(intent);
        });
    }

    private void addHospital() {
        String county = hospitalCounty.getText().toString().trim();
        String district = hospitalDistrict.getText().toString().trim();
        String name = hospitalName.getText().toString().trim();

        if (county.isEmpty() || district.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill all hospital fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> hospital = new HashMap<>();
        hospital.put("county", county);
        hospital.put("district", district);
        hospital.put("hospitalName", name);

        db.collection("hospitals").add(hospital)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Hospital added successfully", Toast.LENGTH_SHORT).show();
                    hospitalCounty.setText("");
                    hospitalDistrict.setText("");
                    hospitalName.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding hospital", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private void addDoctor() {
        String hospital = doctorHospitalName.getText().toString().trim();
        String name = doctorName.getText().toString().trim();
        String department = doctorDepartment.getText().toString().trim();

        if (hospital.isEmpty() || name.isEmpty() || department.isEmpty()) {
            Toast.makeText(this, "Please fill all doctor fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> doctor = new HashMap<>();
        doctor.put("hospitalName", hospital);
        doctor.put("doctorName", name);
        doctor.put("department", department);

        db.collection("doctors").add(doctor)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Doctor added successfully", Toast.LENGTH_SHORT).show();
                    String doctorId = documentReference.getId();
                    generateWeeklyScheduleForDoctor(doctorId);
                    doctorHospitalName.setText("");
                    doctorName.setText("");
                    doctorDepartment.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding doctor", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private void generateWeeklyScheduleForDoctor(String doctorId) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 7; i++) {
            String date = today.plusDays(i).format(formatter);
            giveTimeIntervals(doctorId, date);
        }
        Toast.makeText(this, "Schedule created for new doctor", Toast.LENGTH_SHORT).show();
    }

    public void giveTimeIntervals(String doctorId, String date) {
        // Morning session
        for (int hour = 9; hour < 12; hour++) {
            for (int minute = 0; minute < 60; minute += 5) {
                createTimeInterval(doctorId, date, hour, minute);
            }
        }
        // Afternoon session
        for (int hour = 13; hour < 16; hour++) {
            for (int minute = 0; minute < 60; minute += 5) {
                createTimeInterval(doctorId, date, hour, minute);
            }
        }
    }

    private void createTimeInterval(String doctorId, String date, int hour, int minute) {
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        Map<String, Object> timeInterval = new HashMap<>();
        timeInterval.put("doctorId", doctorId);
        timeInterval.put("date", date);
        timeInterval.put("startTime", time);
        timeInterval.put("available", true);
        db.collection("time_intervals").add(timeInterval);
    }
}
