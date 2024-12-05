package com.example.ecoscout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private List<LeaderboardItem> leaderboardItems;

    public LeaderboardAdapter(List<LeaderboardItem> leaderboardItems) {
        this.leaderboardItems = leaderboardItems;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardItem item = leaderboardItems.get(position);
        holder.participantName.setText(item.getName());
        holder.participantPoints.setText(item.getPoints() + " pts");
        holder.participantImage.setImageResource(item.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return leaderboardItems.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView participantName;
        TextView participantPoints;
        CircleImageView participantImage;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            participantName = itemView.findViewById(R.id.participantName);
            participantPoints = itemView.findViewById(R.id.participantPoints);
            participantImage = itemView.findViewById(R.id.participantImage);
        }
    }
}