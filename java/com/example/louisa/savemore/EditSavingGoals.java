package com.example.louisa.savemore;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import Models.SavingGoals;
import Models.SharedCost;
import butterknife.Bind;
import butterknife.ButterKnife;


public class EditSavingGoals extends BaseActivity {

    //Bind layout with activity
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.price)
    EditText price;
    @Bind(R.id.descriptionSave)
    EditText description;
    @Bind(R.id.btn_save)
    Button btn_save;

    //Saving goals object from saving goals class in models package
    SavingGoals savingGoals;
    DatabaseReference databaseRef;

    String key;
    String senderEmail;
    String goalName;
    String receiverEmail;
    String descriptionSave;

    float amountToShare;
    float totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saving_goals);

        ButterKnife.bind(this);

        key = getIntent().getExtras().getString("key");
        savingGoals = (SavingGoals) getIntent().getExtras().getSerializable("item");
        databaseRef = mDatabase.getReference("savingGoals");
        displayContent();

        setClickEvents();
    }

    //Method displayContent
    private void displayContent() {
        name.setText(savingGoals.getGoalName());
        email.setText(savingGoals.getEmail());
        price.setText(String.valueOf(savingGoals.getTotal_amount()));
        description.setText(savingGoals.getDescription());

    }//End of displayContent method


    //Method setClickEvents
    private void setClickEvents() {
        //When user click on save button
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSave();
            }
        });

    }//End ofsetClickEvents method

    //Method processSave
    private void processSave() {

        if (validateFields()) {
            senderEmail = mAuth.getCurrentUser().getEmail();
            goalName = name.getText().toString();
            receiverEmail = email.getText().toString();
            descriptionSave = description.getText().toString();
            totalAmount = Float.parseFloat(price.getText().toString());
            saveCosts();
        }
    }//End of processSave method

    //Method saveCosts - Update values
    private void saveCosts() {
        amountToShare = totalAmount / 2;

        Utilities.RoundUpMethod roundUp = new Utilities.RoundUpMethod();
        amountToShare = roundUp.roundUpMethod(amountToShare, 2, BigDecimal.ROUND_HALF_UP);

        SavingGoals savingGoals = new SavingGoals();

        savingGoals.setEmail(receiverEmail);
        savingGoals.setGoalName(goalName);
        savingGoals.setGoalAmount(amountToShare);
        savingGoals.setRemainAmount(amountToShare);
        savingGoals.setDescription(descriptionSave);
        savingGoals.setSender_email(senderEmail);
        savingGoals.setTotal_amount(totalAmount);

        senderEmail = cleanEmail(senderEmail);

        Map<String, Object> shareValues = savingGoals.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, shareValues);

        //Firebase method update values
        databaseRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Changes Saved", Toast.LENGTH_LONG).show();
                    databaseRef.child(key).child(senderEmail).setValue(true);
                    receiverEmail = cleanEmail(receiverEmail);
                    databaseRef.child(key).child(receiverEmail).setValue(true);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to save at the moment", Toast.LENGTH_LONG).show();
                }
            }
        });
        databaseRef.setPriority(ServerValue.TIMESTAMP);
    }//End of saveCosts method


    //Method validateFields
    private boolean validateFields() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("Please enter a name");
            return false;
        } else if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Please enter a valid email address");
            return false;
        } else if (TextUtils.isEmpty(price.getText().toString())) {
            price.setError("Please enter a valid price");
            return false;
        } else {
            return true;
        }
    }//End of validateFields method
}//End of class













//databaseRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if (task.isSuccessful()) {
//        Toast.makeText(getApplicationContext(), "Goal Shared", Toast.LENGTH_LONG).show();
//
//        } else {
//        Toast.makeText(getApplicationContext(), "Unable to save at the moment", Toast.LENGTH_LONG).show();
//        }
//        }
//        });