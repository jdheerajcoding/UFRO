/*  MultiWii EZ-GUI
    Copyright (C) <2012>  Bartosz Szczygiel (eziosoft)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.okstate.ufas.tesing.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ezio.sec.Sec;
import com.okstate.ufas.tesing.R;
import com.okstate.ufas.tesing.app.App;
import com.okstate.ufas.tesing.communication.BT_New;
import com.okstate.ufas.tesing.config.ConfigActivity;
import com.okstate.ufas.tesing.graph.GraphsActivity;
import com.okstate.ufas.tesing.raw.RawDataActivity;


public class MainMultiWiiActivity extends Activity {
	private boolean killme = false;
	private App app;
	TextView TVInfo;
	MyPagerAdapter adapter;

	private final Handler mHandler = new Handler();
	private final Handler mHandler1 = new Handler() {
		// BinaryFileAccess file = new
		// BinaryFileAccess("/MultiWiiLogs/dump1.txt", true);

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BT_New.MESSAGE_STATE_CHANGE:
				Log.i("ccc", "MESSAGE_STATE_CHANGE: " + msg.arg1);

				switch (msg.arg1) {
				case BT_New.STATE_CONNECTED:
					// setStatus("Connected");
					break;
				case BT_New.STATE_CONNECTING:
					setStatus(getString(R.string.Connecting));
					break;
				case BT_New.STATE_NONE:
					break;
				}

				break;
			case BT_New.MESSAGE_WRITE:
				break;
			case BT_New.MESSAGE_READ:
				// byte[] readBuf = (byte[]) msg.obj;
				// String readMessage = new String(readBuf, 0, msg.arg1);
				// //file.WriteBytes(readBuf);

				break;
			case BT_New.MESSAGE_DEVICE_NAME:
				String deviceName = msg.getData().getString(BT_New.DEVICE_NAME);
				setStatus(getString(R.string.Connected) + "->" + deviceName);
				Log.d("ccc", "Device Name=" + deviceName);
				break;
			case BT_New.MESSAGE_TOAST:
				Log.i("ccc", "MESSAGE_TOAST:" + msg.getData().getString(BT_New.TOAST));
				Toast.makeText(getApplicationContext(), msg.getData().getString(BT_New.TOAST), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private final void setStatus(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		app = (App) getApplication();
        //app = new App(); //This may not work

		app.commMW.SetHandler(mHandler1);
		app.commFrsky.SetHandler(mHandler1);

		Log.d("aaa", "MAIN ON CREATE");
		//requestWindowFeature(Window.FEATURE_PROGRESS);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiwii_main_layout3);

		ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
		adapter = new MyPagerAdapter(this);

		adapter.SetTitles(new String[] { getString(R.string.page1), getString(R.string.page2), getString(R.string.page3) });
		final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapter.AddView(inflater.inflate(R.layout.multiwii_main_layout3_1, (ViewGroup) null, false));
		adapter.AddView(inflater.inflate(R.layout.multiwii_main_layout3_2, (ViewGroup) null, false));
		adapter.AddView(inflater.inflate(R.layout.multiwii_main_layout3_3, (ViewGroup) null, false));

		TVInfo = (TextView) adapter.views.get(0).findViewById(R.id.textViewInfoFirstPage);

		if (!app.FrskySupport) {
			((Button) adapter.views.get(1).findViewById(R.id.buttonFrsky)).setVisibility(View.GONE);
		}

		viewPager.setAdapter(adapter);

		/*TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		titleIndicator.setViewPager(viewPager);

		getSupportActionBar().setDisplayShowTitleEnabled(false);*/

		app.AppStartCounter++;
		app.SaveSettings(true);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		if (app.AppStartCounter == 1)
			startActivity(new Intent(getApplicationContext(), ConfigActivity.class));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onResume() {
		Log.d("aaa", "MAIN ON RESUME");
		app.ForceLanguage();
		super.onResume();

		killme = false;

		String app_ver = "";
		int app_ver_code = 0;
		try {
			app_ver = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			app_ver_code = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		TVInfo.setText(getString(R.string.app_name) + " " + app_ver + "." + String.valueOf(app_ver_code));

		if (app.commMW.Connected || app.commFrsky.Connected) {

			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {

			}

			mHandler.postDelayed(update, 100);
			// Log.d(BT_old.TAG, "OnResume if connected");

		}

		if (app.ConfigHasBeenChange_DisplayRestartInfo) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.PressOKToRestart)).setCancelable(false).setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
					EXIT(true);
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

	}

	@Override
	public void onPause() {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		super.onPause();

	}

	public void Close() {
		try {
			mHandler.removeCallbacksAndMessages(null);
			if (app.commMW.Connected)
				app.commMW.Close();
			if (app.commFrsky.Connected)
				app.commFrsky.Close();
		}

		catch (Exception e) {

		}

	}

	public void Connect() {
		if (app.CommunicationTypeMW == App.COMMUNICATION_TYPE_BT || app.CommunicationTypeMW == App.COMMUNICATION_TYPE_BT_NEW) {
			if (!app.MacAddress.equals("")) {
				app.commMW.Connect(app.MacAddress, app.SerialPortBaudRateMW);
				app.Say(getString(R.string.menu_connect));
			} else {
				Toast.makeText(getApplicationContext(), "Wrong MAC address. Go to Config and select correct device", Toast.LENGTH_LONG).show();
			}
			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {
			}
		} else {
			app.commMW.Connect(app.MacAddress, app.SerialPortBaudRateMW);
			app.Say(getString(R.string.menu_connect));
		}
	}

	public void ConnectFrsky(String MacAddress) {
		// if (app.CommunicationTypeFrSky == App.COMMUNICATION_TYPE_SERIAL_FTDI)
		// {
		// app.commMW.Connect(app.MacAddressFrsky, app.SerialPortBaudRateFrSky);
		// }

		if (app.CommunicationTypeFrSky == App.COMMUNICATION_TYPE_BT || app.CommunicationTypeFrSky == App.COMMUNICATION_TYPE_BT_NEW) {
			if (!app.MacAddressFrsky.equals("")) {
				app.commFrsky.Connect(app.MacAddressFrsky, app.SerialPortBaudRateFrSky);
				app.Say(getString(R.string.Connect_frsky));
			} else {
				Toast.makeText(getApplicationContext(), "Wrong MAC address. Go to Config and select correct device", Toast.LENGTH_LONG).show();
			}
			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {

			}
		}
	}

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.frskyProtocol.ProcessSerialData(false);
			if (app.commFrsky.Connected) {}
				//setSupportProgress((int) Functions.map(app.frskyProtocol.TxRSSI, 0, 110, 0, 10000));

			String t = new String();
			if (app.mw.BaroPresent == 1)
				t += "[BARO] ";
			if (app.mw.GPSPresent == 1)
				t += "[GPS] ";
			if (app.mw.multi_Capability.Nav)
				t += "[NAV" + String.valueOf(app.mw.multi_Capability.getNavVersion(app.mw.multiCapability)) + "] ";
			if (app.mw.SonarPresent == 1)
				t += "[SONAR] ";
			if (app.mw.MagPresent == 1)
				t += "[MAG] ";
			if (app.mw.AccPresent == 1)
				t += "[ACC]";

			String t1 = "[" + app.mw.MultiTypeName[app.mw.multiType] + "] ";
			t1 += "MultiWii " + String.valueOf(app.mw.version / 100f);

			if (app.mw.multi_Capability.ByMis)
				t += " by Mi�";

			t1 += "\n" + t + "\n";
			t1 += getString(R.string.SelectedProfile) + ":" + String.valueOf(app.mw.confSetting) + "\n";

			if (app.mw.ArmCount > 0) {

				int hours = app.mw.LifeTime / 3600;
				int minutes = (app.mw.LifeTime % 3600) / 60;
				int seconds = app.mw.LifeTime % 60;

				String timeString = hours + ":" + minutes + ":" + seconds;

				t1 += getString(R.string.ArmedCount) + ":" + String.valueOf(app.mw.ArmCount) + " " + getString(R.string.LiveTime) + ":" + timeString;
			}
			if (app.commMW.Connected)
				TVInfo.setText(t1);
			else
				TVInfo.setText(getString(R.string.NotConnected));

			if (!app.mw.multi_Capability.Nav) {
				((Button) adapter.views.get(0).findViewById(R.id.ButtonNavigation)).setVisibility(View.GONE);
			} else {
				((Button) adapter.views.get(0).findViewById(R.id.ButtonNavigation)).setVisibility(View.VISIBLE);
			}

			app.Frequentjobs();
			app.mw.SendRequest(app.MainRequestMethod);
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

			if (app.D)
				Log.d(app.TAG, "loop " + this.getClass().getName());
		}

	};

	// //buttons/////////////////////////////////////

	public void RawDataOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*startActivity(new Intent(getApplicationContext(), RawDataActivity.class));*/
	}

	public void RadioOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*startActivity(new Intent(getApplicationContext(), RadioActivity.class));*/
	}

	public void ConfigOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), ConfigActivity.class));
	}

	public void LoggingOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*startActivity(new Intent(getApplicationContext(), LogActivity.class));*/
	}

	public void GPSOnClick(View v) {
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*if (app.mw.GPSPresent == 1 || app.D) {
			killme = true;
			mHandler.removeCallbacksAndMessages(null);
			startActivity(new Intent(getApplicationContext(), GPSActivity.class));
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.NotAvailableInYourConfiguration), Toast.LENGTH_LONG).show();
		}*/

	}

	public void MotorsOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*startActivity(new Intent(getApplicationContext(), MotorsActivity.class));*/
	}

	public void PIDOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*startActivity(new Intent(getApplicationContext(), PIDActivity.class));*/
	}

	public void OtherOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*startActivity(new Intent(getApplicationContext(), CalibrationActivity.class));*/
	}

	public void MiscOnClick(View v) {
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*if (app.Protocol > 220) {
			killme = true;
			mHandler.removeCallbacksAndMessages(null);
			startActivity(new Intent(getApplicationContext(), MiscActivity.class));
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.OnlyWithMW23), Toast.LENGTH_LONG).show();
		}*/
	}

	public void FrskyOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*startActivity(new Intent(getApplicationContext(), FrskyActivity.class));*/
	}

	public void AUXOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		//startActivity(new Intent(getApplicationContext(), AUXActivity.class));
	}

	public void Dashboard1OnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Raw Data!",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), RawDataActivity.class));
        //startActivity(new Intent(getApplicationContext(), Dashboard11Activity.class));
	}

	public void Dashboard2OnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		//startActivity(new Intent(getApplicationContext(), Dashboard2Activity.class));
	}

	public void Dashboard3OnClick(View v) {
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
        /*if (app.checkGooglePlayServicesAvailability(this)) {
			killme = true;
			mHandler.removeCallbacksAndMessages(null);
			startActivity(new Intent(getApplicationContext(), Dashboard3Activity.class));
		}*/
	}

	public void NewMapOnClick(View v) {
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
        /*if (app.checkGooglePlayServicesAvailability(this)) {
			killme = true;
			mHandler.removeCallbacksAndMessages(null);
			startActivity(new Intent(getApplicationContext(), MapWaypointsActivity.class).putExtra("WAYPOINT", false));
		}*/
	}

	public void NavigationOnClick(View v) {
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*if (app.checkGooglePlayServicesAvailability(this)) {

			if (app.mw.multi_Capability.Nav || app.D) {
				killme = true;
				mHandler.removeCallbacksAndMessages(null);
				startActivity(new Intent(getApplicationContext(), NavActivity.class));
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.NotAvailableInYourConfiguration), Toast.LENGTH_LONG).show();
			}

		}*/

	}

	public void AboutOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		//startActivity(new Intent(getApplicationContext(), AboutActivity.class));
	}

	public void GraphsOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        //Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		startActivity(new Intent(getApplicationContext(), GraphsActivity.class));
	}

	public void AdvancedOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		//startActivity(new Intent(getApplicationContext(), AdvancedActivity.class));
	}

	public void DonateOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=EZ88MU3VKXSGG&lc=GB&item_name=MultiWiiAllinOne&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted"));
		//startActivity(browserIntent);
	}

	public void RateOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		/*Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
		startActivity(intent);*/
	}

	public void TestOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
		//startActivity(new Intent(getApplicationContext(), NavActivity.class));
	}

	public void CommunityMapOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/maps?q=http:%2F%2Fezio.ovh.org%2Fkml2.php&hl=pl&sll=48.856612,2.366095&sspn=0.015614,0.042272&t=h&z=3")));
	}

	public void ServosOnClick(View v) {
		if (app.Protocol > 220) {
			killme = true;
			mHandler.removeCallbacksAndMessages(null);
            Toast.makeText(this, "Unavaiable!",Toast.LENGTH_SHORT).show();
			//startActivity(new Intent(getApplicationContext(), ServosActivity.class));
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.OnlyWithMW23), Toast.LENGTH_LONG).show();
		}
	}

	public void VarioSoundOnOffOnClick(View v) {
		if (Sec.VerifyDeveloperID(Sec.GetDeviceID(getApplicationContext()), Sec.TestersIDs) || Sec.Verify(getApplicationContext(), "D..3")) {
			app.VarioSound = !app.VarioSound;
		} else {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

			dlgAlert.setTitle(getString(R.string.Locked));
			dlgAlert.setMessage(getString(R.string.DoYouWantToUnlock));
			// dlgAlert.setPositiveButton(getString(R.string.Yes), null);
			dlgAlert.setCancelable(false);
			dlgAlert.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					try {
						Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.ezio.ez_gui_unlocker");
						startActivity(LaunchIntent);
					} catch (Exception e) {
						Intent goToMarket = null;
						goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ezio.ez_gui_unlocker"));
						startActivity(goToMarket);
					}
					// finish();
				}
			});
			dlgAlert.setNegativeButton(getString(R.string.No), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// finish();
				}
			});

			dlgAlert.create().show();
		}

	}

	void EXIT(boolean restart) {
		try {
			//stopService(new Intent(getApplicationContext(), MOCK_GPS_Service.class));
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (app.DisableBTonExit && app.ConfigHasBeenChange_DisplayRestartInfo == false) {
			app.commMW.Disable();
			app.commFrsky.Disable();
		}

		app.sensors.stop();
		app.mw.CloseLoggingFile();
		app.notifications.Cancel(99);

		if (restart) {
			if (app.commMW.Connected)
				app.commMW.Disable();
			app.RestartApp();
		}
		Close();
		System.exit(0);
	}

	// /////menu////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		menu.findItem(R.id.menu_connect_frsky).setVisible(app.FrskySupport);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_exit) {
			EXIT(false);

			return true;
		}

		if (item.getItemId() == R.id.menu_connect) {

			Connect();

			mHandler.postDelayed(update, 100);
			return true;
		}

		if (item.getItemId() == R.id.menu_connect_frsky) {

			ConnectFrsky(app.MacAddressFrsky);

			mHandler.postDelayed(update, 100);

			//setSupportProgressBarVisibility(true);

			return true;
		}

		if (item.getItemId() == R.id.menu_disconnect) {
			app.Say(getString(R.string.menu_disconnect));
			Close();
			return true;
		}

		return false;
	}

	// ///menu end//////

	boolean doubleBackToExitPressedOnce = false;

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			EXIT(false);
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, getString(R.string.PressBackAgainToExit), Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;

			}
		}, 2000);
	}

}