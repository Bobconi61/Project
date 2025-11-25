package com.mainPackage.randevuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mainPackage.randevuapp.Database.DatabaseHelper;
import com.mainPackage.randevuapp.R;

import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private Spinner countySpinner;
    private Spinner districtSpinner;
    private Spinner hospitalSpinner;
    private Button confirmButton;
    private DatabaseHelper dbHelper;
    private String userIdNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        userIdNumber = getIntent().getStringExtra("USER_ID_NUMBER");

        countySpinner = findViewById(R.id.countySpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        hospitalSpinner = findViewById(R.id.hospitalSpinner);
        confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setEnabled(false);

        loadCountySpinner();

        countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCounty = parent.getItemAtPosition(position).toString();
                loadDistrictSpinner(selectedCounty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not needed
            }
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = parent.getItemAtPosition(position).toString();
                loadHospitalSpinner(selectedDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not needed
            }
        });

        hospitalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                confirmButton.setEnabled(true);
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
        List<String> counties = dbHelper.getAllCounties();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, counties);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpinner.setAdapter(dataAdapter);
    }

    private void loadDistrictSpinner(String county) {
        List<String> districts = dbHelper.getDistrictsByCounty(county);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(dataAdapter);
    }

    private void loadHospitalSpinner(String district) {
        List<String> hospitals = dbHelper.getHospitalsByDistrict(district);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hospitals);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hospitalSpinner.setAdapter(dataAdapter);
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
