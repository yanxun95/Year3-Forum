package com.example.realgamerhours.forum;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.realgamerhours.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class forumHomepageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FloatingActionButton btnAddPost;
    private BottomNavigationView bottomNavigationView;

    private fragmentHome fragmentHome;
    private fragmentNotification fragmentNotification;
    private fragmentAccount fragmentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_homepage);
        setupUI();
        replaceFragment(fragmentHome);

        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(forumHomepageActivity.this, NewPostActivity.class));
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.forumBottomHome:
                        replaceFragment(fragmentHome);
                        return true;

                    case R.id.forumBottomNotification:
                        replaceFragment(fragmentNotification);
                        return true;

                    case R.id.forumBottomAccount:
                        replaceFragment(fragmentAccount);
                        return true;

                    default:
                        return false;

                }
            }
        });

    }

    private void setupUI(){
        firebaseAuth = FirebaseAuth.getInstance();
        btnAddPost = findViewById(R.id.btnAddForum);
        bottomNavigationView = findViewById(R.id.forumHomepageBottomNavigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forum");
        fragmentHome = new fragmentHome();
        fragmentNotification = new fragmentNotification();
        fragmentAccount = new fragmentAccount();
    }

     private void replaceFragment(Fragment fragment){
         FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
         fragmentTransaction.replace(R.id.forumMainContainer, fragment);
         fragmentTransaction.commit();
     }

}
