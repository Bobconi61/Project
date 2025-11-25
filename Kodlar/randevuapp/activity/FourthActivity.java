package com.mainPackage.randevuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mainPackage.randevuapp.Database.DatabaseHelper;
import com.mainPackage.randevuapp.Model.TimeInterval;
import com.mainPackage.randevuapp.R;
import com.mainPackage.randevuapp.adopter.TimeIntervalAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FourthActivity extends AppCompatActivity {

    private RecyclerView timeIntervalRecyclerView;
    private TimeIntervalAdapter adapter;
    private DatabaseHelper dbHelper;
    private int doctorId;
    private String userIdNumber;
    private TextView dateTextView;
    private Button prevDayButton, nextDayButton;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fourth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        doctorId = getIntent().getIntExtra("DOCTOR_ID", -1);
        userIdNumber = getIntent().getStringExtra("USER_ID_NUMBER");

        dateTextView = findViewById(R.id.dateTextView);
        prevDayButton = findViewById(R.id.prevDayButton);
        nextDayButton = findViewById(R.id.nextDayButton);
        timeIntervalRecyclerView = findViewById(R.id.timeIntervalRecyclerView);
        timeIntervalRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        calendar = Calendar.getInstance();

        prevDayButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            updateDate();
        });

        nextDayButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            updateDate();
        });

        updateDate();
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(calendar.getTime());
        dateTextView.setText(formattedDate);
        loadTimeIntervals(formattedDate);
    }

    private void loadTimeIntervals(String date) {
        List<TimeInterval> timeIntervals = dbHelper.getTimeIntervalsByDoctorAndDate(doctorId, date);

        // Define the action to be taken after a successful booking
        Runnable onAppointmentBooked = () -> {
            Intent intent = new Intent(FourthActivity.this, AppointmentsActivity.class);
            // We need to pass the user's ID to the appointments screen
            intent.putExtra("USER_ID_NUMBER", userIdNumber);
            startActivity(intent);
            // Finish this activity so the user can't navigate back to a stale booking screen
            finish();
        };

        adapter = new TimeIntervalAdapter(timeIntervals, dbHelper, this, userIdNumber, onAppointmentBooked);
        timeIntervalRecyclerView.setAdapter(adapter);
    }
}
