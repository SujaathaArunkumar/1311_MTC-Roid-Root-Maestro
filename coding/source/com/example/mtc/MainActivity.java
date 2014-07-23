package com.example.mtc;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
Button mSearch,mFare,mFavouritePlace;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSearch = (Button)findViewById(R.id.search);
		mFare = (Button)findViewById(R.id.fare);
		mFavouritePlace = (Button)findViewById(R.id.favouriteplace);
		
		mSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,SearchActivity.class));	
			}
		});
		mFare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this,FareActivity.class));	
				
			}
		});
		
		mFavouritePlace.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,FavouritePlaceActivity.class));
				
			}
		});
	}

}
