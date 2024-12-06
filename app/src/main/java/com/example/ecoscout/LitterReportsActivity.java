package com.example.ecoscout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LitterReportsActivity extends AppCompatActivity {

    private LinearLayout litterReportsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_litter_reports);

        litterReportsContainer = findViewById(R.id.litterReportsContainer);

        displayLitterReports();
    }

    private void displayLitterReports() {
        litterReportsContainer.removeAllViews(); // Clear any existing views

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(userId)
                .collection("litterReports")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            TextView noReportsText = new TextView(this);
                            noReportsText.setText("No litter reports yet");
                            noReportsText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            litterReportsContainer.addView(noReportsText);
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                View reportView = getLayoutInflater().inflate(R.layout.item_litter_report, litterReportsContainer, false);

                                ImageView ivLitterPhoto = reportView.findViewById(R.id.ivLitterPhoto);
                                TextView tvLitterType = reportView.findViewById(R.id.tvLitterType);
                                TextView tvLitterLocation = reportView.findViewById(R.id.tvLitterLocation);

                                String photoUrl = document.getString("imageUrl");
                                String litterType = document.getString("litterType");
                                Double latitude = document.getDouble("latitude");
                                Double longitude = document.getDouble("longitude");
                                int points = document.getLong("points") != null
                                        ? document.getLong("points").intValue()
                                        : 0;

                                if (photoUrl != null && !photoUrl.isEmpty()) {
                                    Glide.with(this).load(photoUrl).into(ivLitterPhoto);
                                }

                                tvLitterType.setText("Type: " + litterType + " (Points: " + points + ")");
                                tvLitterLocation.setText(String.format("Location: %.4f, %.4f", latitude, longitude));

                                litterReportsContainer.addView(reportView);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Failed to load litter reports: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}