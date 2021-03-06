package com.preciosclaros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by lucas on 28/6/2017.
 */

public class Splash extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);

        /* New Handler to start the
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                if(!sharedPreferences.contains(Constants.FIRST_TIME))
                {
                    /* Create an Intent that will start the. */
                    Intent mainIntent = new Intent(Splash.this,TutorialActivity.class);
                    Splash.this.startActivity(mainIntent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    Splash.this.finish();
                }else{
                    /* Create an Intent that will start the. */
                    Intent mainIntent = new Intent(Splash.this,HomeActivity.class);
                    Splash.this.startActivity(mainIntent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    Splash.this.finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
