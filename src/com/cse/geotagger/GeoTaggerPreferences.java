package com.cse.geotagger;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;

public class GeoTaggerPreferences extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.preferences);

		CheckBoxPreference gpsSetting = new CheckBoxPreference(this);
		CheckBoxPreference networkSetting = new CheckBoxPreference(this);

		if (gpsSetting.isEnabled()) {
			networkSetting.setChecked(false);
		}
	}
}
