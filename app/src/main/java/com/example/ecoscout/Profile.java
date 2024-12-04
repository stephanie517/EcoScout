package com.example.ecoscout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class Profile extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private Button btnUploadPhoto;
    private EditText etName, etEmail;
    private TextView tvLitterReports;
    private Button btnSaveProfile, btnEditProfile;
    private ProfileData profileData;
    private TextView tvTotalPoints, tvEventsJoined;
    private LinearLayout achievementsContainer; // New addition

    // Constants for SharedPreferences
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String KEY_PROFILE_IMAGE_URI = "ProfileImageUri";

    // Save photo URI to SharedPreferences
    private void saveProfileImageUri(String uri) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PROFILE_IMAGE_URI, uri);
        editor.apply();
    }

    // Retrieve photo URI from SharedPreferences
    private String getProfileImageUri() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, null);
    }


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
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
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


        // Upload Photo Button
        btnUploadPhoto.setOnClickListener(v -> openGallery());

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
        // Fetch Litter Reports and Events

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            profileImage.setImageURI(selectedImage);

            // Save image URI (for demonstration purposes)
            ProfileData.getInstance().setProfileImagePath(selectedImage.toString());
        }
    }
    }
