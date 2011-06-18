package com.cse.geotagger;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyMapActivity extends MapActivity {
	private MapView mapView;
	private MapController mapController;
	List<Overlay> mapOverlays;
	Drawable drawable;
	MyItemizedOverlay itemizedOverlay;

	/*
	 * NOTE! The GeoPoints are measured in microdegrees. The lat and lng are
	 * measured in degrees. To convert, multiply by 1E6 (1,000,000)
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true); // Puts on screen zoom controls
												// (plus/minus buttons)
		mapController = mapView.getController();

		TakePhoto.dbAdapter.open();
		Cursor cursor = TakePhoto.dbAdapter.getAllItemsCursor();
		int dbLength = cursor.getCount();
		Log.i("MyDbAdapter", Integer.toString(cursor.getCount()));

		// get current lat and lng
		Double lat = TakePhoto.latitude, lng = TakePhoto.longitude;
		if (lat == 0 && lng == 0) {
			AlertDialog.Builder errorDialog = new AlertDialog.Builder(
					MyMapActivity.this);
			errorDialog
					.setMessage("Your location has not been determined.")
					.setTitle("Error!")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							}).show();
		} else {
			ArrayList<ImageItem> imageItemArray = new ArrayList<ImageItem>();
			// for(int i = 0; i < dbLength; i++){
			Cursor dbEntries = TakePhoto.dbAdapter.getAllItemsCursor();
			if (dbEntries.moveToFirst()) {
				while (dbEntries.moveToNext()) {
					int id = dbEntries.getInt(MyDbAdapter.ID_COLUMN);
					ImageItem item = TakePhoto.dbAdapter.getImageItem(id);
					imageItemArray.add(item);
					lat = item.getLatitude() * 1E6;
					lng = item.getLongitude() * 1E6;
					// This sets the an overlay item on the map (the map marker)
					mapOverlays = mapView.getOverlays();
					drawable = this.getResources().getDrawable(
							R.drawable.marker);
					itemizedOverlay = new MyItemizedOverlay(drawable);
					GeoPoint point = new GeoPoint(lat.intValue(),
							lng.intValue());
					OverlayItem overlayItem = new OverlayItem(point, "", "");
					itemizedOverlay.addOverlay(overlayItem);
					mapOverlays.add(itemizedOverlay);
					// setCenter sets where the center point is on the map and
					// zoom sets
					// how far away the zoom is with 1 being furthest, 21 being
					// closest.
					mapController.setCenter(point);
					mapController.setZoom(15);

				}
			}
			// }

		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// We are not displaying routes in our application, so this is simply
		// returning false. This method is required, which is why it's still
		// here.
		return false;
	}

	@Override
	public void onDestroy() {
		TakePhoto.dbAdapter.close();
		super.onDestroy();
	}
}
