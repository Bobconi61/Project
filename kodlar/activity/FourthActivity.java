package com.mainPackage.randevuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mainPackage.randevuapp.Model.TimeInterval;
import com.mainPackage.randevuapp.R;
import com.mainPackage.randevuapp.adapter.TimeIntervalAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FourthActivity extends AppCompatActivity {

    private RecyclerView timeIntervalRecyclerView;
    private TimeIntervalAdapter adapter;
    private FirebaseFirestore db;
    private String doctorId;
    private String userId;
    private TextView dateTextView;
    private Button prevDayButton, nextDayButton;
    private Calendar calendar;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final String TAG = "FourthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        doctorId = getIntent().getStringExtra("DOCTOR_ID");

        if (doctorId == null || doctorId.trim().isEmpty()) {
            Toast.makeText(this, "Error: Doctor ID is missing.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to book an appointment.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        userId = currentUser.getUid();

        dateTextView = findViewById(R.id.dateTextView);
        prevDayButton = findViewById(R.id.prevDayButton);
        nextDayButton = findViewById(R.id.nextDayButton);
        timeIntervalRecyclerView = findViewById(R.id.timeIntervalRecyclerView);
        timeIntervalRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        calendar = Calendar.getInstance();

        prevDayButton.setOnClickListener(v -> findPreviousAvailableDate());
        nextDayButton.setOnClickListener(v -> findNextAvailableDate());

        findNextAvailableDate(sdf.format(new Date(0))); // Start with the first available date
    }

    private void updateDate() {
        String formattedDate = sdf.format(calendar.getTime());
        dateTextView.setText(formattedDate);
        loadTimeIntervals(formattedDate);
        updateButtonStates(formattedDate);
    }

    private void findNextAvailableDate() {
        findNextAvailableDate(sdf.format(calendar.getTime()));
    }

    private void findNextAvailableDate(String fromDate) {
        db.collection("time_intervals")
                .whereEqualTo("doctorId", doctorId)
                .whereGreaterThan("date", fromDate)
                .orderBy("date", Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String nextDateStr = queryDocumentSnapshots.getDocuments().get(0).getString("date");
                        try {
                            calendar.setTime(sdf.parse(nextDateStr));
                            updateDate();
                        } catch (ParseException e) {
                            Log.e(TAG, "Date parsing error for next date", e);
                        }
                    } else {
                        if(fromDate.equals(sdf.format(new Date(0)))) { // Only show if it's the initial load
                            Toast.makeText(this, "No available appointments found for this doctor.", Toast.LENGTH_LONG).show();
                            prevDayButton.setEnabled(false);
                            nextDayButton.setEnabled(false);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error finding next date. INDEX REQUIRED?", e));
    }

    private void findPreviousAvailableDate() {
        String currentDate = sdf.format(calendar.getTime());
        db.collection("time_intervals")
                .whereEqualTo("doctorId", doctorId)
                .whereLessThan("date", currentDate)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String prevDateStr = queryDocumentSnapshots.getDocuments().get(0).getString("date");
                        try {
                            calendar.setTime(sdf.parse(prevDateStr));
                            updateDate();
                        } catch (ParseException e) {
                            Log.e(TAG, "Date parsing error for previous date", e);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error finding previous date. INDEX REQUIRED?", e));
    }

    private void updateButtonStates(String currentDate) {
        // Check for next date
        db.collection("time_intervals")
                .whereEqualTo("doctorId", doctorId)
                .whereGreaterThan("date", currentDate)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> nextDayButton.setEnabled(!queryDocumentSnapshots.isEmpty()));

        // Check for previous date
        db.collection("time_intervals")
                .whereEqualTo("doctorId", doctorId)
                .whereLessThan("date", currentDate)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> prevDayButton.setEnabled(!queryDocumentSnapshots.isEmpty()));
    }


    private void loadTimeIntervals(String date) {
        db.collection("time_intervals")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("date", date)
                .orderBy("startTime")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<TimeInterval> timeIntervals = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            TimeInterval ti = document.toObject(TimeInterval.class);
                            ti.setTimeIntervalId(document.getId());
                            timeIntervals.add(ti);
                        }

                        Runnable onAppointmentBooked = () -> {
                            Intent intent = new Intent(FourthActivity.this, AppointmentsActivity.class);
                            intent.putExtra("USER_ID_NUMBER", getIntent().getStringExtra("USER_ID_NUMBER"));
                            startActivity(intent);
                            finish();
                        };

                        adapter = new TimeIntervalAdapter(timeIntervals, db, this, userId, onAppointmentBooked);
                        timeIntervalRecyclerView.setAdapter(adapter);

                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(FourthActivity.this, "Error loading schedule. Check Logcat for details.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
