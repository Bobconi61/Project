package com.mainPackage.randevuapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mainPackage.randevuapp.Model.Appointment;
import com.mainPackage.randevuapp.R;
import com.mainPackage.randevuapp.adapter.AppointmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter adapter;
    private FirebaseFirestore db;
    private String userId;
    private static final String TAG = "AppointmentsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAppointments();
    }

    private void loadAppointments() {
        if (userId == null) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("time_intervals")
                .whereEqualTo("bookedByUserId", userId)
                .get()
                .addOnSuccessListener(timeIntervalsSnapshot -> {
                    if (timeIntervalsSnapshot.isEmpty()) {
                        handleNoAppointments();
                        return;
                    }

                    List<Task<DocumentSnapshot>> doctorTasks = new ArrayList<>();
                    List<QueryDocumentSnapshot> timeIntervalDocs = new ArrayList<>();
                    for (QueryDocumentSnapshot tiDoc : timeIntervalsSnapshot) {
                        String doctorId = tiDoc.getString("doctorId");
                        if (doctorId != null && !doctorId.isEmpty()) {
                            timeIntervalDocs.add(tiDoc);
                            doctorTasks.add(db.collection("doctors").document(doctorId).get());
                        }
                    }

                    if (doctorTasks.isEmpty()) {
                        handleNoAppointments();
                        return;
                    }

                    Tasks.whenAllSuccess(doctorTasks).addOnSuccessListener(doctorSnapshots -> {
                        List<Appointment> appointments = new ArrayList<>();
                        for (int i = 0; i < doctorSnapshots.size(); i++) {
                            DocumentSnapshot doctorDoc = (DocumentSnapshot) doctorSnapshots.get(i);
                            QueryDocumentSnapshot tiDoc = timeIntervalDocs.get(i);

                            if (doctorDoc.exists()) {
                                Appointment appointment = new Appointment(
                                        tiDoc.getId(),
                                        doctorDoc.getString("doctorName"),
                                        doctorDoc.getString("department"),
                                        doctorDoc.getString("hospitalName"),
                                        tiDoc.getString("date"),
                                        tiDoc.getString("startTime")
                                );
                                appointments.add(appointment);
                            }
                        }
                        adapter = new AppointmentAdapter(appointments, db, this::loadAppointments);
                        appointmentsRecyclerView.setAdapter(adapter);

                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading doctor details", e);
                        Toast.makeText(AppointmentsActivity.this, "Error loading appointment details.", Toast.LENGTH_SHORT).show();
                    });

                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting time intervals", e);
                    Toast.makeText(AppointmentsActivity.this, "Error loading appointments.", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleNoAppointments() {
        appointmentsRecyclerView.setAdapter(null); // Clear the list
        Toast.makeText(AppointmentsActivity.this, "No appointments found.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
