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

    private void setOnClickEvent()
    {
        buttonSend.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

        sendEmail();
            }
        });
    }

    private void sendEmail ()
    {

        String to = textTo.getText().toString();
        String subject = textSubject.getText().toString();
        String message = textMessage.getText().toString();

        Intent intent = new Intent(EmailNotification.this, HomePage.class);

        intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
        //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
        //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        //need this to prompts email client only
        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }
}