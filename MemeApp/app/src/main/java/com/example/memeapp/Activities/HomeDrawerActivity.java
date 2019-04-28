package com.example.memeapp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.memeapp.Fragments.HomeFragment;
import com.example.memeapp.Fragments.SettingsFragment;
import com.example.memeapp.Fragments.UserProfileFragment;
import com.example.memeapp.Model.Meme;
import com.example.memeapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class HomeDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQ_CODE = 5115;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog addMemePopup;
    ImageView addMemeUserPhoto;
    ImageView addMemeMeme;
    ImageView addMemePlus;
    EditText addMemeTitle;
    ProgressBar addMemeProgressBar;

    Uri pickedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeAddMemePopup();
        setAddMemeClick();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemePopup.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //call method to set username, mail and photo in app drawer header
        updateNavigationBarUserInfo();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAddMemeClick() {
        addMemeMeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForPermission();
            }
        });
    }


    private void requestForPermission() {

        if (ContextCompat.checkSelfPermission(HomeDrawerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeDrawerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(HomeDrawerActivity.this,"Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(HomeDrawerActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQ_CODE);
            }
        }
        else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQ_CODE);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
        }
        else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new UserProfileFragment()).commit();
        }
        else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingsFragment()).commit();
        }
        else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(),LogInActivity.class);
            startActivity(loginActivity);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    public void updateNavigationBarUserInfo() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userNavbarName = headerView.findViewById(R.id.user_navbar_name);
        TextView userNavbarMail = headerView.findViewById(R.id.user_navbar_mail);
        ImageView userNavbarPhoto = headerView.findViewById(R.id.user_navbar_photo);

        userNavbarMail.setText(currentUser.getEmail());
        userNavbarName.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(userNavbarPhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQ_CODE && data != null ) {
            pickedImgUri = data.getData() ;
            addMemeMeme.setImageURI(pickedImgUri);
        }
    }

    private void initializeAddMemePopup() {

        addMemePopup = new Dialog(this);
        addMemePopup.setContentView(R.layout.add_meme);
        addMemePopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addMemePopup.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        addMemePopup.getWindow().getAttributes().gravity = Gravity.TOP;

        //initializing popup elements

        addMemeUserPhoto = addMemePopup.findViewById(R.id.add_meme_user_photo);
        addMemePlus = addMemePopup.findViewById(R.id.add_meme_plus);
        addMemeMeme = addMemePopup.findViewById(R.id.add_meme_meme);
        addMemeTitle = addMemePopup.findViewById(R.id.add_meme_title);
        addMemeProgressBar = addMemePopup.findViewById(R.id.add_meme_progress_bar);


        //set user profile photo inside popup
        Glide.with(HomeDrawerActivity.this).load(currentUser.getPhotoUrl()).into(addMemeUserPhoto);

        addMemePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addMemePlus.setVisibility(View.INVISIBLE);
                addMemeProgressBar.setVisibility(View.VISIBLE);


                // checking if image field or title field is empty

                if (!addMemeTitle.getText().toString().isEmpty()
                        && pickedImgUri != null ) {

                    // accessing firebase storage
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("memes");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String memeDownloadLink = uri.toString();

                                    // create new Meme
                                    Meme meme = new Meme(memeDownloadLink,
                                            addMemeTitle.getText().toString(),
                                            currentUser.getDisplayName(),
                                            currentUser.getPhotoUrl().toString());

                                    // Add post to firebase database
                                    addMeme(meme);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    // error uploading picture
                                    showToast(e.getMessage());
                                    addMemeProgressBar.setVisibility(View.INVISIBLE);
                                    addMemePlus.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                } else {
                    showToast("Make sure to complete the title field and choose a Meme to upload!");
                    addMemePlus.setVisibility(View.VISIBLE);
                    addMemeProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void addMeme(Meme meme) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("memes").push();

        // get meme unique ID and update memeId
        String key = myRef.getKey();
        meme.setMemeId(key);


        // add meme data to firebase database

        myRef.setValue(meme).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("Post Added successfully");
                addMemeProgressBar.setVisibility(View.INVISIBLE);
                addMemePlus.setVisibility(View.VISIBLE);
                addMemePopup.dismiss();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(HomeDrawerActivity.this,message,Toast.LENGTH_LONG).show();
    }
}

