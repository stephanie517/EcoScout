package com.example.ecoscout;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.contract.ActivityResultContracts;
import android.provider.MediaStore;

public class LitterReporting extends AppCompatActivity {

    private Button btnTakePhoto, btnSubmitReport;
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
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
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

        // Submit Report Button Click
        btnSubmitReport.setOnClickListener(v -> {
            if (litterLocation != null && !litterType.isEmpty()) {
                // Submit report to the server or database
                submitLitterReport(litterLocation, litterType);
            }
        });
    }

    // Handle the image capture result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the image from the intent
            imgCapturedPhoto.setImageURI(data.getData());
            imgCapturedPhoto.setVisibility(View.VISIBLE);

            // Simulate Image Analysis
            analyzeImage(data);
        }
    }

    // Simulate image recognition analysis
    private void analyzeImage(Intent data) {
        // This is where you'd integrate your real image recognition code.
        // For now, we assume the recognition works and classifies the litter as 'Plastic'.
        String recognizedLitterType = "Plastic"; // This is a simulated result.
        tvImageAnalysisResult.setText("Analysis Result: " + recognizedLitterType);
        tvImageAnalysisResult.setVisibility(View.VISIBLE);

        // Enable manual input in case of failure in recognition
        tvManualInputLabel.setVisibility(View.VISIBLE);
        etManualLitterType.setVisibility(View.VISIBLE);
        litterType = recognizedLitterType;
    }

    // Handle location tagging (this can be done with location services)
    private void tagLocation() {
        // This is a simple simulation of location tagging.
        litterLocation = new Location("dummyProvider");
        litterLocation.setLatitude(14.5995); // Example latitude
        litterLocation.setLongitude(120.9842); // Example longitude

        // Display the location
        tvLocation.setText("Location: Lat: " + litterLocation.getLatitude() + ", Lon: " + litterLocation.getLongitude());
        tvLocation.setVisibility(View.VISIBLE);
    }

    // Submit the litter report
    private void submitLitterReport(Location location, String litterType) {
        // You would send the report to your backend or Firebase here.
        // For now, we just simulate the submission.
        String report = "Litter Type: " + litterType + "\nLocation: " + location.getLatitude() + ", " + location.getLongitude();
        // Log or process the report
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Automatically tag location when activity starts
        tagLocation();
    }
}
