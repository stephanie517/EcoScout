package com.example.ecoscout;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.example.ecoscout.databinding.EventDetailedBinding;

public class EventDetailed extends AppCompatActivity {

    EventDetailedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EventDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Populate the event details
        Intent intent = this.getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String date = intent.getStringExtra("date");
            int location = intent.getIntExtra("location", R.string.bwcLocation);
            int desc = intent.getIntExtra("desc", R.string.bwcDesc);
            int image = intent.getIntExtra("image", R.drawable.bwc);

            binding.detailName.setText(name);
            binding.detailDate.setText(date);
            binding.detailDesc.setText(desc);
            binding.detailLocation.setText(location);
            binding.detailImage.setImageResource(image);
        }

        binding.joinNowButton.setOnClickListener(v -> {
            Intent joinIntent = new Intent(EventDetailed.this, EventJoinRequest.class);

            joinIntent.putExtra("name", binding.detailName.getText().toString());
            joinIntent.putExtra("date", binding.detailDate.getText().toString());
            joinIntent.putExtra("location", intent.getIntExtra("location", R.string.bwcLocation));
            joinIntent.putExtra("desc", intent.getIntExtra("desc", R.string.bwcDesc));
            joinIntent.putExtra("image", intent.getIntExtra("image", R.drawable.bwc));

            startActivity(joinIntent);
        });

    }
}
