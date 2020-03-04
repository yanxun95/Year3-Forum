package com.example.realgamerhours.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realgamerhours.HomepageActivity;
import com.example.realgamerhours.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText userEmail, password;
    private TextView numAttemptLeft, userRegister;
    private Button btmLogin;
    private int counter = 5;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
        }

        btmLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLogin(userEmail.getText().toString(), password.getText().toString());
            }
        });

        userRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));

            }
        });
    }

    private void setupUI(){
        userEmail = (EditText)findViewById(R.id.enterEmail);
        password = (EditText)findViewById(R.id.EnterUserPassword);
        numAttemptLeft = (TextView)findViewById(R.id.numAttemptLeft);
        btmLogin = (Button)findViewById(R.id.btnLogin);
        userRegister = (TextView)findViewById((R.id.userRegister));
        numAttemptLeft.setText("No of attempts remaining: 5");
    }

    private void validateLogin (String userEmail, String password){

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, HomepageActivity.class));
                }else{
                    Toast.makeText(MainActivity.this, "Please enter the correct email and password", Toast.LENGTH_SHORT).show();
                    counter--;
                    numAttemptLeft.setText("Number of incorrect attempts: " + counter);
                    progressDialog.dismiss();
                    if(counter == 0){
                        btmLogin.setEnabled(false);
                    }
                }
            }
        });
    }
}
