package com.example.ecoscout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class LoginPage extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize Google Sign-In options and client
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // You can get this from Firebase console
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // Bind UI elements
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        CardView googleSignInButton = findViewById(R.id.googleSignInButton); // Assuming you have this ID in XML


        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // Set OnClickListener for Google Sign-In button
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    private void loginUser (String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser  firebaseUser  = mAuth.getCurrentUser ();
                        if (firebaseUser  != null) {
                            String userId = firebaseUser .getUid();
                            validateUser (userId, firebaseUser ); // Pass firebaseUser  to validateUser
                        }
                    } else {
                        Toast.makeText(LoginPage.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validateUser (String userId, FirebaseUser  firebaseUser ) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Debug", "Checking userId: " + userId);
                Log.d("Debug", "Database snapshot: " + snapshot.getValue());

                if (snapshot.exists()) {
                    Log.d("Debug", "User  found: " + snapshot.getValue());
                    Toast.makeText(LoginPage.this, "Login successful", Toast.LENGTH_SHORT).show();

                    // Add 2 points to the user's points
                    int currentPoints = snapshot.child("points").getValue(Integer.class) != null ? snapshot.child("points").getValue(Integer.class) : 0;
                    Log.d("Debug", "Current Points: " + currentPoints);
                    int newPoints = currentPoints + 2;

                    // Update the points in the database
                    databaseReference.child(userId).child("points").setValue(newPoints)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Debug", "Points updated successfully to: " + newPoints);
                                } else {
                                    Log.e("Debug", "Failed to update points: " + task.getException().getMessage());
                                }
                            });

                    // Check if name and email are stored, if not, store them
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    if (name == null || email == null) {
                        if (firebaseUser  != null) {
                            name = firebaseUser .getDisplayName();
                            email = firebaseUser .getEmail();
                            Log.d("Debug", "Storing name: " + name + " and email: " + email);
                            databaseReference.child(userId).child("name").setValue(name);
                            databaseReference.child(userId).child("email").setValue(email);
                        }
                    }

                    Intent intent = new Intent(LoginPage.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("Debug", "User  ID not found in database: " + userId);
                    Toast.makeText(LoginPage.this, "User  not registered", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Debug", "Database error: " + error.getMessage());
                Toast.makeText(LoginPage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Trigger Google Sign-In flow
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Handle the result from Google Sign-In
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the request code matches
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("Google Sign-In", "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(this, "Sign-in failed, please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Authenticate with Firebase using the Google account
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userId = user != null ? user.getUid() : null;
                        if (userId != null) {
                            validateUser(userId, user); // Validate user after successful Google login
                        }
                    } else {
                        Log.w("Google Sign-In", "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
