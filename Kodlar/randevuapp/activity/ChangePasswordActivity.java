package com.mainPackage.randevuapp.activity;

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
import com.mainPackage.randevuapp.Model.User;
import com.mainPackage.randevuapp.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText idNumberEditText, oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button submitNewPasswordButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        idNumberEditText = findViewById(R.id.idNumberEditText);
        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        submitNewPasswordButton = findViewById(R.id.submitNewPasswordButton);

        submitNewPasswordButton.setOnClickListener(v -> {
            String idNumber = idNumberEditText.getText().toString().trim();
            String oldPassword = oldPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (idNumber.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = dbHelper.getUser(idNumber);

            if (user == null || !user.getPassword().equals(oldPassword)) {
                Toast.makeText(this, "Invalid ID number or old password.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "New passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.updateUserPassword(idNumber, newPassword)) {
                Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(this, "Failed to update password.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
