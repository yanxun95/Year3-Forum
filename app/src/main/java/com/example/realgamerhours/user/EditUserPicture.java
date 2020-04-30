package com.example.realgamerhours.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.realgamerhours.R;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class EditUserPicture extends AppCompatActivity {

    private ImageView editProfilePicture;
    private Button btnEditSavePicture;
    private ProgressBar editProfilePictureProgressBar;
    private Uri mainImageURI = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;

    private String name, userID;
    public static final String TAG = "EditUserPicture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_picture);
        setupUI();

        btnEditSavePicture.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid());
        userID = firebaseAuth.getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                name = userProfile.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditUserPicture.this, databaseError.getCode(), Toast.LENGTH_LONG).show();
            }
        });

        //crop the picture
        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask user for permission for access the photo storage
                if(ContextCompat.checkSelfPermission(EditUserPicture.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(EditUserPicture.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(EditUserPicture.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1 );
                }else{
                    //crop the image
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(EditUserPicture.this);
                }
            }
        });

        //download the profile picture
        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        editProfilePictureProgressBar.setVisibility(View.VISIBLE);

                        String image = task.getResult().getString("image");
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.mipmap.ic_launcher_round);
                        Glide.with(EditUserPicture.this).setDefaultRequestOptions(placeholderRequest).load(image).into(editProfilePicture);
                    }

                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(EditUserPicture.this, "Fire store Error: " + error, Toast.LENGTH_SHORT).show();
                }
                editProfilePictureProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        btnEditSavePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = firebaseAuth.getCurrentUser().getUid();
                final StorageReference imagePath = storageReference.child("profileImage").child(userID + ".jpg");
                editProfilePictureProgressBar.setVisibility(View.VISIBLE);

                imagePath.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imagePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            Map<String, String> userMap = new HashMap<>();

                            userMap.put("name", name);
                            userMap.put("image", downloadUri.toString());

                            firebaseFirestore.collection("Users").document(userID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(EditUserPicture.this, "Profile has been update.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditUserPicture.this, ProfileActivity.class));
                                    }else{
                                        String error = task.getException().getMessage();
                                        Toast.makeText(EditUserPicture.this, "Fire store Error: " + error, Toast.LENGTH_SHORT).show();
                                        editProfilePictureProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                    editProfilePictureProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            editProfilePictureProgressBar.setVisibility(View.INVISIBLE);
                            String error = task.getException().getMessage();
                            Toast.makeText(EditUserPicture.this, "Image Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void setupUI(){
        editProfilePicture = findViewById(R.id.editProfilePicture);
        btnEditSavePicture = findViewById(R.id.btnEditSavePicture);
        editProfilePictureProgressBar = findViewById(R.id.editProfileProgressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Profile Picture");
    }

    //Crop the image
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                editProfilePicture.setImageURI(mainImageURI);
                btnEditSavePicture.setEnabled(true);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
