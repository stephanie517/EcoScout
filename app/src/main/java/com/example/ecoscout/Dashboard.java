package com.example.ecoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Dashboard extends AppCompatActivity {

    private CardView cardGeneral, cardLitterReport, cardMap, cardCleanUp, cardLeaderboard, cardResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // Initialize the CardViews
        cardGeneral = findViewById(R.id.cardGeneral);
        cardLitterReport = findViewById(R.id.cardLitterReport);
        cardMap = findViewById(R.id.cardMap);
        cardCleanUp = findViewById(R.id.cardCleanUp);
        cardLeaderboard = findViewById(R.id.cardLeaderboard);
        cardResources = findViewById(R.id.cardResources);

        // Set onClickListeners for each CardView

        cardGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "General" card click
                Toast.makeText(Dashboard.this, "General Information", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the general information activity
                // startActivity(new Intent(Dashboard.this, GeneralActivity.class));
            }
        });

        cardLitterReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Litter Report" card click
                Toast.makeText(Dashboard.this, "Litter Reporting", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the litter reporting activity
                startActivity(new Intent(Dashboard.this, LitterReporting.class));
            }
        });

        cardMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "View Map" card click
                Toast.makeText(Dashboard.this, "View Map", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the map activity
                // startActivity(new Intent(Dashboard.this, MapActivity.class));
            }
        });

        cardCleanUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Cleanup Event Coordination" card click
                Toast.makeText(Dashboard.this, "Cleanup Event Coordination", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the cleanup event coordination activity
                startActivity(new Intent(Dashboard.this, Event.class));
            }
        });

        cardLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Leaderboard" card click
                Toast.makeText(Dashboard.this, "Leaderboard", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the leaderboard activity
                // startActivity(new Intent(Dashboard.this, LeaderboardActivity.class));
            }
        });

        cardResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Resources" card click
                Toast.makeText(Dashboard.this, "Resources", Toast.LENGTH_SHORT).show();
                // Example: Navigate to the resources activity
                startActivity(new Intent(Dashboard.this, ProperDisposal.class));
            }
        });
    }
}
