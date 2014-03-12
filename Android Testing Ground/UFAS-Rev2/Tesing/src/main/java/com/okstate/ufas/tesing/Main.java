package com.okstate.ufas.tesing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.okstate.ufas.tesing.communication.BT;

public class Main extends Activity {

    String macAddress;
    String MacAddressBTTV;
    BT device;
    EditText text;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Intent i = new Intent(this, DeviceListActivity.class);
        startActivityForResult(i, REQUEST_CONNECT_DEVICE_MULTIWII);

        Button button = (Button) findViewById(R.id.button);
        text = (EditText) findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                device.Write(text.getText().toString().getBytes());
                Toast.makeText(context,text.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    private static final int REQUEST_CONNECT_DEVICE_MULTIWII = 1;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_MULTIWII:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    macAddress = address;
                    MacAddressBTTV = "MAC:" + macAddress;
                    Log.d("UFAS", MacAddressBTTV);
                    connectBT();
                }
                break;
        }
    }

    public void connectBT() {
        device = new BT(this);
        device.Enable();
        device.Connect(macAddress, 9600);
    }

}
