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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import Models.SavingGoals;
import Models.SharedCost;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SharedCostHome extends BaseActivity {

    @Bind(R.id.price)
    TextView price;
    @Bind(R.id.list_friend)
    TextView listFriend;

    SharedCost sharedCost;
    DatabaseReference databaseRef;

    String key;
    String friends;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_cost_home);

        ButterKnife.bind(this);


        key = getIntent().getExtras().getString("key");
        databaseRef = mDatabase.getReference("sharedCost");

        fetchUpdate();
    }

    private void displayContent() {

        price.setText("£" + String.valueOf(sharedCost.getTotal_amount()));
        listFriend.setText(sharedCost.getFriend_involve());
    }

    private void fetchUpdate() {
        databaseRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sharedCost = dataSnapshot.getValue(SharedCost.class);
                displayContent();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
