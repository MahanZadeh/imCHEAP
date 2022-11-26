package com.example.milkyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText name, email, password;
    private TextView banner;
    private Button registerButton;
    private ProgressBar progressBarRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> {
            registerUser();

        });

        name = findViewById(R.id.editTextTextPersonName2);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        progressBarRegister = findViewById(R.id.progressBarRegister);

    }

    private void registerUser(){
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userName = name.getText().toString().trim();

        if(userName.isEmpty()){
            name.setError("Full Name Is Required!");
            name.requestFocus();
            return;
        }
        if(userEmail.isEmpty()){
            email.setError("Email Is Required!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            email.setError("Please Provide Valid Email!");
            email.requestFocus();
            return;
        }

        if(userPassword.isEmpty()){
            password.setError("Password Is Required!");
            password.requestFocus();
            return;
        }

        if(userPassword.length() < 6){
            password.setError("Password must be at least 6 characters long!");
            password.requestFocus();
            return;
        }


        progressBarRegister.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(userName, userEmail);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Register.this, "User Has Been Registered Successfully!", Toast.LENGTH_SHORT).show();
                                        progressBarRegister.setVisibility(View.GONE);
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);

                                        //redirect to login layout!!!!!
                                    }else {
                                        Toast.makeText(Register.this, "Failed To Register, Try Again!", Toast.LENGTH_SHORT).show();
                                        progressBarRegister.setVisibility(View.GONE);
                                    }
                                }
                            });

                } else {
                    Toast.makeText(Register.this, "Failed To Register!", Toast.LENGTH_SHORT).show();
                    progressBarRegister.setVisibility(View.GONE);
                }
            }
        });


    }

}