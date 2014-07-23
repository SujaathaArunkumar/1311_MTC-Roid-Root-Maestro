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
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class BusNumberActivity extends Activity{
	Spinner mBuses;
	Button mBusRoute;
	String[] mBusNumbers;// = {{"D70","M70","M70A","70M"},{"D70","M70","M70A","70M"},{"D70","M70","M70A","70M"},{"D70","M70","M70A","70M"},{"147A","M70","M70A","147B"},{"19B","5B","12B","147M"},{"27L","27H","7M","7H"}};
	ArrayAdapter<String> arrayAdapter;
	String mSource,mDestination;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busnumbers);
		
		mBuses = (Spinner) findViewById(R.id.buses);
		mBusRoute = (Button)findViewById(R.id.getroute);
		Bundle sender = getIntent().getExtras();
		mSource = sender.getString("source");
		mDestination = sender.getString("destination");
		
		mBuses.setPrompt("Choose a Bus");
		
		loadResponse(mSource, mDestination);
		
		mBusRoute.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String busno = arrayAdapter.getItem(mBuses.getSelectedItemPosition());
				Intent mIntent = new Intent(BusNumberActivity.this,RouteActivity.class);
				mIntent.putExtra("source", mSource);
				mIntent.putExtra("destination", mDestination);
				mIntent.putExtra("busno", busno);
				startActivity(mIntent);
			}
		});
	}

	public void loadResponse(String mSource,String mDestination) {

		String url = Constants.BUSNO_API+"source="+mSource+"&Dest="+mDestination;
		url = url.replace(" ", "%20");
		Log.d("url ", "" + url);

		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute(new String[] { url.toString() });
	}

	public class DownloadWebPageTask extends AsyncTask<String, Void, String> {

		final ProgressDialog dialog = new ProgressDialog(BusNumberActivity.this);
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

			//Log.d("result", "" + result);
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
							mBusNumbers=new String[nodesSize];
							for (int i = 0; i < nodesSize; i++) {
								Element e = (Element) nodes_list.item(i);
								String source = (XMLfunctions.getValue(e,"BusNo"));								
								mBusNumbers[i]=source;							
							}
						}
						arrayAdapter = new ArrayAdapter<String>(BusNumberActivity.this, android.R.layout.simple_spinner_item,mBusNumbers);		
						arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
						mBuses.setAdapter(arrayAdapter);						
						
					}else{
						Toast.makeText(getApplicationContext(),"Cannot Connect to Server", Toast.LENGTH_SHORT).show();
					}
					
				}
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			} else {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				Toast toast = Toast.makeText(getApplicationContext(),"Cannot Connect to Server", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		@Override
		protected void onCancelled() {
			running = false;
		}
	}

	public boolean isOnline(Context c) {
		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected())
			return true;
		else
			return false;
	}
	
}
