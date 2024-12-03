package com.example.ecoscout;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LitterReporting extends AppCompatActivity {

    private ImageView btnUploadPhoto;
    private Button btnSubmitReport, btnSelectLocation;
    private ImageView imgCapturedPhoto;
    private TextView tvLocation, tvManualInputLabel;
    private EditText etManualLitterType;
    private String litterType = "";
    private Location litterLocation;

    // Firestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_litter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initializing UI elements
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
        btnSelectLocation = findViewById(R.id.btnSelectLocation);
        imgCapturedPhoto = findViewById(R.id.imgCapturedPhoto);
        tvLocation = findViewById(R.id.tvLocation);
        tvManualInputLabel = findViewById(R.id.tvManualInputLabel);
        etManualLitterType = findViewById(R.id.etManualLitterType);

        // Upload Photo ImageView Click
        btnUploadPhoto.setOnClickListener(v -> showUploadOptions());

        // Select Location Button Click
        btnSelectLocation.setOnClickListener(v -> {
            Intent mapIntent = new Intent(LitterReporting.this, MapActivity.class);
            startActivityForResult(mapIntent, 3);
        });

        // Submit Report Button Click
        btnSubmitReport.setOnClickListener(v -> {
            litterType = etManualLitterType.getText().toString().trim();
            if (litterLocation != null && !litterType.isEmpty()) {
                submitLitterReport(litterLocation, litterType);
            } else {
                Toast.makeText(this, "Please select a location and litter type.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show the BottomSheetDialog for photo upload options
    private void showUploadOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_upload, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        ImageView btnTakePhoto = bottomSheetView.findViewById(R.id.btnTakePhoto);
        ImageView btnUploadFromGallery = bottomSheetView.findViewById(R.id.btnUploadFromGallery);

        // Take Photo ImageButton Click
        btnTakePhoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, 1);
            }
            bottomSheetDialog.dismiss();
        });

        // Upload from Gallery ImageButton Click
        btnUploadFromGallery.setOnClickListener(v -> {
            Intent uploadPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
            uploadPhotoIntent.setType("image/*");
            startActivityForResult(uploadPhotoIntent, 2);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    // Handling the result of photo capture or gallery selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imgCapturedPhoto.setImageURI(imageUri);
            imgCapturedPhoto.setVisibility(View.VISIBLE);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            imgCapturedPhoto.setImageURI(selectedImageUri);
            imgCapturedPhoto.setVisibility(View.VISIBLE);
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            // Handling location selection from MapActivity
            LatLng location = data.getParcelableExtra("location");
            if (location != null) {
                litterLocation = new Location("manual");
                litterLocation.setLatitude(location.latitude);
                litterLocation.setLongitude(location.longitude);

                // Get the address from latitude and longitude using Geocoder
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                    String locationText;
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        locationText = formatAddress(address);
                    } else {
                        locationText = String.format("Location: %.4f, %.4f", location.latitude, location.longitude);
                    }

                    tvLocation.setText(locationText);
                    tvLocation.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                    tvLocation.setText(String.format("Location: %.4f, %.4f", location.latitude, location.longitude));
                    tvLocation.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // Formatting the address to display a readable location
    private String formatAddress(Address address) {
        StringBuilder locationText = new StringBuilder();
        if (address.getThoroughfare() != null) {
            locationText.append(address.getThoroughfare()).append(", ");
        }
        if (address.getLocality() != null) {
            locationText.append(address.getLocality()).append(", ");
        }
        if (address.getAdminArea() != null) {
            locationText.append(address.getAdminArea()).append(", ");
        }
        if (address.getCountryName() != null) {
            locationText.append(address.getCountryName());
        }
        return locationText.toString();
    }

    // Submit the litter report
    private void submitLitterReport(Location location, String litterType) {
        String report = "Litter Type: " + litterType + "\nLocation: " + location.getLatitude() + ", " + location.getLongitude();
        Log.d("LitterReport", report);
        Toast .makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();

        // Create a LitterReport object
        LitterReport litterReport = new LitterReport(litterType, location.getLatitude(), location.getLongitude());

        // Add the report to Firestore
        db.collection("litterReports")
                .add(litterReport)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    // Update the profile data with the new litter report count
                    updateProfileWithNewReport();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));
    }

    private void updateProfileWithNewReport() {
        // Fetch the updated count of litter reports and update the profile
        FirebaseFirestore.getInstance().collection("litterReports")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            count++;
                        }
                        // Update the profile data directly if needed
                        // For example, using a static method or a singleton pattern to manage profile data
                        ProfileData.getInstance().setLitterReportCount(count);
                    } else {
                        Toast.makeText(this, "Failed to update profile data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Automatically tag the user's current location on start (can be customized)
    @Override
    protected void onStart() {
        super.onStart();
        tagLocation();
    }

    private void tagLocation() {
        litterLocation = new Location("dummyProvider");
        litterLocation.setLatitude(14.5995); // Sample coordinates (Manila)
        litterLocation.setLongitude(120.9842);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(14.5995, 120.9842, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String locationText = formatAddress(address);
                tvLocation.setText(locationText);
            } else {
                tvLocation.setText(String.format("Location: %.4f, %.4f", litterLocation.getLatitude(), litterLocation.getLongitude()));
            }
            tvLocation.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
            tvLocation.setText(String.format("Location: %.4f, %.4f", litterLocation.getLatitude(), litterLocation.getLongitude()));
            tvLocation.setVisibility(View.VISIBLE);
        }
    }
}