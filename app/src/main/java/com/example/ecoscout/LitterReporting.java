package com.example.ecoscout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class LitterReporting extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int MAP_REQUEST_CODE = 3;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 4;

    private ImageView btnUploadPhoto, imgCapturedPhoto;
    private Button btnSubmitReport, btnSelectLocation;
    private TextView tvLocation;
    private EditText etManualLitterType;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FusedLocationProviderClient fusedLocationClient;

    private Uri imageUri;
    private Location litterLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_litter);

        initializeFirebase();
        initializeViews();
        setupListeners();
        requestLocationPermissions();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initializeViews() {
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
        btnSelectLocation = findViewById(R.id.btnSelectLocation);
        imgCapturedPhoto = findViewById(R.id.imgCapturedPhoto);
        tvLocation = findViewById(R.id.tvLocation);
        etManualLitterType = findViewById(R.id.etManualLitterType);
    }

    private void setupListeners() {
        btnUploadPhoto.setOnClickListener(v -> showUploadOptions());
        btnSelectLocation.setOnClickListener(v -> openMapActivity());
        btnSubmitReport.setOnClickListener(v -> submitLitterReport());
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            updateLocationUI(location);
                        }
                    });
        }
    }

    private void updateLocationUI(Location location) {
        litterLocation = location;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locationText = formatAddress(address);
                tvLocation.setText(locationText);
            } else {
                tvLocation.setText(String.format("Location: %.4f, %.4f",
                        location.getLatitude(), location.getLongitude()));
            }
        } catch (IOException e) {
            Log.e("LocationError", "Error getting address", e);
        }
    }

    private void showUploadOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_upload, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetView.findViewById(R.id.btnTakePhoto).setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.btnUploadFromGallery).setOnClickListener(v -> {
            Intent uploadPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
            uploadPhotoIntent.setType("image/*");
            startActivityForResult(uploadPhotoIntent, GALLERY_REQUEST_CODE);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void openMapActivity() {
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivityForResult(mapIntent, MAP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = (Bitmap) extras.get("data");
                    imgCapturedPhoto.setImageBitmap(photo);
                    imgCapturedPhoto.setVisibility(View.VISIBLE);
                }
                break;

            case GALLERY_REQUEST_CODE:
                imageUri = data.getData();
                imgCapturedPhoto.setImageURI(imageUri);
                imgCapturedPhoto.setVisibility(View.VISIBLE);
                break;

            case MAP_REQUEST_CODE:
                handleMapResult(data);
                break;
        }
    }

    private void handleMapResult(Intent data) {
        LatLng location = data.getParcelableExtra("location");
        if (location != null) {
            litterLocation = new Location("manual");
            litterLocation.setLatitude(location.latitude);
            litterLocation.setLongitude(location.longitude);

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(
                        location.latitude, location.longitude, 1);

                String locationText = addresses != null && !addresses.isEmpty()
                        ? formatAddress(addresses.get(0))
                        : String.format("Location: %.4f, %.4f", location.latitude, location.longitude);

                tvLocation.setText(locationText);
                tvLocation.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.e("LocationError", "Error getting address", e);
            }
        }
    }

    private String formatAddress(Address address) {
        StringBuilder locationText = new StringBuilder();
        String[] addressParts = {
                address.getThoroughfare(),
                address.getLocality(),
                address.getAdminArea(),
                address.getCountryName()
        };

        for (String part : addressParts) {
            if (part != null) {
                locationText.append(part).append(", ");
            }
        }

        return locationText.length() > 0
                ? locationText.substring(0, locationText.length() - 2)
                : "Unknown Location";
    }

    private void submitLitterReport() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show();
            return;
        }

        String litterType = etManualLitterType.getText().toString().trim();
        if (litterLocation == null || litterType.isEmpty()) {
            Toast.makeText(this, "Complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageAndSaveReport(currentUser.getUid(), litterType);
        } else {
            saveReportToFirestore(currentUser.getUid(), litterType, null);
        }
    }

    private void uploadImageAndSaveReport(String userId, String litterType) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("litter_reports/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        imageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                saveReportToFirestore(userId, litterType, uri.toString())
                        )
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                );
    }

    private void saveReportToFirestore(String userId, String litterType, String imageUrl) {
        LitterReport litterReport = new LitterReport(
                userId,
                litterType,
                litterLocation.getLatitude(),
                litterLocation.getLongitude(),
                imageUrl
        );

        db.collection("users").document(userId)
                .collection("litterReports")
                .add(litterReport)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Report submitted", Toast.LENGTH_SHORT).show();
                    updateUserReportCount(userId);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Report submission failed", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateUserReportCount(String userId) {
        db.collection("users").document(userId)
                .collection("litterReports")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int reportCount = querySnapshot.size();
                    db.collection("users").document(userId)
                            .update("litterReportCount", reportCount);
                });
    }

    // Enhanced LitterReport model
    public static class LitterReport {
        private String userId;
        private String litterType;
        private double latitude;
        private double longitude;
        private String imageUrl;
        private long timestamp;

        public LitterReport(String userId, String litterType, double latitude,
                            double longitude, String imageUrl) {
            this.userId = userId;
            this.litterType = litterType;
            this.latitude = latitude;
            this.longitude = longitude;
            this.imageUrl = imageUrl;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and setters
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getLitterType() {
            return litterType;
        }

        public void setLitterType(String litterType) {
            this.litterType = litterType;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}