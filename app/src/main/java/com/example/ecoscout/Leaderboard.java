package com.example.ecoscout;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);

        RecyclerView recyclerView = findViewById(R.id.leaderboardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<LeaderboardItem> leaderboardItems = createSampleLeaderboardData();
        LeaderboardAdapter adapter = new LeaderboardAdapter(leaderboardItems);
        recyclerView.setAdapter(adapter);
    }

    private List<LeaderboardItem> createSampleLeaderboardData() {
        List<LeaderboardItem> items = new ArrayList<>();

        // Top 3 are already shown in the main layout
        items.add(new LeaderboardItem("David Wilson", 25, R.drawable.profile));
        items.add(new LeaderboardItem("Lisa Kim", 22, R.drawable.profile));
        items.add(new LeaderboardItem("Alex Martinez", 20, R.drawable.profile));
        items.add(new LeaderboardItem("Rachel Green", 18, R.drawable.profile));
        items.add(new LeaderboardItem("Tom Johnson", 15, R.drawable.profile));

        return items;
    }
}
