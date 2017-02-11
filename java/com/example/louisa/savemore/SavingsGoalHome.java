package com.example.louisa.savemore;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import Models.SavingGoals;
import Models.SharedCost;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SavingsGoalHome extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.descriptionSave)
    TextView description;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.update_amount)
    Button updateAmount;
    @Bind(R.id.percentage)
    TextView percentageValue;
    @Bind(R.id.update)
    EditText update;

    SavingGoals savingGoals;
    DatabaseReference databaseRef;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_goal_home);

        ButterKnife.bind(this);


        key = getIntent().getExtras().getString("key");
       // savingGoals = (SavingGoals) getIntent().getExtras().getSerializable("item");
        databaseRef = mDatabase.getReference("savingGoals");
        //displayContent();

        setClickEvent();

        fetchUpdate();
    }

    private void setClickEvent() {
        updateAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (validateFields()) {
                            savingGoals = dataSnapshot.getValue(SavingGoals.class);
                            float oldPercent = savingGoals.getPercentage();
                            float newPercent = Float.parseFloat(update.getText().toString()) + oldPercent;
                            saveNewPercent(newPercent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    private void saveNewPercent(float newPercent) {
        databaseRef.child(key).child("percentage").setValue(newPercent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Update was successful", Toast.LENGTH_LONG).show();
                    update.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void displayContent() {
        title.setText(savingGoals.getDescription() + " /n" + savingGoals.getGoalAmount());
        description.setText(savingGoals.getGoalName());
        calculatePercentage();
    }

    private void fetchUpdate() {
        databaseRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                savingGoals = dataSnapshot.getValue(SavingGoals.class);
                displayContent();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void calculatePercentage() {
        float progressBarValue = 0;
        float total = savingGoals.getGoalAmount();
        float percentage = savingGoals.getPercentage();

        if (percentage == 0) {
            progressBar.setProgress(0);
            percentageValue.setText("0%");
        }
        if (percentage > 0) {
            progressBarValue = (percentage * 100) / total;
            int progress = Math.round(progressBarValue);
            progressBar.setProgress(progress);
            percentageValue.setText(progressBarValue + "%");
        }
    }

    //Method to validate user make sure user fill in the fields
    private boolean validateFields() {
        if (TextUtils.isEmpty(update.getText().toString())) {
            update.setError("Please enter an amount");
            return false;
        } else {
            return true;
        }
    }//End of validation method
}
