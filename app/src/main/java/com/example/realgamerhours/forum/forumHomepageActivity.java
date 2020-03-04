package com.example.realgamerhours.forum;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realgamerhours.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class forumHomepageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FloatingActionButton btnAddPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_homepage);

        firebaseAuth = FirebaseAuth.getInstance();
        btnAddPost = findViewById(R.id.btnAddForum);

        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(forumHomepageActivity.this, NewPostActivity.class));
            }
        });
    }

}
