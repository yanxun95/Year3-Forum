package com.example.realgamerhours.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.realgamerhours.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditUserProfile extends AppCompatActivity {

    private EditText editProfileName, editProfileEmail;
    private Button btnEditSave;
    private ImageView editProfilePicture;
    private ProgressBar editProfileProgressBar;
    private Uri mainImageURI = null;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;

    String username, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        setupUI();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                editProfileName.setText(userProfile.getUsername());
                editProfileEmail.setText(userProfile.getUserEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditUserProfile.this, databaseError.getCode(), Toast.LENGTH_LONG).show();
            }
        });

        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask user for permission for access the photo storage
                if(ContextCompat.checkSelfPermission(EditUserProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(EditUserProfile.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(EditUserProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1 );
                }else{
                    //crop the image
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(EditUserProfile.this);
                }
            }
        });

        btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String username = editProfileName.getText().toString().trim();
//                String email = editProfileName.getText().toString().trim();

//                if(validate()){
                    //update user profile

                    String userID = firebaseAuth.getCurrentUser().getUid();
                    StorageReference imagePath = storageReference.child("profileImage").child(userID + ".jpg");
                    editProfileProgressBar.setVisibility(View.VISIBLE);

                    imagePath.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                Task<Uri> downloadUri = storageReference.getDownloadUrl();
                                Toast.makeText(EditUserProfile.this, "Your change has been update", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditUserProfile.this, ProfileActivity.class));
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(EditUserProfile.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }

                            editProfileProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            //}
        });
    }

    private void setupUI(){
        editProfileName = findViewById(R.id.editUsername);
        editProfileEmail = findViewById(R.id.editEmail);
        btnEditSave = findViewById(R.id.btnEditSave);
        editProfilePicture = findViewById(R.id.editProfilePicture);
        editProfileProgressBar = findViewById(R.id.editProfileProgressBar);
    }

    private boolean validate(){
        Boolean result = false;

        username = editProfileName.getText().toString();
        userEmail = editProfileEmail.getText().toString();
        Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("username").equalTo(username);
        Query userEmailQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("userEmail").equalTo(userEmail);

        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0)
                    Toast.makeText(EditUserProfile.this, "Username already exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userEmailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0)
                    Toast.makeText(EditUserProfile.this, "Email already exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(username.isEmpty() || userEmail.isEmpty()){
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }else {
            result = true;
        }

        return result;
    }

    @SuppressLint("MissingSuperCall")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                editProfilePicture.setImageURI(mainImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
