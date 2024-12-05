package com.example.ecoscout;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventJoinRequest extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_join_request);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Bind views
        TextView eventName = findViewById(R.id.joinEventName);
        ImageView eventImage = findViewById(R.id.joinEventImage);
        TextView eventDate = findViewById(R.id.joinEventDate);
        TextView eventLocation = findViewById(R.id.joinEventLocation);
        TextView eventDesc = findViewById(R.id.joinEventDesc);
        Button confirmButton = findViewById(R.id.confirmJoinButton);
        Button cancelButton = findViewById(R.id.cancelJoinButton);

        // Receive data from EventDetailed
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String date = intent.getStringExtra("date");
        int location = intent.getIntExtra("location", R.string.bwcLocation);
        int desc = intent.getIntExtra("desc", R.string.bwcDesc);
        int image = intent.getIntExtra("image", R.drawable.bwc);

        // Populate data
        eventName.setText(name);
        eventDate.setText("Date: " + date);
        eventLocation.setText("Location: " + getString(location));
        eventDesc.setText(getString(desc));
        eventImage.setImageResource(image);

        // Handle Confirm button click
        confirmButton.setOnClickListener(v -> {
            String userId = mAuth.getCurrentUser().getUid();

            // First get current points and events joined from Firestore
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // Get current points
                        Long currentPoints = documentSnapshot.getLong("totalPoints");
                        Long currentEventsJoined = documentSnapshot.getLong("eventsJoined");

                        int points = (currentPoints != null) ? currentPoints.intValue() : 0;
                        int eventsJoined = (currentEventsJoined != null) ? currentEventsJoined.intValue() : 0;

                        // Add 5 points for joining event
                        int newPoints = points + 5;
                        int newEventsJoined = eventsJoined + 1;

                        // Update both Firestore and Realtime Database
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("totalPoints", newPoints);
                        userData.put("eventsJoined", newEventsJoined);

                        // Update Firestore
                        db.collection("users").document(userId)
                                .update(userData)
                                .addOnSuccessListener(aVoid -> {
                                    // Also update Realtime Database
                                    DatabaseReference rtdb = FirebaseDatabase.getInstance().getReference("Users");
                                    rtdb.child(userId).child("points").setValue(newPoints);

                                    // Save event details
                                    Map<String, Object> eventData = new HashMap<>();
                                    eventData.put("userId", userId);
                                    eventData.put("eventName", name);
                                    eventData.put("eventDate", date);
                                    eventData.put("eventLocation", getString(location));

                                    db.collection("userEvents")
                                            .add(eventData)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(EventJoinRequest.this,
                                                        "Event joined successfully! +5 points",
                                                        Toast.LENGTH_LONG).show();

                                                Intent profileIntent = new Intent(EventJoinRequest.this, Dashboard.class);
                                                profileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(profileIntent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(EventJoinRequest.this,
                                                        "Failed to save event",
                                                        Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EventJoinRequest.this,
                                            "Failed to update points",
                                            Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EventJoinRequest.this,
                                "Failed to get current points",
                                Toast.LENGTH_SHORT).show();
                    });
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> finish());
    }
}

