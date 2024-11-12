package com.example.ecoscout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    private EditText editTextFullName, editTextAddress, editTextPhone, editTextEmail;
    private EditText editTextPassword, editTextConfirmPassword;
    private Button buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonSignup = findViewById(R.id.buttonSignup);

        buttonSignup.setOnClickListener(v -> {
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed directly to dashboard for now
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
