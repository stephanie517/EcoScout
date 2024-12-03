package com.example.ecoscout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Rewards extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewards);

        // Simulate current user points (replace with actual points tracking)
        int currentUserPoints = 750;

        // Create list of badges
        List<Badge> badges = createBadgesList();

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewBadges);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        BadgeAdapter adapter = new BadgeAdapter(badges, currentUserPoints) {
            @Override
            public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {

            }
        };
        recyclerView.setAdapter(adapter);
    }

    private List<Badge> createBadgesList() {
        List<Badge> badges = new ArrayList<>();

        // Eco Beginner Track
        badges.add(new Badge("Seed Sprout", 50, R.drawable.badge_seed_sprout));
        badges.add(new Badge("Sapling", 100, R.drawable.badge_sapling));
        badges.add(new Badge("Young Tree", 200, R.drawable.badge_young_tree));

        // Recycling Track
        badges.add(new Badge("Recycling Rookie", 75, R.drawable.badge_recycling_rookie));
        badges.add(new Badge("Recycling Ranger", 250, R.drawable.badge_recycling_ranger));
        badges.add(new Badge("Recycling Master", 500, R.drawable.badge_recycling_master));

        // Energy Conservation Track
        badges.add(new Badge("Energy Saver", 150, R.drawable.badge_energy_saver));
        badges.add(new Badge("Power Reducer", 350, R.drawable.badge_power_reducer));
        badges.add(new Badge("Green Energy Hero", 700, R.drawable.badge_green_energy_hero));

        // Community Impact Track
        badges.add(new Badge("Local Volunteer", 200, R.drawable.badge_local_volunteer));
        badges.add(new Badge("Community Leader", 500, R.drawable.badge_community_leader));
        badges.add(new Badge("Eco Ambassador", 1000, R.drawable.badge_eco_ambassador));

        return badges;
    }
}