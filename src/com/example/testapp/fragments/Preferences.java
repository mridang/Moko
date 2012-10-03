package com.example.testapp.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.testapp.R;

public class Preferences extends PreferenceFragment  {

    /*
     * This will load the preferences from the preferences store
     * @see android.preference.PreferenceFragment
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		return;

	}

}
