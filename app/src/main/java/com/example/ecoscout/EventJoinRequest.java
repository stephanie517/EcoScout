package com.example.ecoscout;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EventJoinRequest extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_join_request);

        db = FirebaseFirestore.getInstance();

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
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ProfileData profileData = ProfileData.getInstance();

            // First get current points from Firestore
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // Get current points and log them
                        Long currentPoints = documentSnapshot.getLong("totalPoints");
                        int points = (currentPoints != null) ? currentPoints.intValue() : 0;
                        Log.d("EventPoints", "Current points before adding: " + points);

                        // Add 5 points for joining event
                        int newPoints = points + 5;
                        Log.d("EventPoints", "New points after adding 5: " + newPoints);

                        // Update Firestore with new total
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("totalPoints", newPoints);
                        userData.put("eventsJoined", profileData.getEventsJoined() + 1);

                        db.collection("users").document(userId)
                                .update(userData)  // Changed from set() to update()
                                .addOnSuccessListener(aVoid -> {
                                    // Log successful update
                                    Log.d("EventPoints", "Successfully updated points in Firestore to: " + newPoints);

                                    // Update local ProfileData
                                    profileData.setTotalPoints(newPoints);
                                    profileData.setEventsJoined(profileData.getEventsJoined() + 1);

                                    Toast.makeText(this, "You have received 5 points for joining this event!", Toast.LENGTH_LONG).show();

                                    // Save event details
                                    Map<String, Object> eventData = new HashMap<>();
                                    eventData.put("userId", userId);
                                    eventData.put("eventName", name);
                                    eventData.put("eventDate", date);
                                    eventData.put("eventLocation", getString(location));

                                    db.collection("userEvents")
                                            .add(eventData)
                                            .addOnSuccessListener(documentReference -> {
                                                Log.d("EventPoints", "Event saved successfully");
                                                // Redirect to main page
                                                Intent mainIntent = new Intent(EventJoinRequest.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("EventPoints", "Failed to save event", e);
                                                Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("EventPoints", "Failed to update points", e);
                                    Toast.makeText(this, "Failed to update points", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("EventPoints", "Failed to get current points", e);
                        Toast.makeText(this, "Failed to get current points", Toast.LENGTH_SHORT).show();
                    });
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> finish());
    }
}
