package com.mainPackage.randevuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mainPackage.randevuapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText idNumberEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView falseLoginTextView;
    private CheckBox termsCheckBox;
    private TextView checkBoxErrorTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ensureInitialData();

        idNumberEditText = findViewById(R.id.number);
        passwordEditText = findViewById(R.id.Passwort1);
        loginButton = findViewById(R.id.Button1);
        falseLoginTextView = findViewById(R.id.FalseLogin);
        termsCheckBox = findViewById(R.id.checkBox);
        checkBoxErrorTextView = findViewById(R.id.chechBoxText);

        loginButton.setOnClickListener(v -> {
            if (!termsCheckBox.isChecked()) {
                checkBoxErrorTextView.setVisibility(View.VISIBLE);
                falseLoginTextView.setVisibility(View.INVISIBLE);
                return;
            }

            String idNumber = idNumberEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (idNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = idNumber + "@randevuapp.com";

            // Admin Login
            if (idNumber.equals("11111111111") && password.equals("admin123")) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                            intent.putExtra("USER_ID_NUMBER", idNumber);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            falseLoginTextView.setVisibility(View.VISIBLE);
                        }
                    });
        });
    }

    private void ensureInitialData() {
        db.collection("doctors").limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Log.d(TAG, "Database is empty. Populating with initial data.");
                addInitialUsers();
                addInitialHospitals();
                addInitialDoctors();
            }
        });
    }

    private void addInitialUsers() {
        String[][] users = {{"12345678901", "password123"}, {"11122233344", "testpassword"}};
        for (String[] userData : users) {
            String idNumber = userData[0];
            String password = userData[1];
            String email = idNumber + "@randevuapp.com";

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userId = task.getResult().getUser().getUid();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("idNumber", idNumber);
                    db.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "User " + idNumber + " created."))
                            .addOnFailureListener(e -> Log.w(TAG, "Error adding user to Firestore", e));
                }
            });
        }
    }

    private void addInitialHospitals() {
        Map<String, Object> hospital1 = new HashMap<>();
        hospital1.put("county", "Ankara");
        hospital1.put("district", "Çankaya");
        hospital1.put("hospitalName", "Ankara Şehir Hastanesi");
        db.collection("hospitals").add(hospital1);

        Map<String, Object> hospital2 = new HashMap<>();
        hospital2.put("county", "Ankara");
        hospital2.put("district", "Keçiören");
        hospital2.put("hospitalName", "Keçiören Eğitim ve Araştırma Hastanesi");
        db.collection("hospitals").add(hospital2);

        // ... Add other hospitals
    }

    private void addInitialDoctors() {
        String[][] doctors = {
                {"Ankara Şehir Hastanesi", "Dr. Ahmet Yılmaz", "Kardiyoloji"},
                {"Ankara Şehir Hastanesi", "Dr. Ayşe Kaya", "Nöroloji"},
                {"Keçiören Eğitim ve Araştırma Hastanesi", "Dr. Mehmet Demir", "Ortopedi"}
                // ... Add other doctors
        };

        for (String[] docData : doctors) {
            Map<String, Object> doctor = new HashMap<>();
            doctor.put("hospitalName", docData[0]);
            doctor.put("doctorName", docData[1]);
            doctor.put("department", docData[2]);

            db.collection("doctors").add(doctor).addOnSuccessListener(documentReference -> {
                Log.d(TAG, "Doctor " + docData[1] + " added. Generating schedule.");
                generateWeeklyScheduleForDoctor(documentReference.getId());
            });
        }
    }

    private void generateWeeklyScheduleForDoctor(String doctorId) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 7; i++) {
            String date = today.plusDays(i).format(formatter);
            giveTimeIntervals(doctorId, date);
        }
    }

    private void giveTimeIntervals(String doctorId, String date) {
        Random random = new Random();
        List<String> userIds = new ArrayList<>();
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) userIds.add(doc.getId());

            // Morning session
            for (int hour = 9; hour < 12; hour++) {
                for (int minute = 0; minute < 60; minute += 5) {
                    createTimeInterval(doctorId, date, hour, minute, random, userIds);
                }
            }
            // Afternoon session
            for (int hour = 13; hour < 16; hour++) {
                for (int minute = 0; minute < 60; minute += 5) {
                    createTimeInterval(doctorId, date, hour, minute, random, userIds);
                }
            }
        });
    }

    private void createTimeInterval(String doctorId, String date, int hour, int minute, Random random, List<String> userIds) {
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        Map<String, Object> timeInterval = new HashMap<>();
        timeInterval.put("doctorId", doctorId);
        timeInterval.put("date", date);
        timeInterval.put("startTime", time);

        boolean isAvailable = random.nextDouble() > 0.2; // 80% chance of being available
        timeInterval.put("available", isAvailable);
        if (!isAvailable && !userIds.isEmpty()) {
            timeInterval.put("bookedByUserId", userIds.get(random.nextInt(userIds.size())));
        }

        db.collection("time_intervals").add(timeInterval);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
