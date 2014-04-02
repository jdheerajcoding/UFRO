package com.okstate.ufas.tesing.joystick;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import com.okstate.ufas.tesing.R;
import com.okstate.ufas.tesing.app.App;

import org.w3c.dom.Text;

public class JoystickMain extends Activity {

    private boolean killme = false;
    private double MULTIPLIER = .25; //For Security Purposes
    App app;

    Handler mHandler = new Handler();

    private TextView leftView;
    private TextView rightView;
    private DualJoystickView dualJoystickView;

    int[] rc;

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
            app.mw.SendRequestMSP_SET_RAW_RC(rc);
            if (!killme)
                mHandler.postDelayed(update, app.RefreshRate);

            if (app.D)
                Log.d(app.TAG, "loop " + this.getClass().getName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (App) getApplication();
        setTitle("Joysticks");
        setContentView(R.layout.activity_joystick_main);

        rc = new int[8]; //Used for writing the RC channels to the MultiWii

        leftView = (TextView) findViewById(R.id.textView);
        leftView.setText("");
        rightView = (TextView) findViewById(R.id.textView2);
        rightView.setText("");
        dualJoystickView = (DualJoystickView) findViewById(R.id.view);

        /*if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
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

    private int map(int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    /**
     * 0rcRoll 1rcPitch 2rcYaw 3rcThrottle 4rcAUX1 5rcAUX2 6rcAUX3 7rcAUX4
     * // interval [1150;1850]
     * Use this for the Joystick Class
     */
    public boolean dispatchTouchEvent(MotionEvent event) {

        Log.i("INFO", "TOUCHED");

        if(leftView!=null && rightView!=null)  {

            int throttle = map((int)(MULTIPLIER*dualJoystickView.stickR.getYCoor()*4), -1000, 1000, 1150, 1850);
            int roll = map((int)(MULTIPLIER*dualJoystickView.stickL.getXCoor()*4), -1000, 1000, 1150, 1850);
            int pitch = map((int)(MULTIPLIER * dualJoystickView.stickL.getYCoor()*4), -1000, 1000, 1150, 1850);
            int yaw = map((int)(MULTIPLIER*dualJoystickView.stickR.getXCoor()*4), -1000, 1000, 1150, 1850);

            leftView.setText("L: "+throttle+", "+yaw);

            rightView.setText("R: "+roll+", "+pitch);

            rc = new int[]{(int) (MULTIPLIER*dualJoystickView.stickR.getXCoor() + 1000),
                    (int)(MULTIPLIER*dualJoystickView.stickR.getYCoor()+1000),
                    (int)(MULTIPLIER*dualJoystickView.stickL.getXCoor()+1000),
                    (int)(MULTIPLIER*dualJoystickView.stickL.getYCoor()+1000),0,0,0,0};

            app.mw.rcThrottle = throttle;
            app.mw.rcRoll = roll;
            app.mw.rcPitch = pitch;
            app.mw.rcYaw = yaw;

            /*Log.d("rcThrottle", ""+app.mw.rcThrottle);
            Log.d("rcYaw", ""+app.mw.rcYaw);
            Log.d("rcPitch", ""+app.mw.rcPitch);
            Log.d("rcRoll", ""+app.mw.rcRoll);
            Log.d("rcAUX1", ""+app.mw.rcAUX1);
            Log.d("rcAUX2", ""+app.mw.rcAUX2);
            Log.d("rcAUX3", ""+app.mw.rcAUX3);
            Log.d("rcAUX4", ""+app.mw.rcAUX4);*/
        }

        return super.dispatchTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent ev) {

        if(leftView!=null && rightView!=null)  {

            int throttle = map((int)(MULTIPLIER*dualJoystickView.stickR.getYCoor()*4), -1000, 1000, 1150, 1850);
            int roll = map((int)(MULTIPLIER*dualJoystickView.stickL.getXCoor()*4), -1000, 1000, 1150, 1850);
            int pitch = map((int)(MULTIPLIER * dualJoystickView.stickL.getYCoor()*4), -1000, 1000, 1150, 1850);
            int yaw = map((int)(MULTIPLIER*dualJoystickView.stickR.getXCoor()*4), -1000, 1000, 1150, 1850);

            leftView.setText("L: "+throttle+", "+yaw);

            rightView.setText("R: "+roll+", "+pitch);

            rc = new int[]{(int) (MULTIPLIER*dualJoystickView.stickR.getXCoor() + 1000),
                    (int)(MULTIPLIER*dualJoystickView.stickR.getYCoor()+1000),
                    (int)(MULTIPLIER*dualJoystickView.stickL.getXCoor()+1000),
                    (int)(MULTIPLIER*dualJoystickView.stickL.getYCoor()+1000),0,0,0,0};

            app.mw.rcThrottle = throttle;
            app.mw.rcRoll = roll;
            app.mw.rcPitch = pitch;
            app.mw.rcYaw = yaw;

            /*Log.d("rcThrottle", ""+app.mw.rcThrottle);
            Log.d("rcYaw", ""+app.mw.rcYaw);
            Log.d("rcPitch", ""+app.mw.rcPitch);
            Log.d("rcRoll", ""+app.mw.rcRoll);
            Log.d("rcAUX1", ""+app.mw.rcAUX1);
            Log.d("rcAUX2", ""+app.mw.rcAUX2);
            Log.d("rcAUX3", ""+app.mw.rcAUX3);
            Log.d("rcAUX4", ""+app.mw.rcAUX4);*/
        }

        return super.onTouchEvent(ev);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_joystick_main, container, false);

            return rootView;
        }
    }

}
