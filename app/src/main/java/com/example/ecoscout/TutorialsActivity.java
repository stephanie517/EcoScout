package com.example.ecoscout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TutorialsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);

        // Video Button Actions
        Button playRecyclablesVideo = findViewById(R.id.playRecyclablesVideo);
        Button playCompostingVideo = findViewById(R.id.playCompostingVideo);
        Button playEWasteVideo = findViewById(R.id.playEWasteVideo);

        // Recyclables Video
        playRecyclablesVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/recyclables-tutorial"));
            startActivity(intent);
        });

        // Composting Video
        playCompostingVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/composting-tutorial"));
            startActivity(intent);
        });

        // E-Waste Video
        playEWasteVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/e-waste-tutorial"));
            startActivity(intent);
        });
    }
}