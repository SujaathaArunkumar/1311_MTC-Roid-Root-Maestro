package com.example.mtc;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class AlarmScreen {
	MediaPlayer mPlayer;
	static Context context;
	static double destLat, destLng;
	GPSTracker gps;
	public AlarmScreen() {
		checkCurrentLocation();
	}

	public void checkCurrentLocation() {
		gps = new GPSTracker(context);
		// check if GPS enabled
		
		if (gps.canGetLocation()) {
			
			Log.d("destLat alarm screen",""+destLat);
			Log.d("destLng alarm screen",""+destLng);
			
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			
			Log.d("current-Lat alarm screen",""+latitude);
			Log.d("current-Lng alarm screen",""+longitude);
			
			if(destLat-latitude<.1000000||destLng-longitude<.1000000){
				mPlayer = MediaPlayer.create(context, R.raw.alert);
				//mPlayer.setLooping(true);
		        mPlayer.start();
		        Log.d("alert called", "alert called");
			}
			
		}
	}
}
