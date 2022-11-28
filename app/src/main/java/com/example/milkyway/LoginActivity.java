package com.example.milkyway;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        TextView register = findViewById(R.id.register);
        register.setOnClickListener(view -> {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        });

        Button logIn = findViewById(R.id.loginButton);
        logIn.setOnClickListener(view -> userLogin());

        editTextEmail = findViewById(R.id.editTextEmail);

        editTextPassword = findViewById(R.id.editTextPassword);

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(view->
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class)));
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters long!");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            //redirect to user profile
                            progressBar.setVisibility(View.INVISIBLE); //added here so that when they go back to login page, the progress bar no longer spins indefinitely
                            Intent intent = new Intent(getApplicationContext(), LandingPage.class);
                            startActivity(intent);
                        } else {
                            user.sendEmailVerification();
                            Toast.makeText(getApplicationContext(),
                                    "Check your email to verify your account!",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Failed to retrieve user data!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Failed To Login, please check your credentials!!",
                            Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                }
        });
    }



}
