package com.example.ecoscout;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ViewMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private ArrayList<LatLng> litterLocations; // Store the litter locations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the list of litter locations
        litterLocations = new ArrayList<>();

        // Set up the map fragment
        SupportMapFragment mapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mapFragment) // Use the FrameLayout as the container
                .commit();

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadLitterReports();
    }

    private void loadLitterReports() {
        FirebaseUser  currentUser  = FirebaseAuth.getInstance().getCurrentUser ();
        if (currentUser  != null) {
            String userId = currentUser .getUid();
            db.collection("users").document(userId)
                    .collection("litterReports")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LitterReport report = document.toObject(LitterReport.class);
                                LatLng location = new LatLng(report.getLatitude(), report.getLongitude());
                                litterLocations.add(location);
                                mMap.addMarker(new MarkerOptions().position(location).title(report.getLitterType()));
                            }
                            // Move the camera to the first report's location
                            if (!litterLocations.isEmpty()) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(litterLocations.get(0), 12));
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    });
        } else {
            Log.w("ViewMapActivity", "User  not logged in.");
        }
    }
}