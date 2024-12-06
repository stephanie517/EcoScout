package com.example.ecoscout;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class EventsActivity extends AppCompatActivity {

    private LinearLayout eventsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        eventsContainer = findViewById(R.id.eventsContainer);

        displayJoinedEvents();
    }

    private void displayJoinedEvents() {
        eventsContainer.removeAllViews(); // Clear any existing views

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("userEvents")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            TextView noEventsText = new TextView(this);
                            noEventsText.setText("No events attended yet");
                            noEventsText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            eventsContainer.addView(noEventsText);
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                View eventView = getLayoutInflater().inflate(R.layout.item_joined_event, eventsContainer, false);

                                TextView tvEventName = eventView.findViewById(R.id.tvEventName);
                                TextView tvEventDate = eventView.findViewById(R.id.tvEventDate);
                                TextView tvEventLocation = eventView.findViewById(R.id.tvEventLocation);

                                tvEventName.setText(document.getString("eventName"));
                                tvEventDate.setText(document.getString("eventDate"));
                                tvEventLocation.setText(document.getString("eventLocation"));

                                eventsContainer.addView(eventView);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Failed to load events: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}