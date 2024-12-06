package com.example.ecoscout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class Profile extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private Button btnUploadPhoto;
    private EditText etName, etEmail;
    private TextView tvLitterReports;
    private Button btnSaveProfile, btnEditProfile;
    private ProfileData profileData;
    private TextView tvTotalPoints, tvEventsJoined;
    private LinearLayout achievementsContainer, litterReportsContainer;
    private List<LitterReport> userLitterReports;
    private SharedPreferences sharedPreferences;
    private FirebaseUser currentUser;

    // Constants for SharedPreferences
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String KEY_PROFILE_IMAGE_URI = "ProfileImageUri";

    // Firestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
        litterReportsContainer = findViewById(R.id.litterReportsContainer);

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

        LinearLayout eventsContainer = findViewById(R.id.eventsContainer);
        eventsContainer.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, EventsActivity.class);
            startActivity(intent);
        });

        // Litter Reports Container Click Listener
        LinearLayout litterReportsContainer = findViewById(R.id.litterReportsContainer);
        litterReportsContainer.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, LitterReportsActivity.class);
            startActivity(intent);
        });
    }

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

    private void loadProfileData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get total points and litter report count
                        Long totalPoints = documentSnapshot.getLong("totalPoints");
                        Long litterReportCount = documentSnapshot.getLong("litterReportCount");

                        int points = (totalPoints != null) ? totalPoints.intValue() : 0;
                        int reportCount = (litterReportCount != null) ? litterReportCount.intValue() : 0;

                        // Load other profile information
                        Long eventsJoined = documentSnapshot.getLong("eventsJoined");

                        // Update ProfileData and UI
                        ProfileData profileData = ProfileData.getInstance();
                        profileData.setTotalPoints(points);
                        profileData.setEventsJoined(eventsJoined != null ? eventsJoined.intValue() : 0);

                        // Load name and email
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");

                        // Update UI
                        etName.setText(name != null ? name : "No name available");
                        etEmail.setText(email != null ? email : "No email available");
                        tvTotalPoints.setText("Total Points: " + points);
                        tvLitterReports.setText("Litter Reports: " + reportCount);
                        tvEventsJoined.setText("Events Joined: " + profileData.getEventsJoined());

                        // Debugging log
                        Log.d("Profile", "Total Points: " + points + ", Litter Reports: " + reportCount);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
                    Log.e("Profile", "Error loading profile data", e);
                });
    }

    private void saveProfileData() {
        // Save data to ProfileData
        profileData.setName(etName.getText().toString());
        profileData.setEmail(etEmail.getText().toString());

        // Update Firestore with new profile data
        String userId = currentUser.getUid();
        db.collection("users").document(userId)
                .update(
                        "name", etName.getText().toString(),
                        "email", etEmail.getText().toString()
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
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
            saveProfileImageUri(selectedImage.toString());
        }
    }
}