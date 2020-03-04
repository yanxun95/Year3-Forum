package com.example.realgamerhours;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realgamerhours.calendar.CalendarActivity;
import com.example.realgamerhours.event.EventActivity;
import com.example.realgamerhours.forum.forumHomepageActivity;
import com.example.realgamerhours.map.MapActivity;
import com.example.realgamerhours.user.MainActivity;
import com.example.realgamerhours.user.ProfileActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;

public class HomepageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button btnEvent, btnCalendar, btnMap, btnPost;

    public static final String TAG = "HomePage";
    //handle error if we don't have the correct version
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        setupUI();

        firebaseAuth = FirebaseAuth.getInstance();
        if(checkVersion()){
            init();
        }
    }

    private void setupUI(){
        btnEvent = (Button)findViewById(R.id.btnEvent);
        btnCalendar = (Button)findViewById(R.id.btnCalendar);
        btnMap = (Button)findViewById(R.id.btnMap);
        btnPost = (Button)findViewById(R.id.btnForum);
    }

    private void init(){
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, MapActivity.class));
                //startActivity(intent);
            }
        });

        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, EventActivity.class));

            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, CalendarActivity.class));

            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, forumHomepageActivity.class));
            }
        });
    }

    private boolean checkVersion(){
        Log.d(TAG, "checkVersion: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomepageActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            //everything is ready to go
            Log.d(TAG, "checkVersion: Google Play Services is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "checkVersion: an error occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomepageActivity.this, available, ERROR_DIALOG_REQUEST);
        }else{
            Toast.makeText(this, "Error on checkVersion", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(HomepageActivity.this, MainActivity.class));
    }

    //create the menu on top of the right
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //handle the click event on the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutMenu:{
                Logout();
                //startActivity(new Intent(HomepageActivity.this, MainActivity.class));
            }
            case R.id.profileMenu:
                startActivity(new Intent(HomepageActivity.this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
