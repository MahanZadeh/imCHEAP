package com.example.milkyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEditText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailForgotPassword);
        Button resetPasswordButton = findViewById(R.id.resetPassword);

        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword(){
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please provide a valid email");
            emailEditText.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(ForgotPassword.this,
                        "Check your email to reset your password", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            } else {
                Toast.makeText(ForgotPassword.this, "Try again!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}