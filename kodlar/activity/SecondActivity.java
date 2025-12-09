package com.mainPackage.randevuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mainPackage.randevuapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SecondActivity extends AppCompatActivity {

    private Spinner countySpinner;
    private Spinner districtSpinner;
    private Spinner hospitalSpinner;
    private Button confirmButton;
    private FirebaseFirestore db;
    private String userIdNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        userIdNumber = getIntent().getStringExtra("USER_ID_NUMBER");

        countySpinner = findViewById(R.id.countySpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        hospitalSpinner = findViewById(R.id.hospitalSpinner);
        confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setEnabled(false);
        districtSpinner.setEnabled(false);
        hospitalSpinner.setEnabled(false);

        loadCountySpinner();

        countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCounty = parent.getItemAtPosition(position).toString();
                    loadDistrictSpinner(selectedCounty);
                } else {
                    districtSpinner.setAdapter(null);
                    districtSpinner.setEnabled(false);
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinner.setEnabled(false);
                    confirmButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCounty = countySpinner.getSelectedItem().toString();
                    String selectedDistrict = parent.getItemAtPosition(position).toString();
                    loadHospitalSpinner(selectedCounty, selectedDistrict);
                } else {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinner.setEnabled(false);
                    confirmButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        hospitalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                confirmButton.setEnabled(position > 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                confirmButton.setEnabled(false);
            }
        });

        confirmButton.setOnClickListener(v -> {
            String selectedHospital = hospitalSpinner.getSelectedItem().toString();
            Intent intent = new Intent(this, ThirdActivity.class);
            intent.putExtra("SELECTED_HOSPITAL", selectedHospital);
            intent.putExtra("USER_ID_NUMBER", userIdNumber);
            startActivity(intent);
        });
    }

    private void loadCountySpinner() {
        db.collection("hospitals").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Set<String> counties = new HashSet<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String county = document.getString("county");
                    if (county != null) {
                        counties.add(county);
                    }
                }
                List<String> countyList = new ArrayList<>(counties);
                countyList.sort(String::compareTo);
                countyList.add(0, "İl Seçiniz");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countyList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                countySpinner.setAdapter(dataAdapter);
            } else {
                Toast.makeText(this, "Error loading counties.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDistrictSpinner(String county) {
        db.collection("hospitals").whereEqualTo("county", county).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Set<String> districts = new HashSet<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String district = document.getString("district");
                    if (district != null) {
                        districts.add(district);
                    }
                }
                List<String> districtList = new ArrayList<>(districts);
                districtList.sort(String::compareTo);
                districtList.add(0, "İlçe Seçiniz");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpinner.setAdapter(dataAdapter);
                districtSpinner.setEnabled(true);
            } else {
                Toast.makeText(this, "Error loading districts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadHospitalSpinner(String county, String district) {
        db.collection("hospitals")
                .whereEqualTo("county", county)
                .whereEqualTo("district", district)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> hospitals = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String hospital = document.getString("hospitalName");
                            if (hospital != null) {
                                hospitals.add(hospital);
                            }
                        }
                        hospitals.sort(String::compareTo);
                        hospitals.add(0, "Hastane Seçiniz");
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hospitals);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        hospitalSpinner.setAdapter(dataAdapter);
                        hospitalSpinner.setEnabled(true);
                    } else {
                        Toast.makeText(this, "Error loading hospitals.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void Back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void moveToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("USER_ID_NUMBER", userIdNumber);
        startActivity(intent);
    }

    public void moveToAppointments(View view) {
        Intent intent = new Intent(this, AppointmentsActivity.class);
        intent.putExtra("USER_ID_NUMBER", userIdNumber);
        startActivity(intent);
    }
}
