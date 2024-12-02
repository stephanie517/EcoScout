package com.example.ecoscout;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder> {
    private List<Badge> badges;
    private int currentUserPoints;

    public BadgeAdapter(List<Badge> badges, int currentUserPoints) {
        this.badges = badges;
        this.currentUserPoints = currentUserPoints;
        updateBadgeUnlockStatus();
    }

    private void updateBadgeUnlockStatus() {
        for (Badge badge : badges) {
            badge.setUnlocked(currentUserPoints >= badge.getPointsRequired());
        }
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.badge_item, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    class BadgeViewHolder extends RecyclerView.ViewHolder {
        ImageView badgeIcon;
        TextView badgeName, badgePoints, badgeStatus;

        BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeIcon = itemView.findViewById(R.id.badgeIcon);
            badgeName = itemView.findViewById(R.id.badgeName);
            badgePoints = itemView.findViewById(R.id.badgePoints);
            badgeStatus = itemView.findViewById(R.id.badgeStatus);
        }

        void bind(Badge badge) {
            badgeIcon.setImageResource(badge.getBadgeIcon());
            badgeName.setText(badge.getName());
            badgePoints.setText(badge.getPointsRequired() + " Points");

            if (badge.isUnlocked()) {
                badgeStatus.setText("Unlocked");
                badgeStatus.setTextColor(Color.GREEN);
                itemView.setAlpha(1.0f);
            } else {
                badgeStatus.setText("Locked");
                badgeStatus.setTextColor(Color.RED);
                itemView.setAlpha(0.5f);
            }
        }
    }
}