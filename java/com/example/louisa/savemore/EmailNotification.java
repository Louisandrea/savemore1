package com.example.louisa.savemore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EmailNotification extends Activity {

    //Bind layout with activity
    @Bind(R.id.editTextTo)
    EditText textTo;
    @Bind(R.id.editTextSubject)
    EditText textSubject;
    @Bind(R.id.editTextMessage)
    EditText textMessage;
    @Bind(R.id.buttonSend)
    Button buttonSend;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ButterKnife.bind(this);

        setOnClickEvent();

    }

    //setOnClick method
    private void setOnClickEvent()
    {
        //When user click on send button
        buttonSend.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendEmail();
            }
        });
    }//End of setOnClick method

    //Send email method
    private void sendEmail ()
    {

        String to = textTo.getText().toString();
        String subject = textSubject.getText().toString();
        String message = textMessage.getText().toString();

        Intent intent = new Intent(EmailNotification.this, HomePage.class);

        intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        //Let the client know there is a message body in it
        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }//End of send email method
}//End of class