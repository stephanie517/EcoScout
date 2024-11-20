package com.example.ecoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {

    private Button litterReportingAndImageRecognitionButton, cleanupEventCoordinationButton, alertButton,
            properDisposalGuidanceButton, rewardsButton, sustainabilityEducationButton, signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // Initialize the buttons
        litterReportingAndImageRecognitionButton = findViewById(R.id.litterReportingAndImageRecognitionButton);
        cleanupEventCoordinationButton = findViewById(R.id.cleanupEventCoordinationButton);
        alertButton = findViewById(R.id.alertButton);
        properDisposalGuidanceButton = findViewById(R.id.properDisposalGuidanceButton);
        rewardsButton = findViewById(R.id.rewardsButton);
        sustainabilityEducationButton = findViewById(R.id.sustainabilityEducationButton);
        signOutButton = findViewById(R.id.signOutButton);

        // Set onClickListeners for each button

        litterReportingAndImageRecognitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Litter Reporting & Image Recognition" button click
                Toast.makeText(Dashboard.this, "Litter Reporting & Image Recognition", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the litter reporting and image recognition activity
                // startActivity(new Intent(Dashboard.this, LitterReportingActivity.class));
            }
        });

        cleanupEventCoordinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Cleanup Event Coordination" button click
                Toast.makeText(Dashboard.this, "Cleanup Event Coordination", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the cleanup event coordination activity
                // startActivity(new Intent(Dashboard.this, CleanupEventCoordinationActivity.class));
            }
        });

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Alert" button click
                Toast.makeText(Dashboard.this, "Alert", Toast.LENGTH_SHORT).show();
                // Example: Navigate to an alert screen
                // startActivity(new Intent(Dashboard.this, AlertActivity.class));
            }
        });

        properDisposalGuidanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Proper Disposal Guidance" button click
                Toast.makeText(Dashboard.this, "Proper Disposal Guidance", Toast.LENGTH_SHORT).show();
                // Example: Navigate to a proper disposal guidance activity
                // startActivity(new Intent(Dashboard.this, ProperDisposalActivity.class));
            }
        });

        rewardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Rewards & Gamification" button click
                Toast.makeText(Dashboard.this, "Rewards & Gamification", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the rewards and gamification activity
                // startActivity(new Intent(Dashboard.this, RewardsActivity.class));
            }
        });

        sustainabilityEducationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Sustainability Education" button click
                Toast.makeText(Dashboard.this, "Sustainability Education", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the sustainability education activity
                // startActivity(new Intent(Dashboard.this, SustainabilityEducationActivity.class));
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Sign Out" button click
                Toast.makeText(Dashboard.this, "Signing Out", Toast.LENGTH_SHORT).show();
                // Example: Sign out logic, and navigate to login screen
                // startActivity(new Intent(Dashboard.this, LoginActivity.class));
                // finish(); // Close the current activity (Dashboard)
            }
        });
    }
}
