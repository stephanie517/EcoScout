package com.example.ecoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button joinCleanupButton, alertButton, litterReportingButton, rewardsButton, signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // Initialize the buttons
        joinCleanupButton = findViewById(R.id.joinCleanupButton);
        alertButton = findViewById(R.id.alertButton);
        litterReportingButton = findViewById(R.id.litterReportingButton);
        rewardsButton = findViewById(R.id.rewardsButton);
        signOutButton = findViewById(R.id.signOutButton);

        // Set onClickListeners for each button
        joinCleanupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Join Cleanup" button click
                Toast.makeText(MainActivity.this, "Joining Cleanup", Toast.LENGTH_SHORT).show();
                // Example: Navigate to a new activity for joining cleanup events
                // startActivity(new Intent(MainActivity.this, JoinCleanupActivity.class));
            }
        });

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Alert" button click
                Toast.makeText(MainActivity.this, "Alert", Toast.LENGTH_SHORT).show();
                // Example: Navigate to an alert screen
                // startActivity(new Intent(MainActivity.this, AlertActivity.class));
            }
        });

        litterReportingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Litter Reporting" button click
                Toast.makeText(MainActivity.this, "Litter Reporting", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the litter reporting activity
                // startActivity(new Intent(MainActivity.this, LitterReportingActivity.class));
            }
        });

        rewardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Rewards" button click
                Toast.makeText(MainActivity.this, "Rewards", Toast.LENGTH_SHORT).show();
                // Example: Navigate to a rewards screen
                // startActivity(new Intent(MainActivity.this, RewardsActivity.class));
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Sign Out" button click
                Toast.makeText(MainActivity.this, "Signing Out", Toast.LENGTH_SHORT).show();
                // Example: Sign out logic, and navigate to login screen
                // startActivity(new Intent(MainActivity.this, LoginActivity.class));
                // finish(); // Close the current activity (MainActivity)
            }
        });
    }
}
