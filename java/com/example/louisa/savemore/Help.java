package com.example.louisa.savemore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Help extends AppCompatActivity {

    @Bind(R.id.btn_back)
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ButterKnife.bind(this);

        setClickEvent();
    }

    //Method setClickEvent
    private void setClickEvent()
    {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Help.this, HomePage.class));
            }
        });
    }//End of setClickEvent method

}//End of class
