package com.mainPackage.randevuapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.mainPackage.randevuapp.R;

public class RemoveActivity extends AppCompatActivity {

    private EditText removeHospitalName;
    private Button removeHospitalButton;
    private EditText removeDoctorName;
    private Button removeDoctorButton;
    private Button backToAddPageButton;
    private Button clearDataButton;

    private FirebaseFirestore db;
    private static final String TAG = "RemoveActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        removeHospitalName = findViewById(R.id.removeHospitalName);
        removeHospitalButton = findViewById(R.id.removeHospitalButton);
        removeDoctorName = findViewById(R.id.removeDoctorName);
        removeDoctorButton = findViewById(R.id.removeDoctorButton);
        backToAddPageButton = findViewById(R.id.backToAddPageButton);
        clearDataButton = findViewById(R.id.clearDataButton);

        removeHospitalButton.setOnClickListener(v -> removeHospital());
        removeDoctorButton.setOnClickListener(v -> removeDoctor());

        backToAddPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(RemoveActivity.this, AdminActivity.class);
            startActivity(intent);
        });

        clearDataButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete All Data")
                    .setMessage("Are you sure you want to permanently delete all hospitals, doctors, and schedules? This cannot be undone.")
                    .setPositiveButton("Yes, Delete Everything", (dialog, which) -> clearAllCollections())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void clearAllCollections() {
        Toast.makeText(this, "Deleting all data...", Toast.LENGTH_SHORT).show();

        Task<Void> deleteHospitalsTask = deleteCollection("hospitals");
        Task<Void> deleteDoctorsTask = deleteCollection("doctors");
        Task<Void> deleteTimeIntervalsTask = deleteCollection("time_intervals");

        Tasks.whenAll(deleteHospitalsTask, deleteDoctorsTask, deleteTimeIntervalsTask).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "All app data has been cleared.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RemoveActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to clear all data.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error clearing all collections", task.getException());
            }
        });
    }

    private Task<Void> deleteCollection(String collectionName) {
        return db.collection(collectionName).get().continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot document : task.getResult()) {
                batch.delete(document.getReference());
            }
            return batch.commit();
        });
    }

    private void removeHospital() {
        String name = removeHospitalName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a hospital name to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("hospitals").whereEqualTo("hospitalName", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        WriteBatch batch = db.batch();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            batch.delete(document.getReference());
                        }
                        batch.commit().addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Hospital(s) removed successfully", Toast.LENGTH_SHORT).show();
                            removeHospitalName.setText("");
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Error during hospital removal.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error committing batch delete", e);
                        });
                    } else if (task.isSuccessful()){
                        Toast.makeText(this, "No hospital found with that name.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error finding hospital.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void removeDoctor() {
        String name = removeDoctorName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a doctor name to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("doctors").whereEqualTo("doctorName", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot doctorDoc = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        String doctorId = doctorDoc.getId();

                        db.collection("time_intervals").whereEqualTo("doctorId", doctorId)
                                .get()
                                .addOnCompleteListener(tiTask -> {
                                    if(tiTask.isSuccessful()){
                                        WriteBatch batch = db.batch();
                                        for(QueryDocumentSnapshot tiDoc : tiTask.getResult()){
                                            batch.delete(tiDoc.getReference());
                                        }
                                        batch.delete(doctorDoc.getReference());

                                        batch.commit().addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Doctor and schedule removed successfully", Toast.LENGTH_SHORT).show();
                                            removeDoctorName.setText("");
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error during removal.", Toast.LENGTH_SHORT).show();
                                            Log.w(TAG, "Error committing batch delete for doctor", e);
                                        });
                                    } else {
                                        Toast.makeText(this, "Error finding doctor's schedule.", Toast.LENGTH_SHORT).show();
                                        Log.w(TAG, "Error getting time intervals: ", tiTask.getException());
                                    }
                                });

                    } else if (task.isSuccessful()){
                        Toast.makeText(this, "No doctor found with that name.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error finding doctor.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}
