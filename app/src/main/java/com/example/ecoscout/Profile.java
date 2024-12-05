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
    ;
    // Firestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

            litterReportsContainer = findViewById(R.id.litterReportsContainer); // Your LinearLayout in profile.xml

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

        displayJoinedEvents();
        // Show Litter Reports
        displayLitterReports();

    }

    // Rest of the methods remain the same as in your original code
    private void loadProfileData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get total points directly
                        Long totalPoints = documentSnapshot.getLong("totalPoints");
                        int points = (totalPoints != null) ? totalPoints.intValue() : 0;

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
                        tvEventsJoined.setText("Events Joined: " + profileData.getEventsJoined());

                        // For debugging
                        Log.d("Profile", "Total Points loaded: " + points);
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

    private void displayJoinedEvents() {
        LinearLayout eventsContainer = findViewById(R.id.eventsContainer);
        eventsContainer.removeAllViews(); // Clear any existing views

        // Fetch events from Firestore (replace with your actual user ID)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("userEvents")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // No events joined
                            TextView noEventsText = new TextView(this);
                            noEventsText.setText("No events attended yet");
                            noEventsText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            eventsContainer.addView(noEventsText);
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Create a view for each event
                                View eventView = getLayoutInflater().inflate(R.layout.item_joined_event, eventsContainer, false);

                                TextView tvEventName = eventView.findViewById(R.id.tvEventName);
                                TextView tvEventDate = eventView.findViewById(R.id.tvEventDate);
                                TextView tvEventLocation = eventView.findViewById(R.id.tvEventLocation);

                                // Set event details from Firestore document
                                tvEventName.setText(document.getString("eventName"));
                                tvEventDate.setText(document.getString("eventDate"));
                                tvEventLocation.setText(document.getString("eventLocation"));

                                eventsContainer.addView(eventView);
                            }
                        }
                    } else {
                        // Handle error
                        Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void displayLitterReports() {
        LinearLayout litterReportsContainer = findViewById(R.id.litterReportsContainer);
        litterReportsContainer.removeAllViews(); // Clear any existing views

        // Fetch litter reports from Firestore based on the current user
        String userId = currentUser.getUid();

        db.collection("litterReports")
                .whereEqualTo("userId", userId) // Assuming "userId" is stored in each litter report
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // No litter reports yet
                            TextView noReportsText = new TextView(this);
                            noReportsText.setText("No litter reports yet");
                            noReportsText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            litterReportsContainer.addView(noReportsText);
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Inflate the layout for each litter report
                                View litterReportView = getLayoutInflater().inflate(R.layout.item_litter_report, litterReportsContainer, false);

                                ImageView ivLitterPhoto = litterReportView.findViewById(R.id.ivLitterPhoto);
                                TextView tvLitterType = litterReportView.findViewById(R.id.tvLitterType);
                                TextView tvLitterLocation = litterReportView.findViewById(R.id.tvLitterLocation);

                                // Get the litter report details from Firestore document
                                String photoUrl = document.getString("photoUrl"); // Assuming the photo URL is stored in this field
                                String litterType = document.getString("litterType");
                                String litterLocation = document.getString("litterLocation");

                                // Set the photo if available
                                if (photoUrl != null && !photoUrl.isEmpty()) {
                                    Glide.with(this).load(photoUrl).into(ivLitterPhoto);
                                }

                                // Set the litter type and location text
                                tvLitterType.setText(litterType);
                                tvLitterLocation.setText(litterLocation);

                                // Add the view to the container
                                litterReportsContainer.addView(litterReportView);
                            }
                        }
                    } else {
                        // Handle error
                        Toast.makeText(this, "Failed to load litter reports", Toast.LENGTH_SHORT).show();
                    }
                });
    }}
