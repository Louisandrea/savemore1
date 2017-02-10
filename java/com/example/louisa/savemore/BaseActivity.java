package com.example.louisa.savemore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    public FirebaseDatabase mDatabase;
    FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

    }

    //Firebase key doesn't allow "." special character therefore this method is to replace "." with space
    public String cleanEmail(String email) {
        email = email.replace(".", "");
        return email;
    }//End of method

    //Get user email method
    public String getUserEmail() {
        String email = mAuth.getCurrentUser().getEmail();
        email = cleanEmail(email);
        return email;
    }//End of method
}
