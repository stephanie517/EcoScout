package com.example.ecoscout;

import android.content.Intent;
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
import android.widget.Toast; // Import Toast
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

public class LitterReporting extends AppCompatActivity {

    private Button btnTakePhoto, btnUploadPhoto, btnSubmitReport, btnSelectLocation;
    private ImageView imgCapturedPhoto;
    private TextView tvImageAnalysisResult, tvLocation, tvManualInputLabel;
    private EditText etManualLitterType;
    private String litterType = "";
    private Location litterLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_litter);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
        btnSelectLocation = findViewById(R.id.btnSelectLocation); // Ensure this button exists in your layout
        imgCapturedPhoto = findViewById(R.id.imgCapturedPhoto);
        tvImageAnalysisResult = findViewById(R.id.tvImageAnalysisResult);
        tvLocation = findViewById(R.id.tvLocation);
        tvManualInputLabel = findViewById(R.id.tvManualInputLabel);
        etManualLitterType = findViewById(R.id.etManualLitterType);

        // Take Photo Button Click
        btnTakePhoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, 1);
            }
        });

        // Upload Photo Button Click
        btnUploadPhoto.setOnClickListener(v -> {
            Intent uploadPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
            uploadPhotoIntent.setType("image/*");
            startActivityForResult(uploadPhotoIntent, 2);
        });

        // Select Location Button Click
        btnSelectLocation.setOnClickListener(v -> {
            Intent mapIntent = new Intent(LitterReporting.this, MapActivity.class);
            startActivityForResult(mapIntent, 3);
        });

        // Submit Report Button Click
        btnSubmitReport.setOnClickListener(v -> {
            if (litterLocation != null && !litterType.isEmpty()) {
                submitLitterReport(litterLocation, litterType);
            } else {
                Toast.makeText(this, "Please select a location and litter type.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle the image capture result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData(); // Get the URI from the Intent
            imgCapturedPhoto.setImageURI(imageUri);
            imgCapturedPhoto.setVisibility(View.VISIBLE);
            analyzeImage(data);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            imgCapturedPhoto.setImageURI(selectedImageUri);
            imgCapturedPhoto.setVisibility(View.VISIBLE);
            analyzeImage(data);
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            LatLng location = data.getParcelableExtra("location");
            if (location != null) {
                litterLocation = new Location("manual");
                litterLocation.setLatitude(location.latitude);
                litterLocation.setLongitude(location.longitude);
                tvLocation.setText("Location: Lat: " + litterLocation.getLatitude() + ", Lon: " + litterLocation.getLongitude());
                tvLocation.setVisibility(View.VISIBLE);
            }
        }
    }

    // Simulate image recognition analysis
    private void analyzeImage(Intent data) {
        String recognizedLitterType = "Plastic"; // Simulated result
        tvImageAnalysisResult.setText("Analysis Result: " + recognizedLitterType);
        tvImageAnalysisResult.setVisibility(View.VISIBLE);
        tvManualInputLabel.setVisibility(View.VISIBLE);
        etManualLitterType.setVisibility(View.VISIBLE);
        litterType = recognizedLitterType;
    }

    // Submit the litter report
    private void submitLitterReport(Location location, String litterType)
    {
        String report = "Litter Type: " + litterType + "\nLocation: " + location.getLatitude() + ", " + location.getLongitude();
        Log.d("LitterReport", report);
        Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tagLocation(); // Optional: Automatically tag the user's current location
    }

    // Optional: Automatically tag the user's current location
    private void tagLocation() {
        litterLocation = new Location("dummyProvider");
        litterLocation.setLatitude(14.5995);
        litterLocation.setLongitude(120.9842);
        tvLocation.setText("Location: Lat: " + litterLocation.getLatitude() + ", Lon: " + litterLocation.getLongitude());
        tvLocation.setVisibility(View.VISIBLE);
    }
}