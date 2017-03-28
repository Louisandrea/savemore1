package com.example.louisa.savemore;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;


import java.util.HashMap;
import java.util.Map;

import Models.SavingGoals;
import butterknife.Bind;
import butterknife.ButterKnife;


public class AddSavingGoals extends BaseActivity {

    //Binding the layout with activity
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.price)
    EditText price;
    @Bind(R.id.descriptionSave)
    EditText descriptionSave;
    @Bind(R.id.btn_save)
    Button btn_save;

    //Database instances
    DatabaseReference databaseRef;

    String key;
    String senderEmail;
    String goalName;
    String receiverEmail;
    String description;
    float amountToShare;
    float totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saving_goals);

        ButterKnife.bind(this);

        databaseRef = mDatabase.getReference("savingGoals");
        key = databaseRef.push().getKey();
        setClickEvents();

    }

    //Method click event
    private void setClickEvents() {
        //When user click on save button
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSave();
            }
        });

    }//End of click event method


    //Method for saving the details
    private void processSave() {

        if (validateFields()) {
            //String loginUserId = mAuth.getCurrentUser();
            senderEmail = mAuth.getCurrentUser().getEmail();

            goalName = name.getText().toString();
            receiverEmail = email.getText().toString();
            description = descriptionSave.getText().toString();
            totalAmount = Float.parseFloat(price.getText().toString());

            saveCosts();
        }
    }//End of process save method

    //Save cost method
    private void saveCosts() {
        amountToShare = totalAmount / 2;

        SavingGoals savingGoals = new SavingGoals();
        savingGoals.setEmail(receiverEmail);
        savingGoals.setGoalName(goalName);
        savingGoals.setGoalAmount(amountToShare);
        savingGoals.setRemainAmount(amountToShare);
        savingGoals.setSender_email(senderEmail);
        savingGoals.setTotal_amount(totalAmount);
        savingGoals.setDescription(description);

        senderEmail = cleanEmail(senderEmail);

        Map<String, Object> shareValues = savingGoals.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, shareValues);


        //Firebase method, Update values
        databaseRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Goal Saved", Toast.LENGTH_LONG).show();
                    databaseRef.child(key).child(senderEmail).setValue(true);
                    receiverEmail = cleanEmail(receiverEmail);
                    databaseRef.child(key).child(receiverEmail).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to save at the moment", Toast.LENGTH_LONG).show();
                }
            }
        });
        databaseRef.setPriority(ServerValue.TIMESTAMP);
    }//End of save cost method

    //Method validate fields to make sure user key in the value
    private boolean validateFields() {
        if (TextUtils.isEmpty(descriptionSave.getText().toString())) {
            descriptionSave.setError("Please enter a goal name");
            return false;
        } else if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Please enter a valid email address");
            return false;
        } else if (TextUtils.isEmpty(price.getText().toString())) {
            price.setError("Please enter a valid amount");
            return false;
        } else {
            return true;
        }
    }//End of the validation method
}//End of class