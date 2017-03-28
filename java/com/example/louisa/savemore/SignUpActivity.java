package com.example.louisa.savemore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends BaseActivity {

    //Bind layout component with activity variable
    @Bind(R.id.email)
    EditText inputEmail;
    @Bind(R.id.password)
    EditText inputPassword;
    @Bind(R.id.sign_in_button)
    Button btnSignIn;
    @Bind(R.id.sign_up_button)
    Button btnSignUp;
    @Bind(R.id.btn_reset_password)
    Button btnResetPassword;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        setOnClickEvent();

    }

    //Method set on click event
    private void setOnClickEvent() {
        //When user click on reset password button
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, ResetPasswordActivity.class));
            }
        });

        //When user click on sign in button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //When user click on sign up button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {

                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return ;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return ;
                }

                progressBar.setVisibility(View.VISIBLE);

                signUp();

            }
        });

    }//End of on click method

    //Method signUp
    private void signUp () {
        //Method create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        //If not successful, message will be displayed
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        //If task is successful, the auth state listener will be notified
                        else {
                            startActivity(new Intent(SignUpActivity.this, HomePage.class));
                            finish();
                        }
                    }
                });
    }//End signUp method


    @Override
    //Method onResume for Auth listener
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }//End of method onResume

}//End of class