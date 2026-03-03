package com.example.campusapp.ui.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import com.example.campusapp.R;
import com.example.campusapp.data.DatabaseHelper;
import com.example.campusapp.ui.main.MainActivity;

import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        DatabaseHelper db = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();

            boolean valid = db.checkUser(user, pass);


            if (valid) {
                SharedPreferences prefs =
                        getSharedPreferences("campus_prefs", MODE_PRIVATE);
                prefs.edit()
                        .putString("logged_user", user)
                        .apply();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this,
                        "Incorrect username or password",
                        Toast.LENGTH_SHORT).show();
            }

        });


        btnRegister.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this,
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.userExists(user)) {
                Toast.makeText(this,
                        "Username already exists",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = db.registerUser(user, pass);

            if (success) {
                Toast.makeText(this,
                        "Registered successfully",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Registration failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
