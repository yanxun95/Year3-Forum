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

import com.example.realgamerhours.map.MapActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;

public class Homepage extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button btnEvent;

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

        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Homepage.this, EventActivity.class));

            }
        });

    }

    private void setupUI(){
        btnEvent = (Button)findViewById(R.id.btnEvent);

    }

    private void init(){
        Button btnMap = (Button)findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Homepage.this, MapActivity.class));
                //startActivity(intent);
            }
        });
    }

    private boolean checkVersion(){
        Log.d(TAG, "checkVersion: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Homepage.this);

        if(available == ConnectionResult.SUCCESS) {
            //everything is ready to go
            Log.d(TAG, "checkVersion: Google Play Services is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "checkVersion: an error occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Homepage.this, available, ERROR_DIALOG_REQUEST);
        }else{
            Toast.makeText(this, "Error on checkVersion", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(Homepage.this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutMenu:{
                Logout();
                //startActivity(new Intent(Homepage.this, MainActivity.class));
            }
            case R.id.profileMenu:
                startActivity(new Intent(Homepage.this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
