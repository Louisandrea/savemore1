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

import java.util.HashMap;
import java.util.Map;

import Models.SavingGoals;
import Models.SharedCost;
import butterknife.Bind;
import butterknife.ButterKnife;


public class EditSharedCost extends BaseActivity {
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.price)
    EditText price;
    @Bind(R.id.friendsInvolve)
    EditText friendsInvolve;
    @Bind(R.id.btn_save)
    Button btn_save;

    SharedCost sharedCost;
    DatabaseReference databaseRef;

    String key;
    String friends;
    String senderEmail;
    String productName;
    String receiverEmail;
    String receipients[];
    String otherFriends[];
    int count = 1;
    float amountToShare;
    float totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shared_cost);

        ButterKnife.bind(this);

        key = getIntent().getExtras().getString("key");
        sharedCost = (SharedCost) getIntent().getExtras().getSerializable("item");
        databaseRef = mDatabase.getReference("sharedCost");
        displayContent();

        setClickEvents();
    }

    private void displayContent() {
        name.setText(sharedCost.getName());
        email.setText(sharedCost.getEmail());
        price.setText(String.valueOf(sharedCost.getPrice()));

    }


    private void setClickEvents() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSave();
            }
        });

    }


    private void processSave() {

        if (validateFields()) {
            //String loginUserId = mAuth.getCurrentUser();
            senderEmail = mAuth.getCurrentUser().getEmail();

            productName = name.getText().toString();
            receiverEmail = email.getText().toString();
            totalAmount = Float.parseFloat(price.getText().toString());
            friends = friendsInvolve.getText().toString();
            //mDatabase.getReference("sharedCost").child (productName).child(receiverEmail).child(amountToShare)

            saveCosts();
        }
    }


    private void saveCosts() {
        receipients = receiverEmail.split(",");
        otherFriends = friends.split(",");
        amountToShare = totalAmount / (receipients.length + otherFriends.length);
        amountToShare = Math.round(amountToShare);

        SharedCost sharedCost = new SharedCost();
        sharedCost.setEmail(receiverEmail);
        sharedCost.setName(productName);
        sharedCost.setPrice(amountToShare);
        sharedCost.setSender_email(senderEmail);
        sharedCost.setTotal_amount(totalAmount);
        sharedCost.setFriend_involve(friends);

        senderEmail = cleanEmail(senderEmail);

        Map<String, Object> shareValues = sharedCost.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, shareValues);

        //Save into Firebase (Update)
        databaseRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Cost Shared", Toast.LENGTH_LONG).show();
                    databaseRef.child(key).child(senderEmail).setValue(true);

                    for (String receipient : receipients) {
                        receiverEmail = cleanEmail(receipient);
                        databaseRef.child(key).child(receiverEmail).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (count == receipients.length) {
                                    finish();
                                }
                            }
                        });
                        count++;
                        finish();
                    }

                    for (final String otherFriend : otherFriends) {
                        databaseRef.child(key).child(friends).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (count == otherFriends.length) {

                                    finish();
                                }

                            }
                        });
                        count++;
                        finish();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to save at the moment", Toast.LENGTH_LONG).show();
                }
            }
        });//End of update
        databaseRef.setPriority(ServerValue.TIMESTAMP);
    }//End of sort shared cost value

    private boolean validateFields() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            email.setError("Please enter a name");
            return false;
        } else if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Please enter a valid email address");
            return false;
        } else if (TextUtils.isEmpty(price.getText().toString())) {
            price.setError("Please enter a valid price");
            return false;
        } else if (TextUtils.isEmpty(friendsInvolve.getText().toString())) {
            friendsInvolve.setError("Remember to enter your name");
            return false;
        } else {
            return true;
        }
    }
}