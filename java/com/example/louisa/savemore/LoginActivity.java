package com.example.louisa.savemore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    //Bind layout with activity
    @Bind(R.id.email)
    EditText inputEmail;
    @Bind(R.id.password)
    EditText inputPassword;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.btn_signup)
    Button btnSignup;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.btn_reset_password)
    Button btnReset;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //If the user login direct to home page
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomePage.class));
            finish();
        }

        //Call method for on click event
        setOnClickEvent();
    }

    //Method for all on click event
    private void setOnClickEvent()
    {
        //When user click on sign up link
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        //When user click on reset link
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        //When user click on login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                signIn();

            }
        });

    }//End of method on click

    //Method signIn
    private void signIn() {
        //Authenticate user instance
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        //If failed, message will be shown
                        if (!task.isSuccessful()) {
                            // If password too short
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            }
                            //If Firebase cannot find the user account
                            else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        }
                        //If success, direct user to home page
                        else {
                            Intent intent = new Intent(LoginActivity.this, HomePage.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

    }//End method signIn
}//End of class