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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LitterReporting extends AppCompatActivity {

    private ImageView btnUploadPhoto;
    private Button btnSubmitReport, btnSelectLocation;
    private ImageView imgCapturedPhoto;
    private TextView tvImageAnalysisResult, tvLocation, tvManualInputLabel;
    private EditText etManualLitterType;
    private String litterType = "";
    private Location litterLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_litter);

        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
        btnSelectLocation = findViewById(R.id.btnSelectLocation);
        imgCapturedPhoto = findViewById(R.id.imgCapturedPhoto);
        tvImageAnalysisResult = findViewById(R.id.tvImageAnalysisResult);
        tvLocation = findViewById(R.id.tvLocation);
        tvManualInputLabel = findViewById(R.id.tvManualInputLabel);
        etManualLitterType = findViewById(R.id.etManualLitterType);

        // Upload Photo ImageView Click
        btnUploadPhoto.setOnClickListener(v -> {
            showUploadOptions();
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

    private void showUploadOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_upload, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button btnTakePhoto = bottomSheetView.findViewById(R.id.btnTakePhoto);
        Button btnUploadFromGallery = bottomSheetView.findViewById(R.id.btnUploadFromGallery);

        btnTakePhoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, 1);
            }
            bottomSheetDialog.dismiss();
        });

        btnUploadFromGallery.setOnClickListener(v -> {
            Intent uploadPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
            uploadPhotoIntent.setType("image/*");
            startActivityForResult(uploadPhotoIntent, 2);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
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

                // Get the address from latitude and longitude
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                    String locationText;
                    if (addresses != null && addresses.size() > 0) {Address address = addresses.get(0);
                        // Create a more readable address string
                        locationText = "";
                        if (address.getThoroughfare() != null) {
                            locationText += address.getThoroughfare() + ", ";
                        }
                        if (address.getLocality() != null) {
                            locationText += address.getLocality() + ", ";
                        }
                        if (address.getAdminArea() != null) {
                            locationText += address.getAdminArea() + ", ";
                        }
                        if (address.getCountryName() != null) {
                            locationText += address.getCountryName();
                        }
                    } else {
                        // Fallback to coordinates if no address found
                        locationText = String.format("Location: %.4f, %.4f", location.latitude, location.longitude);
                    }

                    tvLocation.setText(locationText);
                    tvLocation.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Fallback to coordinates if geocoding fails
                    tvLocation.setText(String.format("Location: %.4f, %.4f", location.latitude, location.longitude));
                    tvLocation.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void analyzeImage(Intent data) {
        String recognizedLitterType = "Plastic"; // Simulated result
        tvImageAnalysisResult.setText("Analysis Result: " + recognizedLitterType);
        tvImageAnalysisResult.setVisibility(View.VISIBLE);
        tvManualInputLabel.setVisibility(View.VISIBLE);
        etManualLitterType.setVisibility(View.VISIBLE);
        litterType = recognizedLitterType;
    }

    private void submitLitterReport(Location location, String litterType) {
        String report = "Litter Type: " + litterType + "\nLocation: " + location.getLatitude() + ", " + location.getLongitude();
        Log.d(" LitterReport", report);
        Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();

        // Navigate to the ReportListActivity
        Intent intent = new Intent(LitterReporting.this, ReportListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tagLocation(); // Optional: Automatically tag the user's current location
    }

    private void tagLocation() {
        litterLocation = new Location("dummyProvider");
        litterLocation.setLatitude(14.5995);
        litterLocation.setLongitude(120.9842);

        // Use Geocoder to get a readable location name
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(14.5995, 120.9842, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String locationText = "";
                if (address.getLocality() != null) {
                    locationText += address.getLocality() + ", ";
                }
                if (address.getAdminArea() != null) {
                    locationText += address.getAdminArea();
                }
                tvLocation.setText(locationText);
            } else {
                // Fallback to coordinates if no address found
                tvLocation.setText(String.format("Location: %.4f, %.4f", litterLocation.getLatitude(), litterLocation.getLongitude()));
            }
            tvLocation.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to coordinates if geocoding fails
            tvLocation.setText(String.format("Location: %.4f, %.4f", litterLocation.getLatitude(), litterLocation.getLongitude()));
            tvLocation.setVisibility(View.VISIBLE);
        }
    }
}