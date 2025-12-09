package com.mainPackage.randevuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mainPackage.randevuapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThirdActivity extends AppCompatActivity {

    private Spinner departmentSpinner;
    private Spinner doctorSpinner;
    private Button confirmAppointmentButton;
    private TextView selectedHospitalTextView;
    private FirebaseFirestore db;
    private String selectedHospital;
    private String userIdNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_third);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        departmentSpinner = findViewById(R.id.departmentSpinner);
        doctorSpinner = findViewById(R.id.doctorSpinner);
        confirmAppointmentButton = findViewById(R.id.confirmAppointmentButton);
        selectedHospitalTextView = findViewById(R.id.selectedHospitalTextView);

        confirmAppointmentButton.setEnabled(false);

        selectedHospital = getIntent().getStringExtra("SELECTED_HOSPITAL");
        userIdNumber = getIntent().getStringExtra("USER_ID_NUMBER");
        selectedHospitalTextView.setText(selectedHospital);

        loadDepartmentSpinner(selectedHospital);

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedDepartment = parent.getItemAtPosition(position).toString();
                    loadDoctorSpinner(selectedDepartment, selectedHospital);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        doctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                confirmAppointmentButton.setEnabled(position > 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                confirmAppointmentButton.setEnabled(false);
            }
        });

        confirmAppointmentButton.setOnClickListener(v -> {
            String selectedDoctorName = doctorSpinner.getSelectedItem().toString();
            db.collection("doctors")
                    .whereEqualTo("doctorName", selectedDoctorName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            String doctorId = task.getResult().getDocuments().get(0).getId();
                            Intent intent = new Intent(ThirdActivity.this, FourthActivity.class);
                            intent.putExtra("DOCTOR_ID", doctorId);
                            intent.putExtra("USER_ID_NUMBER", userIdNumber);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ThirdActivity.this, "Error finding doctor.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void loadDepartmentSpinner(String hospital) {
        db.collection("doctors").whereEqualTo("hospitalName", hospital).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Set<String> departments = new HashSet<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String department = document.getString("department");
                    if (department != null) {
                        departments.add(department);
                    }
                }
                List<String> departmentList = new ArrayList<>(departments);
                departmentList.add(0, "Departman Seçiniz");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                departmentSpinner.setAdapter(dataAdapter);
            } else {
                Toast.makeText(this, "Error loading departments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDoctorSpinner(String department, String hospital) {
        db.collection("doctors")
                .whereEqualTo("department", department)
                .whereEqualTo("hospitalName", hospital)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> doctors = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String doctor = document.getString("doctorName");
                            if (doctor != null) {
                                doctors.add(doctor);
                            }
                        }
                        doctors.add(0, "Doktor Seçiniz");
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doctors);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        doctorSpinner.setAdapter(dataAdapter);
                        doctorSpinner.setEnabled(true);
                    } else {
                        Toast.makeText(this, "Error loading doctors.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void moveToSymptomChecker(View view) {
        Intent intent = new Intent(this, SymptomCheckerActivity.class);
        startActivity(intent);
    }
}
