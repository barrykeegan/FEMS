package org.leoapps.fems;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Splash extends Activity {

    private final String TAG = "Splash Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.i(TAG, "In Splash Activity onCreate");

        Utils.appContext = getApplicationContext();
        Utils.database = AppDatabase.getDatabase(getApplicationContext());

        Log.i(TAG, "In Splash Activity - set up reference to DB");

        new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                  Intent intent = new Intent(Splash.this, MainActivity.class);
                  Splash.this.startActivity(intent);
                  Splash.this.finish();
              }
          }, 250);
    }
}
