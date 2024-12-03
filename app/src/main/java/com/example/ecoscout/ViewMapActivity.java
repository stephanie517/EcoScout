package com.example.ecoscout;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ViewMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LatLng> litterLocations; // Store the litter locations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        // Initialize the list of litter locations (this can be loaded from a database or storage)
        litterLocations = new ArrayList<>();
        // Example locations - these should be replaced with actual stored data
        litterLocations.add(new LatLng(14.5995, 120.9842));  // Sample Manila coordinates

        // Set up the map fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Loop through the litter locations and add markers to the map
        for (LatLng location : litterLocations) {
            mMap.addMarker(new MarkerOptions().position(location).title("Reported Litter"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12)); // Adjust zoom level
        }
    }
}
