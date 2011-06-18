package com.cse.geotagger;

/**
 * An object that stores the crucial information for the photos: the longitude,
 * latitude and path.
 * 
 * @author Aaron
 * 
 */
public class ImageItem {
	/**
	 * The path of the image stored on the SD card.
	 */
	String mPath;
	/**
	 * The longitude of the image.
	 */
	double mLng;
	/**
	 * The latitude of the image.
	 */
	double mLat;

	/**
	 * The sole constructor that sets the path, lat and long.
	 */
	public ImageItem(String path, double lng, double lat) {
		mPath = path;
		mLng = lng;
		mLat = lat;
	}

	/**
	 * @return The path of the object.
	 */
	public String getPath() {
		return mPath;
	}

	/**
	 * @return The longitude of the object.
	 */
	public double getLongitude() {
		return mLng;
	}

	/**
	 * @return The latitude of the object.
	 */
	public double getLatitude() {
		return mLat;
	}

	/**
	 * Turns the information of the ImageItem into a string.
	 */
	@Override
	public String toString() {
		return "Path: " + mPath + "\nLatitude: " + mLat + "\nLongitude: "
				+ mLng;
	}
}
