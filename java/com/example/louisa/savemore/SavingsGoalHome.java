package com.example.louisa.savemore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import Models.SavingGoals;
import Models.SharedCost;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SavingsGoalHome extends BaseActivity {
    //Binding the layout with activity
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.descriptionSave)
    TextView description;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.update_amount)
    Button updateAmount;
    @Bind(R.id.btn_back)
    Button back;
    @Bind(R.id.percentage)
    TextView percentageValue;
    @Bind(R.id.update)
    EditText update;

    //Saving goals object from Saving Goals class in models package
    SavingGoals savingGoals;
    DatabaseReference databaseRef;
    String key;

    //Round Up Method
    Utilities.RoundUpMethod roundUpMethod = new Utilities.RoundUpMethod();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_goal_home);

        ButterKnife.bind(this);


        key = getIntent().getExtras().getString("key");
        databaseRef = mDatabase.getReference("savingGoals");

        setClickEvent();

        fetchUpdate();
    }

    //Method setClickEvent
    private void setClickEvent() {
        updateAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (validateFields()) {
                            savingGoals = dataSnapshot.getValue(SavingGoals.class);

                            //Get current progress
                            float oldPercent = savingGoals.getPercentage();
                            //New progress = current progress + new value inserted
                            float newPercent = Float.parseFloat(update.getText().toString()) + oldPercent;
                            //New Remain = Total Remain - new value inserted
                            float newRemainAmount = savingGoals.getRemainAmount() - Float.parseFloat(update.getText().toString());

                            saveNewPercent(newPercent, newRemainAmount);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SavingsGoalHome.this, HomePage.class));
            }
        });
    }//End of setClickEvent Method

    //SaveNewPercent Method - Update new progress
    private void saveNewPercent(final float newPercent, float newRemainAmount) {

        databaseRef.child(key).child("percentage").setValue(newPercent);
        databaseRef.child(key).child("remainAmount").setValue(newRemainAmount);

        roundUpMethod.roundUpMethod(newPercent, 2, BigDecimal.ROUND_HALF_UP);
        roundUpMethod.roundUpMethod(newRemainAmount,2, BigDecimal.ROUND_HALF_UP);

    }//End SaveNewPercent Method

    //Method displayContent - display values on Big circle
    private void displayContent() {
        title.setText(savingGoals.getDescription() + "   " + "£" + savingGoals.getRemainAmount());
        description.setText(savingGoals.getGoalName());
        calculatePercentage();
    }//End of displayContent method

    //Method fetchUpdate - Update changes
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
    }//End of fetchUpdate Method

    //Method CalculatePercentage
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

    }//End of CalculatePercentage method

    //Method to validate user make sure user fill in the fields
    private boolean validateFields() {
        if (TextUtils.isEmpty(update.getText().toString())) {
            update.setError("Please enter an amount");
            return false;
        }
        else if ( Float.parseFloat(update.getText().toString()) > savingGoals.getRemainAmount()){
            update.setError("Progress value cannot be more than goal amount");
            return false;

        }
        else {
            return true;
        }
    }//End of validation method
}//End of class