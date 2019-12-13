package com.example.realgamerhours;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity{

    private EditText username, email, password, rePassword;
    private TextView userLogin;
    private Button btnRegister;
    private ImageView userProfilePic;
    private FirebaseAuth firebaseAuth;
    String name, userEmail, userPassword, reUserPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUI();

        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    //upload to database
                    //trim is remove all the white space
                    String user_email = email.getText().toString().trim();
                    String user_password = password.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendUserData();
                                firebaseAuth.signOut();
                                Toast.makeText(RegistrationActivity.this, "RegistrationActivity Successful", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            }else{
                                FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                //Toast.makeText(RegistrationActivity.this, "RegistrationActivity Failed", Toast.LENGTH_LONG).show();
                                Log.e("LoginActivity", "Failed RegistrationActivity", e);
                            }
                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });

    }

    private void setupUI(){
        userProfilePic = (ImageView)findViewById(R.id.profilePic);
        username = (EditText)findViewById(R.id.editUsername);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        rePassword = (EditText)findViewById(R.id.reEnterPassword);
        btnRegister = (Button)findViewById(R.id.btmRegister);
        userLogin = (TextView)findViewById(R.id.userLogin);
    }

    private boolean validate(){
        Boolean result = false;

        name = username.getText().toString();
        userEmail = email.getText().toString();
        userPassword = password.getText().toString();
        reUserPassword = rePassword.getText().toString();
        Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("username").equalTo(name);
        Query userEmailQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("userEmail").equalTo(userEmail);

        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0)
                    Toast.makeText(RegistrationActivity.this, "Username already exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userEmailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0)
                    Toast.makeText(RegistrationActivity.this, "Email already exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(name.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || reUserPassword.isEmpty()){
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }else if(userPassword.length() < 8){
            Toast.makeText(this, "Password need to be at least 8 digits long", Toast.LENGTH_SHORT).show();
        }else if(!checkPassword(userPassword)){
            Toast.makeText(this, "Password must include 1 uppercase, 1 lowercase, 1 number and 1 special character ", Toast.LENGTH_SHORT).show();
        }else if(!userPassword.equals(reUserPassword)){
            Toast.makeText(this, "Please make sure password are the same", Toast.LENGTH_SHORT).show();
        }
        else {
            result = true;
        }

        return result;
    }

    private static boolean checkPassword(String password) {
        String specialChars = "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?";
        char currentCharacter;
        boolean number = false;
        boolean upperCase = false;
        boolean lowerCase = false;
        boolean specialCharacter = false;

        for (int i = 0; i < password.length(); i++) {
            currentCharacter = password.charAt(i);
            if (Character.isDigit(currentCharacter)) {
                number = true;
            } else if (Character.isUpperCase(currentCharacter)) {
                upperCase = true;
            } else if (Character.isLowerCase(currentCharacter)) {
                lowerCase = true;
            } else if (specialChars.contains(String.valueOf(currentCharacter))) {
                specialCharacter = true;
            }
        }

        return
                number && upperCase && lowerCase && specialCharacter;
    }

    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid());

        UserProfile userProfile = new UserProfile(name, userEmail);
        dataRef.setValue(userProfile);
    }

}
