package com.cse.geotagger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDbAdapter {
	// Basic Database information
	private static final String DATABASE_NAME = "imageDB.db";
	private static final String DATABASE_TABLE = "imageItems";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase db;
	private final Context context;

	// Column names:
	public static final String KEY_ID = "_id";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";
	public static final String KEY_PATH = "path";

	// Used to identify the columns in other classes.
	public static final int ID_COLUMN = 0;
	public static final int LAT_COLUMN = 1;
	public static final int LNG_COLUMN = 2;
	public static final int PATH_COLUMN = 3;

	// instantiation of the SQLiteOpenHelper below
	private geoTagDbOpenHelper dbHelper;

	public MyDbAdapter(Context _context) {
		context = _context;
		dbHelper = new geoTagDbOpenHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	// Simply closes the database
	public void close() {
		db.close();
	}

	// Opens the database
	public void open() throws SQLiteException {
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbHelper.getReadableDatabase();
		}
	}

	// Insert a new Image into Database
	public long insertImage(ImageItem image) {
		ContentValues newImageValues = new ContentValues();
		newImageValues.put(KEY_LAT, image.getLatitude());
		newImageValues.put(KEY_LNG, image.getLongitude());
		newImageValues.put(KEY_PATH, image.getPath());
		// Inserts the new row into the database
		return db.insert(DATABASE_TABLE, null, newImageValues);
	}

	// Remove an image based on its index
	public boolean removeImage(long rowIndex) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + rowIndex, null) > 0;
	}

	// Update an ImageItem that is already in the database
	public boolean updateImagePath(long rowIndex, String path) {
		ContentValues newValue = new ContentValues();
		newValue.put(KEY_PATH, path);
		return db.update(DATABASE_TABLE, newValue, KEY_ID + "=" + rowIndex,
				null) > 0;
	}

	// BEGIN QUERIES

	/**
	 * Returns all the items in the database in a Cursor Object.
	 */
	public Cursor getAllItemsCursor() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_LAT,
				KEY_LNG, KEY_PATH }, null, null, null, null, null);
	}

	public Cursor setCursorImageItem(long index) throws SQLException {
		Cursor result = db.query(true, DATABASE_TABLE, new String[] { KEY_ID,
				KEY_PATH }, KEY_ID + "=" + index, null, null, null, null, null);
		if ((result.getCount() == 0) || !result.moveToFirst()) {
			throw new SQLException("No items found for row " + index);
		}
		return result;
	}

	/**
	 * Returns the ImageItem at the given index in the database.
	 */
	public ImageItem getImageItem(long index) throws SQLException {
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ID,
				KEY_LAT, KEY_LNG, KEY_PATH }, KEY_ID + "=" + index, null, null,
				null, null, null);
		if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
			throw new SQLException("No image item found for row #" + index);
		}
		String path = cursor.getString(PATH_COLUMN);
		double lng = cursor.getDouble(LNG_COLUMN);
		double lat = cursor.getDouble(LAT_COLUMN);
		return new ImageItem(path, lng, lat);
	}

	// This simplifies version management:
	private static class geoTagDbOpenHelper extends SQLiteOpenHelper {
		public geoTagDbOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// SQL Statement to create a new database.
		private static final String DATABASE_CREATE = "create table "
				+ DATABASE_TABLE + " (" + KEY_ID
				+ " integer primary key autoincrement, " + KEY_LAT
				+ " double, " + KEY_LNG + " double, " + KEY_PATH + " text);";

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion
					+ " to " + _newVersion
					+ ", which will destroy all old data");
			// Drop the old table.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			// Create a new one.
			onCreate(_db);
		}
	}
}