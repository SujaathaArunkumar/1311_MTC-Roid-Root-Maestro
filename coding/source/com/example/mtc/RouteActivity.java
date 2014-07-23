package com.example.mtc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtc.utils.Constants;
import com.example.mtc.utils.XMLfunctions;

public class RouteActivity extends Activity {
	Button mGetLocation;
	TextView mHour;
	ListView mRouteList;
	String[] mBusRoutes;// =
						// {"Thirumangalam","Collector Nagar","Golden Flats","Mogappair","Ambattur"};
	ArrayAdapter<String> arrayAdapter;
	String source, destination, busno;
    double mLat,mLng;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route);
		mRouteList = (ListView) findViewById(R.id.routelist);
		mHour = (TextView) findViewById(R.id.hour);
		mGetLocation = (Button) findViewById(R.id.getlocation);
		Bundle sender = getIntent().getExtras();
		source = sender.getString("source");
		destination = sender.getString("destination");
		busno = sender.getString("busno");
		loadResponse(source, destination, busno);
        loadLatLng(destination);
		mGetLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent inte = new Intent(RouteActivity.this,AndroidGPSTrackingActivity.class);
				inte.putExtra("Lat", mLat);
				inte.putExtra("Lng", mLng);
//				Log.d("Lat",""+mLat);
//				Log.d("Lng",""+mLng);
				startActivity(inte);
			}
		});
		turnGPSOn();
	}

	public void loadResponse(String mSource, String mDestination, String mBusno) {

		String url = Constants.ROUTE_API + "source=" + mSource + "&Dest="
				+ mDestination + "&BusNo=" + mBusno;
		url = url.replace(" ", "%20");
		Log.d("url ", "" + url);

		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute(new String[] { url.toString() });
	}

	public class DownloadWebPageTask extends AsyncTask<String, Void, String> {

		final ProgressDialog dialog = new ProgressDialog(RouteActivity.this);
		private volatile boolean running = true;

		public DownloadWebPageTask() {
			running = true;
		}

		@Override
		protected void onPreExecute() {

			this.dialog.setCancelable(true);
			this.dialog.setMessage("Loading data...");
			this.dialog.show();
			this.dialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							cancel(true);
						}
					});
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			while (running) {
				response = XMLfunctions.getXML(urls[0]);
				Log.d("response", "" + response);
				running = false;
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {

			// Log.d("result", "" + result);
			if (!result.startsWith("<html>")) {
				Document doc = XMLfunctions.XMLfromString(result);

				int numResults = XMLfunctions.numResults(doc);
				Log.d("numResult", "" + numResults);
				if (numResults == -1 || numResults == 0) {

					Toast toast = Toast.makeText(getApplicationContext(),
							"Cannot Connect to Server", Toast.LENGTH_LONG);
					toast.show();
				} else {

					NodeList nodes = doc.getElementsByTagName("Database");
					Element d = (Element) nodes.item(0);
					String status = (XMLfunctions.getValue(d, "Message"));
					String hour = (XMLfunctions.getValue(d, "Hour"));
					mHour.setText(hour + " - min ");
					if (status.equals("Success")) {
						NodeList nodes_list = doc.getElementsByTagName("Node");
						int nodesSize = nodes_list.getLength();
						Log.d("nodes size", "" + nodesSize);
						if (nodesSize > 0) {
							mBusRoutes = new String[nodesSize];
							for (int i = 0; i < nodesSize; i++) {
								Element e = (Element) nodes_list.item(i);
								String source = (XMLfunctions.getValue(e,
										"Stop"));
								mBusRoutes[i] = source;
							}
						}
						arrayAdapter = new ArrayAdapter<String>(
								RouteActivity.this, R.layout.listrow,
								R.id.textView, mBusRoutes);
						mRouteList.setAdapter(arrayAdapter);
						mGetLocation.setVisibility(View.VISIBLE);
					}
				}
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			} else {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				Toast toast = Toast.makeText(getApplicationContext(),
						"Cannot Connect to Server", Toast.LENGTH_SHORT);
				toast.show();
			}
		}

		@Override
		protected void onCancelled() {
			running = false;
		}
	}

	public void loadLatLng(String mDestination) {

		String url = Constants.LAT_LANG_API + "source=" + mDestination;
		url = url.replace(" ", "%20");
		Log.d("url ", "" + url);

		DownloadWebPageTask01 task = new DownloadWebPageTask01();
		task.execute(new String[] { url.toString() });
	}

	public class DownloadWebPageTask01 extends AsyncTask<String, Void, String> {

		final ProgressDialog dialog = new ProgressDialog(RouteActivity.this);
		private volatile boolean running = true;

		public DownloadWebPageTask01() {
			running = true;
		}

		@Override
		protected void onPreExecute() {

			this.dialog.setCancelable(true);
			this.dialog.setMessage("Loading data...");
			this.dialog.show();
			this.dialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							cancel(true);
						}
					});
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			while (running) {
				response = XMLfunctions.getXML(urls[0]);
				Log.d("response", "" + response);
				running = false;
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {

			// Log.d("result", "" + result);
			if (!result.startsWith("<html>")) {
				Document doc = XMLfunctions.XMLfromString(result);

				int numResults = XMLfunctions.numResults(doc);
				Log.d("numResult", "" + numResults);
				if (numResults == -1 || numResults == 0) {

					Toast toast = Toast.makeText(getApplicationContext(),
							"Cannot Connect to Server", Toast.LENGTH_LONG);
					toast.show();
				} else {

					NodeList nodes = doc.getElementsByTagName("Database");
					Element d = (Element) nodes.item(0);
					String status = (XMLfunctions.getValue(d, "Message"));
					
					if (status.equals("Success")) {
						NodeList nodes_list = doc.getElementsByTagName("Node");
						Element e = (Element) nodes_list.item(0);
						mLat = Double.parseDouble((XMLfunctions.getValue(e, "Lat")));
						mLng = Double.parseDouble((XMLfunctions.getValue(e, "Long")));
					}
				}
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			} else {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				Toast toast = Toast.makeText(getApplicationContext(),
						"Cannot Connect to Server", Toast.LENGTH_SHORT);
				toast.show();
			}
		}

		@Override
		protected void onCancelled() {
			running = false;
		}
	}

	
	public boolean isOnline(Context c) {
		ConnectivityManager cm = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected())
			return true;
		else
			return false;
	}
	public void turnGPSOn(){
        try
        {       
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);       
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
        }
        catch (Exception e) {
           
        }
    }
}
