package com.example.mtc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.mtc.utils.Constants;
import com.example.mtc.utils.XMLfunctions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FavouritePlaceActivity extends Activity {
	TextView mFavouriteSpot;
	Button mGetFavouriteSpot;
	Spinner mSourcePoint;
	String[] mSource;
	ArrayAdapter<String> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favouriteplace);
		mFavouriteSpot = (TextView) findViewById(R.id.favouritespot);
		mGetFavouriteSpot = (Button) findViewById(R.id.getfavouritespot);
		mSourcePoint = (Spinner) findViewById(R.id.source);

		mSourcePoint.setPrompt("Choose a Place");
		loadResponse();
		
		mGetFavouriteSpot.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String place = arrayAdapter.getItem(mSourcePoint.getSelectedItemPosition());
				loadResponse01(place);				
			}
		});
	}

	public void loadResponse() {

		String url = Constants.SOURCE_API;
		url = url.replace(" ", "%20");
		Log.d("url ", "" + url);

		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute(new String[] { url.toString() });
	}

	public class DownloadWebPageTask extends AsyncTask<String, Void, String> {

		final ProgressDialog dialog = new ProgressDialog(
				FavouritePlaceActivity.this);
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
					if (status.equals("Success")) {
						NodeList nodes_list = doc.getElementsByTagName("Node");
						int nodesSize = nodes_list.getLength();
						Log.d("nodes size", "" + nodesSize);
						if (nodesSize > 0) {
							mSource = new String[nodesSize];
							for (int i = 0; i < nodesSize; i++) {
								Element e = (Element) nodes_list.item(i);
								String source = (XMLfunctions.getValue(e,
										"Source"));
								mSource[i] = source;
							}
						}
						arrayAdapter = new ArrayAdapter<String>(
								FavouritePlaceActivity.this,
								android.R.layout.simple_spinner_item, mSource);
						arrayAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						mSourcePoint.setAdapter(arrayAdapter);

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

	public void loadResponse01(String mSur) {

		String url = Constants.FAVOURITE_PLACE_API+"source="+mSur;
		url = url.replace(" ", "%20");
		Log.d("url ", "" + url);

		DownloadWebPageTask01 task = new DownloadWebPageTask01();
		task.execute(new String[] { url.toString() });
	}

	public class DownloadWebPageTask01 extends AsyncTask<String, Void, String> {

		final ProgressDialog dialog = new ProgressDialog(
				FavouritePlaceActivity.this);
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
					String favourite = (XMLfunctions.getValue(d, "Favourite"));
					mFavouriteSpot.setText(favourite);
					}else{
						mFavouriteSpot.setText("");
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
}
