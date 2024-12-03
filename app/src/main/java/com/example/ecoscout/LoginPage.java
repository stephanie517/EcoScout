package com.example.ecoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        // Set up the login button click listener
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // You can add authentication logic here (e.g., check credentials)
                // For now, proceed directly to the Dashboard
                Toast.makeText(LoginPage.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginPage.this, Dashboard.class);
                startActivity(intent);
                finish(); // Close the login page so the user cannot go back
            }
        });
    }

    public void Finish(View view) {
        finish();
    }
}
