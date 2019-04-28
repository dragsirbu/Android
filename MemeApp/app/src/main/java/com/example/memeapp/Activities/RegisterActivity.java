package com.example.memeapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.memeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameField;
    EditText mailField;
    EditText passwordField;
    EditText confirmPasswordField;
    Button registerButton;
    ImageView userPhoto;
    ProgressBar progressBar;
    Button logInButton;

    private static final int REQ_CODE = 1;

    FirebaseAuth mAuth;
    Uri profilePhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameField = findViewById(R.id.username_field);
        mailField = findViewById(R.id.mail_field);
        passwordField = findViewById(R.id.password_field);
        confirmPasswordField = findViewById(R.id.confirm_password_field);
        registerButton = findViewById(R.id.register_btn);
        userPhoto = findViewById(R.id.user_image);
        progressBar = findViewById(R.id.progress_bar);
        logInButton = findViewById(R.id.log_in_btn);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        progressBar.setVisibility(View.INVISIBLE);

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    requestPermission();
                } else {
                    openGallery();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameField.getText().toString();
                final String mail = mailField.getText().toString();
                final String password = passwordField.getText().toString();
                final String confirmPassword = confirmPasswordField.getText().toString();

                registerButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                if (username.isEmpty()) {
                    showToast("Username field is required!");
                    registerButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else if (mail.isEmpty()) {
                    showToast("Mail field is required!");
                    registerButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else if (password.isEmpty()) {
                    showToast("Password field is required!");
                    registerButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else if (confirmPassword.isEmpty()) {
                    showToast("Confirm password field is required!");
                    registerButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else if (!password.equals(confirmPassword)) {
                    showToast("Password and Confirm Password must match!");
                    registerButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {

                    //All fields completed
                    //Call to private method to create new user
                    createUserAccount(username,password,mail);
                }

            }
        });
    }

    private void openGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openGalleryIntent.setType("image/*");
        startActivityForResult(openGalleryIntent,REQ_CODE);
    }

    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this,"Please accept for required permission!",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_CODE);
            }
        }
        else {
            openGallery();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((resultCode == RESULT_OK) && (requestCode == REQ_CODE) && (data != null)) {
            profilePhotoUri = data.getData();
            userPhoto.setImageURI(profilePhotoUri);
        } else if ((resultCode == RESULT_OK) && (requestCode == REQ_CODE) && (data == null)) {
            showToast("Please choose a profile picture!");
        }
    }


        //This method creates a new user if the provided email is valid
        private void createUserAccount(final String username, String password, String email) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showToast("Account successfully created!");
                            editUser(username,profilePhotoUri, mAuth.getCurrentUser());
                        }
                        else {
                            showToast("Account creation failed!");
                            registerButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    //Edits user photo and username
    private void editUser(final String username, final Uri profilePhotoUri, final FirebaseUser currentUser) {

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("user_profile_pics");
        final StorageReference picturePath = mStorageRef.child(profilePhotoUri.getLastPathSegment());
        picturePath.putFile(profilePhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                picturePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest editProfile = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .setPhotoUri(profilePhotoUri)
                                .build();

                        currentUser.updateProfile(editProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showToast("Registration complete!");
                                    updateView();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    //show home page
    private void updateView() {
        Intent startHomeActivity = new Intent(getApplicationContext(),HomeDrawerActivity.class);
        startActivity(startHomeActivity);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}
