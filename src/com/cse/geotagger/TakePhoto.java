package com.cse.geotagger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TakePhoto extends Activity {

	private static final int CAMERA_PIC_REQUEST = 0;
	public static String networkPref = "1";
	Location location;
	public static double latitude;
	public static double longitude;

	public static MyDbAdapter dbAdapter; // The Database adapter that allows us
											// to access the database.

	// LocationListener that checks for updates to the GPS location:
	private final LocationListener mylocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			getLocation(location);
		}

		public void onProviderDisabled(String provider) {
			getLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.takephoto);

		// Opens the database for use.
		dbAdapter = new MyDbAdapter(this);
		dbAdapter.open();

		// This sets the visibility of some of the UI objects
		TextView locationView = (TextView) findViewById(R.id.latitudeLabel);
		locationView.setVisibility(View.VISIBLE);
		ImageView imageResult = (ImageView) findViewById(R.id.imageView1);
		imageResult.setVisibility(View.GONE);

		// Begin getting the location.
		String serviceString = Context.LOCATION_SERVICE;
		LocationManager locationManager = (LocationManager) getSystemService(serviceString);

		getPreferences();
		LocationProvider provider;
		String providerName = "";
		// If GPS option is selected
		if (networkPref.compareTo("1") == 0) {
			System.out.println("TESTING - GPS");
			providerName = LocationManager.GPS_PROVIDER;
			provider = locationManager.getProvider(providerName);
		} else { // If Network Provider option is selected
			System.out.println("TESTING - Network");
			providerName = LocationManager.NETWORK_PROVIDER;
			provider = locationManager.getProvider(providerName);
		}
		location = locationManager.getLastKnownLocation(providerName);
		boolean locDetermined = getLocation(location);
		locationManager.requestLocationUpdates(providerName, 500, 1,
				mylocationListener);

		Button saveButton = (Button) findViewById(R.id.savePicture);
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		if (locDetermined) {
			// Calls the camera applicaction.
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
		} else {
			// If location was not determined, then the camera is not launched.
			TextView locationText = (TextView) findViewById(R.id.latitudeLabel);
			locationText.setVisibility(View.GONE);
			AlertDialog.Builder locationDialog = new AlertDialog.Builder(
					TakePhoto.this);
			locationDialog
					.setMessage(
							"No location found! The camera will not be launched unless the location is found. Please try again. ")
					.setTitle("Error!")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent takePhotoActivity = new Intent(
											TakePhoto.this, GeoTagger.class);
									startActivity(takePhotoActivity);
								}
							}).show();
			cancelButton.setVisibility(View.GONE);
			saveButton.setVisibility(View.GONE);
		}

		// The cancel button that does not save any information.
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent takePhotoActivity = new Intent(TakePhoto.this,
						GeoTagger.class);
				startActivity(takePhotoActivity);
			}
		});

		// The save button which saves the information about the photo.
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Save the image
				saveImage();
				Intent takePhotoActivity = new Intent(TakePhoto.this,
						GeoTagger.class);
				startActivity(takePhotoActivity);
			}
		});

	}

	Uri imageUri = null;
	private static Bitmap thumbnail;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			// do something
			if (data != null) {
				if (data.hasExtra("data")) {
					// Retrieves the image from the results of the activity and
					// sets the image as a thumbnail at the top of the screen.
					thumbnail = data.getParcelableExtra("data");
					ImageView image = (ImageView) findViewById(R.id.imageView1);
					image.setVisibility(View.VISIBLE);
					EditText textBox = (EditText) findViewById(R.id.nameImage);
					textBox.setVisibility(View.VISIBLE);
					image.setImageBitmap(thumbnail);
				}
			}
			getLocation(location);
		}
	}

	// Retrieves the settings
	private void getPreferences() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		networkPref = prefs.getString("locationMethod", "1");

	}

	// Does the work of finding the location.
	private boolean getLocation(Location location) {
		TextView locationView = (TextView) findViewById(R.id.latitudeLabel);
		String locText = "Your Location is: \n";
		boolean locationDetermined = false;
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			locText = locText + "\tLatitude: " + Double.toString(latitude)
					+ "\n\tLongitude: " + Double.toString(longitude);
			locationDetermined = true;
		} else {
			// If location was not determined, an alert box pops up with an
			// error message.
			locText = "Error: No location found! Make sure GPS is on or you have signal and try again!";
			locationDetermined = false;
		}
		locationView.setText(locText);
		return locationDetermined;
	}

	/** Called when the activity is first created. */
	@Override
	public void onPause() {
		TextView locationView = (TextView) findViewById(R.id.latitudeLabel);
		locationView.setText("");
		super.onPause();
	}

	private void saveImage() {
		EditText textInput = (EditText) findViewById(R.id.nameImage);
		String imageName = textInput.getText().toString() + ".jpg";
		Log.d("TAKEPHOTO", imageName);
		String path = "/sdcard/csePics";

		// create a File object for the parent directory
		File imageDirectory = new File(path);
		// have the object build the directory structure, if needed.
		imageDirectory.mkdirs();
		// create a File object for the output file
		File outputFile = new File(imageDirectory, imageName);
		// now attach the OutputStream to the file object, instead of a String
		// representation
		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ContentValues values = new ContentValues(3);
		values.put(Media.DISPLAY_NAME, imageName + ".jpg");
		values.put(Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.Images.Media.DATA, path + "/" + imageName);
		Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI,
				values);
		try {
			OutputStream outStream = getContentResolver().openOutputStream(uri);
			thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
			outStream.close();
		} catch (Exception e) {
			Log.e("TakePhoto", "exception while writing image", e);
		}

		ImageItem imageItem = new ImageItem(path + "/" + imageName, longitude,
				latitude);
		dbAdapter.insertImage(imageItem);
		Log.d("TAKEPHOTO", imageItem.toString());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dbAdapter.close(); // Closes the DB
	}

}