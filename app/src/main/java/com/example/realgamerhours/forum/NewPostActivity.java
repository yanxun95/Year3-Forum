package com.example.realgamerhours.forum;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.realgamerhours.R;
import com.example.realgamerhours.user.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private Button btnUploadPost;
    private EditText newPostDesc;
    private ProgressBar newPostProgressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;

    private String username, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        setupUI();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid());
        userID = firebaseAuth.getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                username = userProfile.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewPostActivity.this, databaseError.getCode(), Toast.LENGTH_LONG).show();
            }
        });

        btnUploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = newPostDesc.getText().toString();

                if((!TextUtils.isEmpty(desc))){
                    newPostProgressBar.setVisibility(View.VISIBLE);

                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("userID", userID);
                    postMap.put("username", username);
                    postMap.put("desc", desc);
                    postMap.put("timesStamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Post").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(NewPostActivity.this, forumHomepageActivity.class));
                                            finish();

                                        }else{
                                            String error = task.getException().getMessage();
                                            Toast.makeText(NewPostActivity.this, "Fire store Error: " + error, Toast.LENGTH_SHORT).show();
                                            newPostProgressBar.setVisibility(View.INVISIBLE);

                                        }
                                        newPostProgressBar.setVisibility(View.INVISIBLE);
                                    }
                    });
                }else{
                    Toast.makeText(NewPostActivity.this, "Description is empty.", Toast.LENGTH_SHORT).show();
                    newPostProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setupUI(){
        btnUploadPost = findViewById(R.id.btnPostForum);
        newPostProgressBar = findViewById(R.id.newForumProgressBar);
        newPostDesc = findViewById(R.id.forumAddDescription);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Post");
    }

}
