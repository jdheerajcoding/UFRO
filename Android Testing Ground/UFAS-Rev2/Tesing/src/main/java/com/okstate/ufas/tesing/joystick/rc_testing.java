package com.okstate.ufas.tesing.joystick;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.okstate.ufas.tesing.R;
import com.okstate.ufas.tesing.app.App;

/**
 * Created by ngoalie on 4/2/2014.
 */
public class rc_testing extends Activity{

    int throttle = 1500;
    int yaw = 1500;
    int pitch = 1500;
    int roll = 1500;

    private boolean killme = false;
    private double MULTIPLIER = .25; //For Security Purposes
    App app;

    Handler mHandler = new Handler();

    int[] rc = new int[8];

    View FlashUpdate;

    private Runnable update = new Runnable() {
        @Override
        public void run() {

            app.mw.ProcessSerialData(app.loggingON);

            app.frskyProtocol.ProcessSerialData(false);
            app.Frequentjobs();

            //displayData();

/*            if (FlashUpdate.getVisibility() == View.VISIBLE) {
                FlashUpdate.setVisibility(View.INVISIBLE);
            } else {
                FlashUpdate.setVisibility(View.VISIBLE);
            }*/

            app.mw.SendRequest(app.MainRequestMethod);

            /**
             * 0rcRoll 1rcPitch 2rcYaw 3rcThrottle 4rcAUX1 5rcAUX2 6rcAUX3 7rcAUX4
             * // interval [1150;1850]
             * Use this for the Joystick Class
             */

            rc = new int[] {roll, pitch, yaw, throttle , 0 , 0 , 0, 0};

            app.mw.SendRequestMSP_SET_RAW_RC(rc);
            if (!killme)
                mHandler.postDelayed(update, app.RefreshRate);

            if (app.D)
                Log.d(app.TAG, "loop " + this.getClass().getName());
        }
    };

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        app = (App) getApplication();
        setTitle("Joysticks");
        setContentView(R.layout.rc_testing);

        /* int throttle = 0;
        int yaw = 0;
        int pitch = 0;
        int roll = 0; */

        Button negThrottle = (Button) findViewById(R.id.NegThrottle);
        negThrottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                throttle = 1150;
                rc = new int[] {throttle, 1500, 1500, 1500 , 0 , 0 , 0, 0};

            }
        });

        Button neuThrottle = (Button) findViewById(R.id.NeuThrottle);
        neuThrottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                throttle = 1500;
                rc = new int[] {throttle, 1500, 1500, 1500 , 0 , 0 , 0, 0};
            }
        });

        Button PosThrottle = (Button) findViewById(R.id.PosThrottle);
        PosThrottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                throttle = 1850;
                rc = new int[] {throttle, 1500, 1500, 1500 , 0 , 0 , 0, 0};

            }
        });

        Button negYaw = (Button) findViewById(R.id.NegYaw);
        negYaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                yaw = 1150;

            }
        });

        Button neuYaw = (Button) findViewById(R.id.NeuYaw);
        neuYaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                yaw = 1500;
            }
        });

        Button posYaw = (Button) findViewById(R.id.PosYaw);
        posYaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                yaw = 1850;

            }
        });

        Button negPitch = (Button) findViewById(R.id.negPitch);
        negPitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pitch = 1150;

            }
        });

        Button neuPitch = (Button) findViewById(R.id.NeuPitch);
        neuPitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pitch = 1500;
            }
        });

        Button posPitch = (Button) findViewById(R.id.PosPitch);
        posPitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pitch = 1850;

            }
        });

        Button negRoll = (Button) findViewById(R.id.NegRoll);
        negRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                roll = 1150;

            }
        });

        Button neuRoll = (Button) findViewById(R.id.NeuRoll);
        neuRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                roll = 1500;
            }
        });

        Button posRoll = (Button) findViewById(R.id.PosRoll);
        posRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                roll = 1850;

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.joystick_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        app.ForceLanguage();
        killme = false;
        mHandler.postDelayed(update, app.RefreshRate);
    }

    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(null);
        killme = true;
    }

}
