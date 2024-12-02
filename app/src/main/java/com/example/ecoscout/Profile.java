package com.example.ecoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    private ImageView profileImage;
    private EditText etName, etEmail;
    private TextView tvLitterReports, tvEventsAttended;
    private Button btnSaveProfile, btnEditProfile;
    private ProfileData profileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Initialize views
        profileImage = findViewById(R.id.profileImage);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        tvLitterReports = findViewById(R.id.tvLitterReports);
        tvEventsAttended = findViewById(R.id.tvEventsAttended);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        // Initialize ProfileData (in a real app, this would come from a database or shared preferences)
        profileData = new ProfileData();
        loadProfileData();

        // Edit Profile Button
        btnEditProfile.setOnClickListener(v -> {
            etName.setEnabled(true);
            etEmail.setEnabled(true);
            btnSaveProfile.setVisibility(View.VISIBLE);
            btnEditProfile.setVisibility(View.GONE);
        });

        // Save Profile Button
        btnSaveProfile.setOnClickListener(v -> {
            saveProfileData();
            etName.setEnabled(false);
            etEmail.setEnabled(false);
            btnSaveProfile.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
        });

        // Profile Image Click (for future image upload functionality)
        profileImage.setOnClickListener(v -> {
            // TODO: Implement image upload from gallery or camera
            Toast.makeText(this, "Image upload coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProfileData() {
        // In a real app, load from database or SharedPreferences
        etName.setText(profileData.getName());
        etEmail.setText(profileData.getEmail());

        // Fetch and display user's environmental contributions
        int litterReports = getLitterReportCount();
        int eventsAttended = getEventsAttendedCount();

        tvLitterReports.setText("Litter Reports: " + litterReports);
        tvEventsAttended.setText("Events Attended: " + eventsAttended);
    }

    private void saveProfileData() {
        // In a real app, save to database or SharedPreferences
        profileData.setName(etName.getText().toString());
        profileData.setEmail(etEmail.getText().toString());
    }

    // Simulated methods to get report and event counts
    private int getLitterReportCount() {
        // TODO: Replace with actual database query
        return 5; // Example count
    }

    private int getEventsAttendedCount() {
        // TODO: Replace with actual database query
        return 3; // Example count
    }
}