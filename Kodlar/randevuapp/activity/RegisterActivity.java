package com.mainPackage.randevuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText registerIdNumber;
    private EditText registerPassword;
    private Button registerButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        registerIdNumber = findViewById(R.id.registerIdNumber);
        registerPassword = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String idNumber = registerIdNumber.getText().toString().trim();
            String password = registerPassword.getText().toString().trim();

            if (idNumber.length() != 11) {
                Toast.makeText(this, "Kimlik numarası 11 haneli olmalıdır.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Lütfen bir şifre giriniz.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.getUser(idNumber) != null) {
                Toast.makeText(this, "Bu kimlik numarası zaten kayıtlı.", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.addUser(new User(idNumber, password));
            Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
    public void Back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void moveToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}