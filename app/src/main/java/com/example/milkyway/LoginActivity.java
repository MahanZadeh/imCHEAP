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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView register;
    private EditText editTextEmail, editTextPassword;
    private Button logIn;
    private ProgressBar progressBar;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        register = findViewById(R.id.register);
        register.setOnClickListener(view -> {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        });

        logIn = findViewById(R.id.loginButton);
        logIn.setOnClickListener(view -> {
            userLogin();
            Intent intent = new Intent(this, LandingPage.class);
            startActivity(intent);
        });

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setOnClickListener(view -> {

        });

        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword.setOnClickListener(view -> {

        });

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(view->{
            startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
        });


    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
////            reload();
//        }
//    }

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

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
//
//                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                        startActivity(intent);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user.isEmailVerified()){
                            //redirect to user profile
                            Intent intent = new Intent(getApplicationContext(), LandingPage.class);
                            startActivity(intent);
                        }else {
                            user.sendEmailVerification();
                            Toast.makeText(getApplicationContext(), "Check your email to verify your account!", Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        Toast.makeText(LoginActivity.this, "Failed To Login, please check your credentials!!", Toast.LENGTH_SHORT).show();
//                        progressBarRegister.setVisibility(View.GONE);
                    }
            }
        });
    }

}
