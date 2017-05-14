package com.sandersoft.applaudohomework.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.sandersoft.applaudohomework.R;

public class SplashActivity extends AppCompatActivity {

    long mSplashTime = 2000;
    SplashThread mSplashThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RelativeLayout lay_splash = (RelativeLayout) findViewById(R.id.lay_splash);
        lay_splash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        mSplashThread = new SplashThread();
        mSplashThread.start();
    }

    /**
     * Stops the thread and closes this activity, opening the main activity
     */
    public void stop(){
        runOnUiThread(new Runnable() {
            public void run() {
                if (mSplashThread != null) {
                    mSplashThread = null;

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);

                    finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    /**
     * Thread thats waits to remove the splash screen
     * @author Sander
     */
    public class SplashThread extends Thread{
        public SplashThread(){

        }

        public void run(){
            try {
                Thread.sleep(mSplashTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SplashActivity.this.stop();
        }
    }

}
