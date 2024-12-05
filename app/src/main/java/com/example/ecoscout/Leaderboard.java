package com.example.ecoscout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private List<LeaderboardItem> leaderboardItems;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private TextView tvUserRank, tvUserPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);

        // Initialize Firestore and current user
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        recyclerView = findViewById(R.id.leaderboardRecyclerView);
        tvUserRank = findViewById(R.id.tvUserRank);
        tvUserPoints = findViewById(R.id.tvUserPoints);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        leaderboardItems = new ArrayList<>();
        adapter = new LeaderboardAdapter(leaderboardItems);
        recyclerView.setAdapter(adapter);

        // Fetch and display leaderboard data
        fetchLeaderboardData();
    }

    private void fetchLeaderboardData() {
        db.collection("users")
                .orderBy("totalPoints", Query.Direction.DESCENDING)
                .limit(50)  // Limit to top 50 users
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        leaderboardItems.clear();
                        int userRank = -1;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("fullName");
                            Long totalPoints = document.getLong("totalPoints");
                            int points = totalPoints != null ? totalPoints.intValue() : 0;

                            LeaderboardItem item = new LeaderboardItem(name, points, R.drawable.profile);
                            leaderboardItems.add(item);

                            // Find current user's rank
                            if (currentUser != null && document.getId().equals(currentUser.getUid())) {
                                userRank = leaderboardItems.size();
                            }
                        }

                        // Update UI
                        adapter.notifyDataSetChanged();

                        // Display user's rank and points
                        if (userRank != -1) {
                            tvUserRank.setText("Your Rank: " + userRank);
                            tvUserPoints.setText("Your Points: " + leaderboardItems.get(userRank - 1).getPoints());
                        } else {
                            tvUserRank.setText("Rank: Not Ranked");
                            tvUserPoints.setText("Points: 0");
                        }
                    } else {
                        Log.e("Leaderboard", "Error fetching leaderboard data", task.getException());
                        Toast.makeText(this, "Failed to load leaderboard", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

