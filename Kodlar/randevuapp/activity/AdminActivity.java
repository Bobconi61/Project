package com.mainPackage.randevuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mainPackage.randevuapp.Database.DatabaseHelper;
import com.mainPackage.randevuapp.Model.Doctor;
import com.mainPackage.randevuapp.Model.Hospital;
import com.mainPackage.randevuapp.Model.TimeInterval;
import com.mainPackage.randevuapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminActivity extends AppCompatActivity {

    private EditText hospitalCounty, hospitalDistrict, hospitalName;
    private Button addHospitalButton;

    private EditText doctorHospitalName, doctorName, doctorDepartment;
    private Button addDoctorButton;

    private Button goToRemovePageButton;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

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

        dbHelper.addHospital(new Hospital(county, district, name));
        Toast.makeText(this, "Hospital added successfully", Toast.LENGTH_SHORT).show();
        // Clear fields
        hospitalCounty.setText("");
        hospitalDistrict.setText("");
        hospitalName.setText("");
    }

    private void addDoctor() {
        String hospital = doctorHospitalName.getText().toString().trim();
        String name = doctorName.getText().toString().trim();
        String department = doctorDepartment.getText().toString().trim();

        if (hospital.isEmpty() || name.isEmpty() || department.isEmpty()) {
            Toast.makeText(this, "Please fill all doctor fields", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.addDoctor(new Doctor(hospital, name, department));
        Toast.makeText(this, "Doctor added successfully", Toast.LENGTH_SHORT).show();

        // Generate schedule for the new doctor
        int doctorId = dbHelper.getDoctorId(name);
        if (doctorId != -1) {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (int i = 0; i < 7; i++) {
                LocalDate date = today.plusDays(i);
                String formattedDate = date.format(formatter);
                giveTimeIntervals(doctorId, formattedDate);
            }
            Toast.makeText(this, "Schedule created for " + name, Toast.LENGTH_SHORT).show();
        }
        // Clear fields
        doctorHospitalName.setText("");
        doctorName.setText("");
        doctorDepartment.setText("");
    }

    public void giveTimeIntervals(int doctorId, String date){
        // Morning session: 9:00 to 12:00
        for (int hour = 9; hour < 12; hour++) {
            for (int minute = 0; minute < 60; minute += 5) {
                String time = String.format("%02d:%02d", hour, minute);
                dbHelper.addTimeInterval(new TimeInterval(doctorId, date, time, true));
            }
        }

        // Afternoon session: 13:00 to 16:00
        for (int hour = 13; hour < 16; hour++) {
            for (int minute = 0; minute < 60; minute += 5) {
                String time = String.format("%02d:%02d", hour, minute);
                dbHelper.addTimeInterval(new TimeInterval(doctorId, date, time, true));
            }
        }
    }
}
