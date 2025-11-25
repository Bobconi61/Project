package com.mainPackage.randevuapp.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mainPackage.randevuapp.Database.DatabaseHelper;
import com.mainPackage.randevuapp.Model.Appointment;
import com.mainPackage.randevuapp.R;
import com.mainPackage.randevuapp.adopter.AppointmentAdapter;

import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter adapter;
    private DatabaseHelper dbHelper;
    private String userIdNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Enable the back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        userIdNumber = getIntent().getStringExtra("USER_ID_NUMBER");

        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAppointments();
    }

    private void loadAppointments() {
        int userId = dbHelper.getUserId(userIdNumber);
        List<Appointment> appointments = dbHelper.getAppointmentsForUser(userId);
        adapter = new AppointmentAdapter(appointments, dbHelper, this::loadAppointments);
        appointmentsRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the back button click
        onBackPressed();
        return true;
    }
}
