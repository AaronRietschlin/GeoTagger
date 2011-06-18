package com.cse.geotagger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GeoTagger extends Activity {
	private static final int SETTINGS_ID = Menu.FIRST;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Creates listeners to tell what each button does. The first one
		// launches TakePhoto activity, the second launches MyMapActivity.
		Button takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
		takePhotoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent takePhotoActivity = new Intent(GeoTagger.this,
						TakePhoto.class);
				startActivity(takePhotoActivity);
			}
		});

		Button viewMapButton = (Button) findViewById(R.id.viewMapButton);
		viewMapButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent viewMapActivity = new Intent(GeoTagger.this,
						MyMapActivity.class);
				startActivity(viewMapActivity);
			}
		});

	}

	/**
	 *  Creates the menu options. There's only one: Settings.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, SETTINGS_ID, 0, R.string.settings);
		return true;
	}

	/**
	 * Tells what to do when an option is selected in the menu options. Settings
	 * is the only one which launches the PreferenceActivity.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case SETTINGS_ID:
			Intent settingsActivity = new Intent(getBaseContext(),
					GeoTaggerPreferences.class);
			startActivity(settingsActivity);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}