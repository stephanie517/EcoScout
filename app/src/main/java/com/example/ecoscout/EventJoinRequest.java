package com.example.ecoscout;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
        EditText optionalMessage = findViewById(R.id.optionalMessage);
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
            String message = optionalMessage.getText().toString().trim();

            // Add points for joining event
            ProfileData profileData = ProfileData.getInstance();
            profileData.addPoints(5);
            profileData.incrementEventsJoined();

            // Save event to Firestore
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("userId", userId);
            eventData.put("eventName", name);
            eventData.put("eventDate", date);
            eventData.put("eventLocation", getString(location));

            db.collection("userEvents")
                    .add(eventData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "You have received 5 points for joining this event!", Toast.LENGTH_LONG).show();

                        // Redirect to main page
                        Intent mainIntent = new Intent(EventJoinRequest.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
                    });
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> finish());
    }
}
