package com.fibonacciStudios.joystickContorller;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import org.w3c.dom.Text;

public class JoystickMain extends Activity {

    private TextView leftView;
    private TextView rightView;
    private DualJoystickView dualJoystickView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Joysticks");
        setContentView(R.layout.activity_joystick_main);

        leftView = (TextView) findViewById(R.id.textView);
        leftView.setText("Hello");
        rightView = (TextView) findViewById(R.id.textView2);
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
        getMenuInflater().inflate(R.menu.joystick_main, menu);
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

    public boolean dispatchTouchEvent(MotionEvent event) {

        if(leftView!=null && rightView!=null)  {
            leftView.setText("L: "+dualJoystickView.stickL.getXCoor()/1000+", "+dualJoystickView.stickL.getYCoor()/1000);
            rightView.setText("R: "+dualJoystickView.stickR.getXCoor()/1000+", "+dualJoystickView.stickR.getYCoor()/1000);
        }

        return super.dispatchTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent ev) {

        return super.onTouchEvent(ev);
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
