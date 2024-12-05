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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class LitterReporting extends AppCompatActivity {
    // Request codes
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int MAP_REQUEST_CODE = 3;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 4;

    // UI Components
    private ImageView btnUploadPhoto, imgCapturedPhoto;
    private Button btnSubmitReport, btnSelectLocation, btnSelectLitterType;
    private TextView tvLocation, tvCategorizationResult;

    // Firebase and Location Components
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FusedLocationProviderClient fusedLocationClient;
    private StorageReference storageReference;

    // Litter Reporting Variables
    private Uri imageUri;
    private Location litterLocation;
    private String selectedLitterType;

    // Litter Types Array
    private static final String[] LITTER_TYPES = {
            "Food wrappers", "Plastic bottles", "Shopping bags", "Newspapers", "Cardboard boxes",
            "Receipts", "Broken glass bottles", "Jars", "Soda cans", "Tin foil",
            "Bottle caps", "Food scraps", "Leaves and twigs", "Cigarette butts", "Chewing gum",
            "Styrofoam containers", "Single-use cutlery", "Straws and stirrers", "Torn clothing",
            "Takeout containers", "Disposable coffee cups", "Plastic bottle caps", "Balloons",
            "Vehicle tires", "Snack wrappers", "Pens and markers", "Old keys or locks",
            "Plastic utensils", "Paper napkins", "Tissues", "Milk cartons", "Candy wrappers",
            "Construction debris", "Rubber bands", "Plastic zip ties", "Broken ceramics",
            "Rubber gloves", "Masks (face masks)", "Fishing lines", "Foam packing material",
            "Disposable razors", "Wet wipes", "Plastic rings from six-packs", "Broken toys",
            "Garden waste", "Fruit peels", "Old shoes", "Deflated balls", "Metal wires",
            "Used napkins", "Tangled cables", "Discarded books", "Plastic beads",
            "Electronic parts", "Shattered mirrors", "Pet waste bags", "Soap wrappers",
            "Plastic food packaging", "Ice cream sticks", "Old magazines", "Torn umbrellas",
            "Disposable chopsticks", "Snack boxes", "Earbuds or headphones", "Broken glassware",
            "Old furniture pieces", "Empty toothpaste tubes", "Discarded batteries", "Plastic trays",
            "Packing peanuts", "Plastic wrap", "Damaged handbags", "Old belts",
            "Bottle seals", "Aluminum trays", "Coffee pod capsules", "Damaged power cords",
            "Ripped tarps", "Faded posters", "Kite strings", "Disposable thermometers",
            "Popcorn bags", "Plastic egg cartons", "Disused paper plates"
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_litter);

        initializeFirebase();
        initializeViews();
        setupListeners();
        requestLocationPermissions();

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        // Request camera permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_litter_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_profile) {
            startActivity(new Intent(this, Profile.class));
            return true;
        } else if (itemId == R.id.action_view_map) {
            startActivity(new Intent(this, ViewMapActivity.class));
            return true;
        } else if (itemId == R.id.action_cleanup_events) {
            startActivity(new Intent(this, Event.class));
            return true;
        } else if (itemId == R.id.action_leaderboard) {
            startActivity(new Intent(this, Leaderboard.class));
            return true;
        } else if (itemId == R.id.action_resources) {
            startActivity(new Intent(this, TutorialsActivity.class));
            return true;
        } else if (itemId == R.id.action_sign_out) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void initializeViews() {
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
        btnSelectLocation = findViewById(R.id.btnSelectLocation);
        btnSelectLitterType = findViewById(R.id.btnSelectLitterType);
        imgCapturedPhoto = findViewById(R.id.imgCapturedPhoto);
        tvLocation = findViewById(R.id.tvLocation);
        tvCategorizationResult = findViewById(R.id.tvCategorizationResult);
    }

    private void setupListeners() {
        btnUploadPhoto.setOnClickListener(v -> showUploadOptions());
        btnSelectLocation.setOnClickListener(v -> openMapActivity());
        btnSelectLitterType.setOnClickListener(v -> showLitterTypeDialog());
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
            if (part != null) locationText.append(part).append(", ");
        }
        return locationText.length() > 0
                ? locationText.substring(0, locationText.length() - 2)
                : "Unknown Location";
    }

    private void showLitterTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Litter Type");

        builder.setItems(LITTER_TYPES, (dialog, which) -> {
            selectedLitterType = LITTER_TYPES[which];
            Log.d("LitterReporting", "Selected Litter Type: " + selectedLitterType);

            int points = categorizeLitterAndGetPoints(selectedLitterType);
            Log.d("LitterReporting", "Points: " + points);

            if (points == 20) {
                tvCategorizationResult.setText("Selected litter type is hazardous.");
            } else {
                tvCategorizationResult.setText("Selected litter type is standard.");
            }
            tvCategorizationResult.setVisibility(View.VISIBLE);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitLitterReport() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (litterLocation == null || selectedLitterType == null || selectedLitterType.isEmpty()) {
            Toast.makeText(this, "Complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int points = categorizeLitterAndGetPoints(selectedLitterType);

        String imagePath = null;
        if (imageUri != null) {
            imagePath = saveImageLocally(imageUri);
            if (imagePath == null) {
                Toast.makeText(this, "Failed to save image locally", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Save the report to Firestore
        saveReportToFirestore(currentUser.getUid(), selectedLitterType, imagePath, points);
    }

    private String saveImageLocally(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // Create a directory in internal storage
            File directory = new File(getFilesDir(), "litter_images");
            if (!directory.exists() && !directory.mkdirs()) {
                Log.e("LocalStorage", "Failed to create directory");
                return null;
            }

            // Create a file with a unique name
            String fileName = UUID.randomUUID().toString() + ".jpg";
            File imageFile = new File(directory, fileName);

            // Save the bitmap to the file
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }

            Log.d("LocalStorage", "Image saved locally: " + imageFile.getAbsolutePath());
            return imageFile.getAbsolutePath(); // Return the file path
        } catch (IOException e) {
            Log.e("LocalStorageError", "Failed to save image", e);
            return null;
        }
    }

    private void saveReportToFirestore(String userId, String litterType, String imagePath, int points) {
        LitterReport litterReport = new LitterReport(
                userId,
                litterType,
                litterLocation.getLatitude(),
                litterLocation.getLongitude(),
                imagePath, // Save the local file path
                points
        );

        db.collection("users").document(userId)
                .collection("litterReports")
                .add(litterReport)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Report submitted", Toast.LENGTH_SHORT).show();
                    updateUserReportCount(userId);

                    // Update total points
                    db.collection("users").document(userId)
                            .update("totalPoints", FieldValue.increment(points))
                            .addOnSuccessListener(aVoid ->
                                    Log.d("LitterReporting", "Total points updated")
                            )
                            .addOnFailureListener(e ->
                                    Log.e("LitterReporting", "Failed to update total points", e)
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Report submission failed", Toast.LENGTH_SHORT).show()
                );
    }

    private int categorizeLitterAndGetPoints(String litterType) {
        String[] hazardousWaste = {
                "Pesticides", "Paints and solvents", "Cleaning agents", "Batteries", "Old computers",
                "Fluorescent light bulbs", "Used syringes", "Expired medications", "Contaminated dressings",
                "Motor oil", "Gasoline", "Aerosol cans", "Fertilizers", "Glue with toxic substances",
                "Asbestos", "PCB-containing materials", "Radioactive materials", "Fire extinguishers",
                "Pool chemicals", "Refrigerants", "Compressed gas cylinders", "Mercury thermometers",
                "Car batteries", "Brake fluids", "Old mobile phones", "Printer ink cartridges",
                "Chemical lab waste", "Photographic chemicals", "Antifreeze", "Herbicides",
                "Insecticides", "Industrial adhesives", "Lead-based paints", "Toxic cleaning wipes",
                "Sharp blades", "Acids (e.g., sulfuric acid)", "Alkalis (e.g., lye)", "Used motor oils",
                "Medical sharps", "Lab reagents", "Dry cleaning chemicals", "Fuel additives",
                "Ink and toner cartridges", "E-waste (e.g., cables, old routers)", "Light ballasts",
                "Transmission fluids", "Used brake pads", "Gas cylinders (propane or butane)",
                "Coolant fluids", "Refrigerant oils", "Explosives (e.g., fireworks)", "Bleach products",
                "Corrosive cleaners", "Contaminated oil rags", "Discarded x-ray plates", "Heavy metals",
                "Old smoke detectors", "Medical diagnostic devices", "Cement residues", "PCB oils",
                "Old pesticide containers", "Lawn treatment chemicals", "Expired cleaning sprays",
                "Wood varnishes", "Sanding dust with toxins", "Fumigation chemicals", "Lead pipes"
        };


        for (String hazardous : hazardousWaste) {
            if (litterType.equals(hazardous)) {
                return 20; // Points for hazardous waste
            }
        }
        return 10; // Points for standard litter
    }

    private void uploadImageAndSaveReport(String userId, String litterType, int points) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("litter_reports/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        imageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                        saveReportToFirestore(userId, litterType, uri.toString(), points)
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                                ));
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

    public static class LitterReport {
        private String userId;
        private String litterType;
        private double latitude;
        private double longitude;
        private String imageUrl;
        private long timestamp;
        private int points;

        public LitterReport(String userId, String litterType, double latitude,
                            double longitude, String imageUrl, int points) {
            this.userId = userId;
            this.litterType = litterType;
            this.latitude = latitude;
            this.longitude = longitude;
            this.imageUrl = imageUrl;
            this.timestamp = System.currentTimeMillis();
            this.points = points;
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

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }
}