package com.example.ecoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Profile extends AppCompatActivity {

    private ImageView profileImage;
    private EditText etName, etEmail;
    private TextView tvLitterReports;
    private Button btnSaveProfile, btnEditProfile;
    private ProfileData profileData;
    private TextView tvTotalPoints, tvEventsJoined;
    private LinearLayout achievementsContainer; // New addition

    // Firestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        profileImage = findViewById(R.id.profileImage);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        tvLitterReports = findViewById(R.id.tvLitterReports);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        tvTotalPoints = findViewById(R.id.tvTotalPoints);
        tvEventsJoined = findViewById(R.id.tvEventsJoined);
        achievementsContainer = findViewById(R.id.achievementsContainer);

        // Initialize ProfileData using the Singleton instance
        profileData = ProfileData.getInstance();
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
            Toast.makeText(this, "Image upload coming soon!", Toast.LENGTH_SHORT).show();
        });

        achievementsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, Rewards.class));
            }
        });
    }

    // Rest of the methods remain the same as in your original code
    private void loadProfileData() {
        ProfileData profileData = ProfileData.getInstance();
        // Load data from ProfileData
        etName.setText(profileData.getName());
        etEmail.setText(profileData.getEmail());

        // Update points display
        tvTotalPoints.setText("Total Points: " + profileData.getTotalPoints());

        // Update events joined
        tvEventsJoined.setText("Events Joined: " + profileData.getEventsJoined());

        // Fetch and display user's environmental contributions
        getLitterReportCount();
    }

    private void saveProfileData() {
        // Save data to ProfileData
        profileData.setName(etName.getText().toString());
        profileData.setEmail(etEmail.getText().toString());
    }

    private void getLitterReportCount() {
        // Fetch the count of litter reports from Firestore
        db.collection("litterReports")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // You can add additional filtering logic here if needed
                            count++;
                        }
                        tvLitterReports.setText("Litter Reports: " + count);
                    } else {
                        Toast.makeText(this, "Failed to load litter reports", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}