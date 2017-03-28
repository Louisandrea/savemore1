package com.example.louisa.savemore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {

    //Initiate for Firebase authorisation
    public FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //Initiate for Firebase datbaase
    public FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get current user
        mAuth = FirebaseAuth.getInstance();
        //Get database instances
        mDatabase = FirebaseDatabase.getInstance();

    }

    //Firebase key doesn't allow "." special character therefore this method is to replace "." with space
    public String cleanEmail(String email) {
        email = email.replace(".", "");
        return email;
    }//End of method

    //Get user email method
    public String getUserEmail() {
        //Get current user email
        String email = mAuth.getCurrentUser().getEmail();
        email = cleanEmail(email);
        return email;
    }//End of method
}//End of class
