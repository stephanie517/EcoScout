package com.example.ecoscout;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// import com.google.firebase.database.DatabaseReference;
// import com.google.firebase.database.FirebaseDatabase;

public class CleanupEvent extends AppCompatActivity {
    private EditText eventNameEditText, eventDateEditText, eventTimeEditText, eventLocationEditText;
    private Button submitEventButton;
    // private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_planner);

        // Initialize Firebase database reference
        // databaseReference = FirebaseDatabase.getInstance().getReference("events");

        // Initialize UI elements
        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventTimeEditText = findViewById(R.id.eventTimeEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        submitEventButton = findViewById(R.id.submitEventButton);

        // Set submit button click listener
        submitEventButton.setOnClickListener(v -> submitEvent());
    }

    private void submitEvent() {
        String eventName = eventNameEditText.getText().toString().trim();
        String eventDate = eventDateEditText.getText().toString().trim();
        String eventTime = eventTimeEditText.getText().toString().trim();
        String eventLocation = eventLocationEditText.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(eventName)) {
            eventNameEditText.setError("Event name is required");
            return;
        }
        if (TextUtils.isEmpty(eventDate)) {
            eventDateEditText.setError("Date is required");
            return;
        }
        if (TextUtils.isEmpty(eventTime)) {
            eventTimeEditText.setError("Time is required");
            return;
        }
        if (TextUtils.isEmpty(eventLocation)) {
            eventLocationEditText.setError("Location is required");
            return;
        }

        // Generate unique ID for each event
        //String eventId = databaseReference.push().getKey();

        // Create Event object
        //Event event = new Event(eventId, eventName, eventDate, eventTime, eventLocation);

        // Save event to Firebase
        //if (eventId != null) {
        //databaseReference.child(eventId).setValue(event)
        //.addOnSuccessListener(aVoid -> {
        //Toast.makeText(EventPlannerActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
        // Clear input fields after successful creation
        //eventNameEditText.setText("");
        //eventDateEditText.setText("");
        //eventTimeEditText.setText("");
        //eventLocationEditText.setText("");
        //})
        //.addOnFailureListener(e -> {
        //Toast.makeText(EventPlannerActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
        //});
    }
}
//}