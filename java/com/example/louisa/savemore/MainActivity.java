package com.example.louisa.savemore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity{

    //Binding layout with activity
    @Bind(R.id.change_email_button)
    Button btnChangeEmail;
    @Bind(R.id.change_password_button)
    Button btnChangePassword;
    @Bind(R.id.sending_pass_reset_button)
    Button btnSendResetEmail;
    @Bind(R.id.remove_user_button)
    Button btnRemoveUser;
    @Bind(R.id.changeEmail)
    Button changeEmail;
    @Bind(R.id.changePass)
    Button changePassword;
    @Bind(R.id.send)
    Button sendEmail;
    @Bind(R.id.remove)
    Button remove;
    @Bind(R.id.sign_out)
    Button signOut;
    @Bind(R.id.old_email)
    EditText oldEmail;
    @Bind(R.id.new_email)
    EditText newEmail;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.newPassword)
    EditText newPassword;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        setOnClickEvent();

    }

    //Method setOnClickEvent
    private void setOnClickEvent() {

        currentUser();

        //When user click on change email button
        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        //When user click on change password
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        //When user click on send reset email button
        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
        });

        //When user click on change email button
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                changeEmail();
            }
        });

        //When user click on change password button
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                changePassword();
            }
        });

        //When user click on send reset password email button
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                sendResetEmail();
            }
        });

        //When user click on delete account button
        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                removeUser();

            }
        });

        //When user click on sign out button
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }//End of on click method

    //Method currentUser
    private void currentUser()
    {
        //Hide all the buttons
        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        //Get Current user
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //If user aut state changed to null - Login activity will be launched
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

    }//End of method current user

    //Method change password
    private void changePassword()
    {
        if (user != null && !newPassword.getText().toString().trim().equals("")) {
            if (newPassword.getText().toString().trim().length() < 6) {
                newPassword.setError("Password too short, enter minimum 6 characters");
                progressBar.setVisibility(View.GONE);
            } else {
                //Update the password based on user's auth info in Firebase
                user.updatePassword(newPassword.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                    signOut();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        } else if (newPassword.getText().toString().trim().equals("")) {
            newPassword.setError("Enter password");
            progressBar.setVisibility(View.GONE);
        }

    }//End of method change password


    //Method send reset password email
    private void sendResetEmail()
    {
        //Make sure no space in between
        if (!oldEmail.getText().toString().trim().equals("")) {
            //Firebase auth instance method
            mAuth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            oldEmail.setError("Enter email");
            progressBar.setVisibility(View.GONE);
        }

    }//End of method send reset password email

    //Method removeUser
    private void removeUser()
    {
        if (user != null) {
            //Delete user information
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Your profile is deleted! Create a account?", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                                finish();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to delete your account.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }

    }//End of method removeUser

    //Method changeEmail
    private void changeEmail()
    {
        if (user != null && !newEmail.getText().toString().trim().equals("")) {
            //Update email method
            user.updateEmail(newEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                signOut();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else if (newEmail.getText().toString().trim().equals("")) {
            newEmail.setError("Enter email");
            progressBar.setVisibility(View.GONE);
        }
    }//End of method Change Email

    //Sign Out Method
    public void signOut() {
        mAuth.signOut();
    }

    @Override
    //Method for Auth Listener onResume
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }//End of method onResume

    @Override
    //Method for Auth Listener onStart
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }//End of method onStart

    @Override
    //Method for Auth Listener onStop
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }//End of method onStop

}//End of class