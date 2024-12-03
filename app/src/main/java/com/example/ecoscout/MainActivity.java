package com.example.ecoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import pl.droidsonroids.gif.GifImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonSignUp;
    private GifImageView gifImageView; // Declare GifImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Sign Up button and GifImageView
        buttonSignUp = findViewById(R.id.sign_up_button);
        Button logInButton = findViewById(R.id.log_in_button);
        gifImageView = findViewById(R.id.taking_photo_gif);

        // Set click listener for Sign Up button
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignupActivity
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LogInActivity (create if necessary)
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
            }
        });

        // Optionally, control GIF playback (e.g., stop, start, etc.)
        // gifImageView.setVisibility(View.VISIBLE); // Show GIF
        // gifImageView.pause(); // To pause GIF animation if needed
    }
}
