package com.example.ecoscout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import android.text.InputType;

public class LoginPage extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private CheckBox showPasswordCheckbox, rememberMeCheckbox;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore firestoreDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initialize components
        initializeComponents();

        // Set up login and sign-in listeners
        setupLoginListeners();
    }

    private void initializeComponents() {
        // Initialize UI elements
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        TextView termsConditionsLink = findViewById(R.id.termsConditionsLink);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firestoreDb = FirebaseFirestore.getInstance();

        // Initialize Google Sign-In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // Load saved credentials
        sharedPreferences = getSharedPreferences("User Prefs", MODE_PRIVATE);
        loadSavedCredentials();
    }

    private void setupLoginListeners() {
        // Email/Password Login
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Always show Terms and Conditions before login for email/password
            showTermsAndConditionsDialog(email, password, false);
        });

        // Google Sign-In
        CardView googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(v -> showTermsAndConditionsDialog(null, null, true));

        // Show/Hide Password
        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editTextPassword.setInputType(isChecked
                    ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editTextPassword.setSelection(editTextPassword.getText().length());
        });
    }

    private void showTermsAndConditionsDialog(String email, String password, boolean isGoogleSignIn) {
        // Remove the previous check for agreed terms
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EcoScout Terms and Conditions");

        String termsMessage = "Welcome to EcoScout - Your Sustainable Living Companion\n\n" +
                "By using EcoScout, you agree to the following terms:\n\n" +
                "1. Purpose of EcoScout:\n" +
                "   - EcoScout is a platform dedicated to promoting sustainable living and environmental awareness.\n" +
                "   - Our goal is to help users reduce their carbon footprint and make eco-friendly choices.\n\n" +
                "2. User Responsibilities:\n" +
                "   - Provide accurate and honest information in your profile and activities.\n" +
                "   - Respect the community guidelines and promote positive environmental action.\n" +
                "   - Use the app for its intended purpose of sustainability and environmental education.\n\n" +
                "3. Data and Privacy:\n" +
                "   - We collect data to personalize your sustainability journey and track environmental impact.\n" +
                "   - Your personal information will be kept confidential and used only within the app.\n\n" +
                "4. Points and Rewards:\n" +
                "   - Earn points by completing eco-challenges, logging sustainable actions, and participating in community activities.\n" +
                "   - Points are for motivation and do not have monetary value.\n\n" +
                "5. Content and Contributions:\n" +
                "   - Users are responsible for the content they share.\n" +
                "   - Inappropriate or harmful content is strictly prohibited.\n\n" +
                "6. Modifications:\n" +
                "   - EcoScout reserves the right to modify terms, features, and point systems.\n\n" +
                "By proceeding, you acknowledge that you are at least 13 years old and agree to these terms.";

        builder.setMessage(termsMessage);

        builder.setPositiveButton("I Agree", (dialog, which) -> {
            // Always save agreement
            sharedPreferences.edit().putBoolean("agreedToTerms", true).apply();

            // Proceed based on login type
            if (isGoogleSignIn) {
                signInWithGoogle();
            } else {
                loginUser(email, password);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            validateEmailUser(userId, firebaseUser, password);
                        }
                    } else {
                        Toast.makeText(LoginPage.this,
                                "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validateEmailUser(String userId, FirebaseUser firebaseUser, String password) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Create user profile if not exists
                    createEmailUserProfile(userId, firebaseUser);
                }

                // Add points and navigate to dashboard
                updatePointsAndNavigateForEmail(userId, firebaseUser, password);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginPage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createEmailUserProfile(String userId, FirebaseUser firebaseUser) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.child("name").setValue(firebaseUser.getDisplayName() != null
                ? firebaseUser.getDisplayName()
                : "");
        userRef.child("email").setValue(firebaseUser.getEmail());
        userRef.child("points").setValue(0);
    }

    private void updatePointsAndNavigateForEmail(String userId, FirebaseUser firebaseUser, String password) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.child("points").get().addOnSuccessListener(snapshot -> {
            int currentPoints = snapshot.getValue(Integer.class) != null
                    ? snapshot.getValue(Integer.class)
                    : 0;
            int newPoints = currentPoints + 2;

            // Update points in Realtime Database
            userRef.child("points").setValue(newPoints)
                    .addOnSuccessListener(aVoid -> {
                        // Update points in Firestore
                        firestoreDb.collection("users").document(userId)
                                .update("totalPoints", newPoints);

                        // Save credentials if "Remember Me" is checked
                        if (rememberMeCheckbox.isChecked()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("rememberMe", true);
                            editor.putString("email", firebaseUser.getEmail());
                            editor.putString("password", password);
                            editor.apply();
                        }

                        // Navigate to Dashboard
                        Intent intent = new Intent(LoginPage.this, Dashboard.class);
                        startActivity(intent);
                        finish();
                    });
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e("Google Sign-In", "Google sign in failed", e);
                Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            // Similar points and validation logic as email login
                            validateGoogleUser(userId, user);
                        }
                    } else {
                        Log.w("Google Sign-In", "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validateGoogleUser(String userId, FirebaseUser user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Create user profile if not exists
                    createGoogleUserProfile(userId, user);
                }

                // Add points and navigate to dashboard
                updatePointsAndNavigate(userId, user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginPage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createGoogleUserProfile(String userId, FirebaseUser user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.child("name").setValue(user.getDisplayName());
        userRef.child("email").setValue(user.getEmail());
        userRef.child("points").setValue(0);
    }

    private void updatePointsAndNavigate(String userId, FirebaseUser user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.child("points").get().addOnSuccessListener(snapshot -> {
            int currentPoints = snapshot.getValue(Integer.class) != null
                    ? snapshot.getValue(Integer.class)
                    : 0;
            int newPoints = currentPoints + 2;

            // Update points in Realtime Database
            userRef.child("points").setValue(newPoints)
                    .addOnSuccessListener(aVoid -> {
                        // Update points in Firestore
                        firestoreDb.collection("users").document(userId)
                                .update("totalPoints", newPoints);

                        // Navigate to Dashboard
                        Intent intent = new Intent(LoginPage.this, Dashboard.class);
                        startActivity(intent);
                        finish();
                    });
        });
    }

    private void loadSavedCredentials() {
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            editTextEmail.setText(sharedPreferences.getString("email", ""));
            editTextPassword.setText(sharedPreferences.getString("password", ""));
            rememberMeCheckbox.setChecked(true);
        }
    }
}