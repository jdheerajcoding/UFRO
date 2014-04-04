package info.androidhive.slidingmenu;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.content.Context;

public class ConnectFragment extends Fragment {
	
	public ConnectFragment(){
		
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		final String TAG = "Connect";
		final ImageButton bluetooth,wifi, tele, how;
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		View rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Log.d("SIZE: ",width + " x " + height);

	    bluetooth = (ImageButton) rootView.findViewById(R.id.ButtonBluetooth);
		wifi = (ImageButton) rootView.findViewById(R.id.ButtonWifi);
		wifi.setEnabled(false);
		wifi.setAlpha((float) 0.5);
		tele = (ImageButton) rootView.findViewById(R.id.ButtonTele);
		how = (ImageButton) rootView.findViewById(R.id.ButtonHow);

        //testing
        //height = 1001;
        if(height>1000)
        {
            RelativeLayout.LayoutParams  params = (RelativeLayout.LayoutParams) bluetooth.getLayoutParams();
            params.setMargins(30,102,0,0); //left, top, right, bottom
            bluetooth.setLayoutParams(params);
            RelativeLayout.LayoutParams  params1 = (RelativeLayout.LayoutParams) wifi.getLayoutParams();
            params1.setMargins(0,102,0,0); //left, top, right, bottom
            wifi.setLayoutParams(params1);
            RelativeLayout.LayoutParams  params2 = (RelativeLayout.LayoutParams) tele.getLayoutParams();
            params2.setMargins(0,102,0,0); //left, top, right, bottom
            tele.setLayoutParams(params1);
            RelativeLayout.LayoutParams  params3 = (RelativeLayout.LayoutParams) how.getLayoutParams();
            params3.setMargins(0,102,0,0); //left, top, right, bottom
            how.setLayoutParams(params1);
        } else  {
            //bluetooth.setImageResource(R.drawable.bluetooth_small);
            bluetooth.setScaleY(.6F);
            bluetooth.setScaleX(.6F);
            RelativeLayout.LayoutParams  params = (RelativeLayout.LayoutParams) bluetooth.getLayoutParams();
            params.setMargins(-55,35,0,0); //left, top, right, bottom
            //bluetooth.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            //Log.d("DEBUG",dpInPx+"");

            wifi.setScaleY(.6F);
            wifi.setScaleX(.6F);
            RelativeLayout.LayoutParams  params1 = (RelativeLayout.LayoutParams) wifi.getLayoutParams();
            params1.setMargins(-165,35,0,0); //left, top, right, bottom

            tele.setScaleY(.6F);
            tele.setScaleX(.6F);
            RelativeLayout.LayoutParams  params2 = (RelativeLayout.LayoutParams) tele.getLayoutParams();
            params2.setMargins(-170,35,0,0); //left, top, right, bottom

            how.setScaleY(.6F);
            how.setScaleX(.6F);
            RelativeLayout.LayoutParams  params3 = (RelativeLayout.LayoutParams) how.getLayoutParams();
            params3.setMargins(-160,35,-100,0); //left, top, right, bottom
        }

		View.OnClickListener buttonHandler = new View.OnClickListener() {
			boolean isBluetoothPressed=false,  isTelePressed=false, isHowPressed = false;
			  public void onClick(View v) {
			      switch(v.getId()) {
			        case R.id.ButtonBluetooth:
			        	Log.i(TAG, "Bluetooth");
			        	if(isBluetoothPressed)
			        		bluetooth.setBackgroundResource(R.drawable.bluetoothpressed);
			        	else
			        		bluetooth.setBackgroundResource(R.drawable.bluetooth);
			        	isBluetoothPressed=!isBluetoothPressed;
			          break;
			        case R.id.ButtonWifi:
			        	Log.i(TAG, "Wifi");
				      break;
			        case R.id.ButtonTele:
			        	Log.i(TAG, "Tele");
			        	if(isTelePressed)
			        		tele.setBackgroundResource(R.drawable.telepressed);
			        	else
			        		tele.setBackgroundResource(R.drawable.tele);
			        	isTelePressed=!isTelePressed;
				          break;
			        case R.id.ButtonHow:
			        	Log.i(TAG, "How");
			        	if(isHowPressed)
			        		how.setBackgroundResource(R.drawable.howpressed);
			        	else
			        		how.setBackgroundResource(R.drawable.how);
			        	isHowPressed=!isHowPressed;
				          break;
			      }
			   }
			};
			
		bluetooth.setOnClickListener(buttonHandler);
		wifi.setOnClickListener(buttonHandler);
		tele.setOnClickListener(buttonHandler);
		how.setOnClickListener(buttonHandler);

		
		return rootView;
    }
}


