package com.example.memeapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private EditText mailField;
    private EditText passwordField;
    private Button logInButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mailField = findViewById(R.id.mail_field);
        passwordField = findViewById(R.id.password_field);
        logInButton = findViewById(R.id.log_in_btn);
        registerButton = findViewById(R.id.register_btn);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                logInButton.setVisibility(View.INVISIBLE);
                registerButton.setVisibility(View.INVISIBLE);

                final String mail = mailField.getText().toString();
                final String password = passwordField.getText().toString();

                if (mail.isEmpty()) {
                    showToast("Mail field is required!");
                    progressBar.setVisibility(View.INVISIBLE);
                    logInButton.setVisibility(View.VISIBLE);
                    registerButton.setVisibility(View.VISIBLE);
                } else if (password.isEmpty()) {
                    showToast("Password field is required!");
                    progressBar.setVisibility(View.INVISIBLE);
                    logInButton.setVisibility(View.VISIBLE);
                    registerButton.setVisibility(View.VISIBLE);
                } else {
                    signIn(mail,password);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            //redirect to home page if user is connected
            updateView();
        }
    }

    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    logInButton.setVisibility(View.VISIBLE);
                    registerButton.setVisibility(View.VISIBLE);
                    updateView();
                } else {
                    showToast("Error logging in!");
                    progressBar.setVisibility(View.INVISIBLE);
                    logInButton.setVisibility(View.VISIBLE);
                    registerButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateView() {
        Intent startHomeActivity = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(startHomeActivity);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}
