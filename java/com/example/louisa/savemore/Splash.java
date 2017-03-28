package com.example.louisa.savemore;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

    // Splash Screen Duration
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                //Executed once and launch Login activity
                Intent i = new Intent(Splash.this, LoginActivity.class);
                startActivity(i);
                //End Splash Activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}