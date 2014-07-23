package com.example.mtc;

import java.util.List;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;


public class AndroidGPSTrackingActivity extends Activity {

	TextView mAddress;
	GPSTracker gps;
	List<Address> addresses;
	String myAddress = "";
    double destLat,destLng;    
    Handler handler;
	TimerTask updateTask;
	Timer t;
	
	AlarmScreen alarmScreen;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mAddress = (TextView) findViewById(R.id.address);
		
		AlarmScreen.context = this;
		
		Bundle sender = getIntent().getExtras();
		destLat = sender.getDouble("Lat");
		destLng = sender.getDouble("Lng");
		
//		Log.d("destLat",""+destLat);
//		Log.d("destLng",""+destLng);
		
		AlarmScreen.destLat = destLat;
		AlarmScreen.destLng = destLng;
		
		t = new Timer();
		handler = new Handler();
		
	
		gps = new GPSTracker(AndroidGPSTrackingActivity.this);

		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			loadUpdate();
			try {
				// Getting address from found locations.

				Geocoder geocoder = new Geocoder(
						AndroidGPSTrackingActivity.this, Locale.getDefault());
				addresses = geocoder.getFromLocation(latitude, longitude, 1);

				int count = addresses.get(0).getMaxAddressLineIndex();
				if (count > 0) {
					for (int i = 0; i < count; i++) {
						myAddress = myAddress
								+ addresses.get(0).getAddressLine(i) + "\n";
					}
					Log.d("address line", ""+myAddress);
				} else {
					myAddress += addresses.get(0).getAdminArea()+"\n";
					myAddress += addresses.get(0).getLocality()+"\n";
					myAddress += addresses.get(0).getCountryName();
					Log.d("address city", ""+myAddress);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			mAddress.setText(myAddress);		

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}

	}
	public void loadUpdate() {
		updateTask = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						alarmScreen = new AlarmScreen();
//						Log.d("alarm screen ", "Called");
					}
				});
			}
		};
		t.schedule(updateTask, 10, 120000);
	}
}