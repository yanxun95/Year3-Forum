package com.example.realgamerhours.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.realgamerhours.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName, profileEmail;
    private Button btnEditPassword, btnEditPicture;
    private ImageView profileImage;
    private ProgressBar profileProgressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupUI();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid());
        userID = firebaseAuth.getCurrentUser().getUid();
        profileProgressBar.setVisibility(View.VISIBLE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                profileName.setText("Username: " + userProfile.getUsername());
                profileEmail.setText("Email: " + userProfile.getUserEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_LONG).show();
            }
        });

        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){

                        String image = task.getResult().getString("image");

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.mipmap.ic_launcher_round);

                        Glide.with(ProfileActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profileImage);
                    }

                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, "Fire store Error: " + error, Toast.LENGTH_SHORT).show();
                }
                profileProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditUserPassword.class));
            }
        });

        btnEditPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditUserPicture.class));
            }
        });
    }

    private void setupUI(){
        profileName = findViewById(R.id.profileUsername);
        profileEmail = findViewById(R.id.profileUserEmail);
        btnEditPassword = findViewById(R.id.btnEditProfilePassword);
        btnEditPicture = findViewById(R.id.editProfilePicture);
        profileImage = findViewById(R.id.profilePicture);
        profileProgressBar = findViewById(R.id.profileProgressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
    }

}