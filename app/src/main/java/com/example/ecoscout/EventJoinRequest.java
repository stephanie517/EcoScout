package com.example.ecoscout;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EventJoinRequest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_join_request);

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

            // Simulate sending join request
            Toast.makeText(this, "Joining Event...\nMessage: " + (message.isEmpty() ? "No message provided" : message), Toast.LENGTH_LONG).show();

            // Display points message
            Toast.makeText(this, "You have received 5 points for joining this event!", Toast.LENGTH_LONG).show();

            // TODO: Save to database or API call to send the join request

            // Redirect to main page
            Intent mainIntent = new Intent(EventJoinRequest.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> finish());
    }
}
