package com.example.realgamerhours.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.realgamerhours.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditUserPassword extends AppCompatActivity {

    private EditText editNewPassword, editReNewPassword;
    private Button btnEditSave;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String newPassword, reNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_password);
        setupUI();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate()){
                    String newPassword = editNewPassword.getText().toString();
                    firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(EditUserPassword.this, "Password has been update", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setupUI(){
        editNewPassword = findViewById(R.id.editNewPassword);
        editReNewPassword = findViewById(R.id.editReNewPassword);
        btnEditSave = findViewById(R.id.btnEditSavePassword);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Password");
    }

    private boolean validate(){
        Boolean result = false;

        newPassword = editNewPassword.getText().toString();
        reNewPassword = editReNewPassword.getText().toString();


        if(newPassword.isEmpty() || reNewPassword.isEmpty()){
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }else if(newPassword.length() < 8){
            Toast.makeText(this, "Password need to be at least 8 digits long", Toast.LENGTH_SHORT).show();
        }else if(!checkPassword(newPassword)){
            Toast.makeText(this, "Password must include 1 uppercase, 1 lowercase, 1 number and 1 special character ", Toast.LENGTH_SHORT).show();
        }else if(!newPassword.equals(reNewPassword)){
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

}
