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
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        leaderboardItems.clear();

                        // Separate variables for top 3 users
                        LeaderboardItem firstPlace = null;
                        LeaderboardItem secondPlace = null;
                        LeaderboardItem thirdPlace = null;

                        int userRank = -1;
                        int index = 0;
                        int currentUserPoints = 0; // To store the current user's total points

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("fullName");
                            Long totalPoints = document.getLong("totalPoints");
                            if (name == null) name = "Anonymous"; // Default name
                            int points = totalPoints != null ? totalPoints.intValue() : 0;

                            LeaderboardItem item = new LeaderboardItem(name, points, R.drawable.profile);

                            // Assign top 3 places
                            if (index == 0) {
                                firstPlace = item;
                            } else if (index == 1) {
                                secondPlace = item;
                            } else if (index == 2) {
                                thirdPlace = item;
                            } else {
                                leaderboardItems.add(item);
                            }

                            // Find current user's rank and points
                            if (currentUser != null && document.getId().equals(currentUser.getUid())) {
                                userRank = index + 1;
                                currentUserPoints = points; // Get the current user's total points
                            }
                            index++;
                        }

                        // Update top 3 UI
                        if (firstPlace != null) {
                            ((TextView) findViewById(R.id.tvFirstPlaceName)).setText(firstPlace.getName());
                            ((TextView) findViewById(R.id.tvFirstPlacePoints)).setText(firstPlace.getPoints() + " points");
                        }
                        if (secondPlace != null) {
                            ((TextView) findViewById(R.id.tvSecondPlaceName)).setText(secondPlace.getName());
                            ((TextView) findViewById(R.id.tvSecondPlacePoints)).setText(secondPlace.getPoints() + " points");
                        }
                        if (thirdPlace != null) {
                            ((TextView) findViewById(R.id.tvThirdPlaceName)).setText(thirdPlace.getName());
                            ((TextView) findViewById(R.id.tvThirdPlacePoints)).setText(thirdPlace.getPoints() + " points");
                        }

                        // Update RecyclerView for other users
                        adapter.notifyDataSetChanged();

                        // Display user's rank and points
                        if (userRank != -1) {
                            tvUserRank.setText("Your Rank: " + userRank);
                            tvUserPoints.setText("Your Points: " + currentUserPoints);
                        } else {
                            tvUserRank.setText("Your Rank: Not Ranked");
                            tvUserPoints.setText("Your Points: 0");
                        }
                    } else {
                        Log.e("Leaderboard", "Error fetching leaderboard data", task.getException());
                        Toast.makeText(this, "Failed to load leaderboard", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
