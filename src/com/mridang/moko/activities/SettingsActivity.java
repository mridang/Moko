package com.mridang.moko.activities;

import java.util.List;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.mridang.moko.R;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

	/*
     * @see android.app.Fragment#onResume()
     */
    @TargetApi(9)
	@Override
    public void onResume() {

        super.onResume();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
        	return;
        
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        SharedPreferences spePreferences = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());

        EditTextPreference etpUsername = (EditTextPreference) getPreferenceScreen().findPreference(
                "torrentleech_username");
        if (!spePreferences.getString("torrentleech_username", "").isEmpty())
            etpUsername.setSummary(spePreferences.getString("torrentleech_username", ""));

        EditTextPreference etpPassword = (EditTextPreference) getPreferenceScreen().findPreference(
                "torrentleech_password");
        if (!spePreferences.getString("torrentleech_password", "").isEmpty())
            etpPassword.setSummary(spePreferences.getString("torrentleech_password", "")
                    .replaceAll("(?s).", "*"));

    }

    /*
     * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
     */
	@Override
	public void onCreate(Bundle bndState) {
		
		super.onCreate(bndState);

		if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.preferences);
		}

	}

    /*
     * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
     */
    @TargetApi(9)
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    	if (key.equals("torrentleech_username")) {
            Preference pref = findPreference(key);
            EditTextPreference etpUsername = (EditTextPreference) pref;
            if (etpUsername.getText().isEmpty()) {
            	etpUsername.setSummary(getResources().getString(R.string.enter_your_username));
            } else {
	            pref.setSummary(etpUsername.getText());
            }
    	}

    	if (key.equals("torrentleech_password")) {
            Preference pref = findPreference(key);
            EditTextPreference etpPassword = (EditTextPreference) pref;
            if (etpPassword.getText().isEmpty()) {
            	etpPassword.setSummary(getResources().getString(R.string.enter_your_password));
            } else {
	            pref.setSummary(etpPassword.getText().replaceAll("(?s).", "*"));
            }
    	}

    }

	/*
	 * @see android.preference.PreferenceActivity#onBuildHeaders(java.util.List)
	 */
	@Override
	public void onBuildHeaders(List<Header> lstHeaders) {

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
	  	    loadHeadersFromResource(R.xml.headers, lstHeaders);
		}
		
		super.onBuildHeaders(lstHeaders);

	}
    
    /*
     * @see android.app.Fragment#onPause()
     */
    @TargetApi(9)
	@Override
    public void onPause() {

        super.onPause();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
        	return;
        
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
        SharedPreferences spePreferences = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());

        EditTextPreference etpUsername = (EditTextPreference) getPreferenceScreen().findPreference(
                "torrentleech_username");
        if (!spePreferences.getString("torrentleech_username", "").isEmpty())
            etpUsername.setSummary(spePreferences.getString("torrentleech_username", ""));

        EditTextPreference etpPassword = (EditTextPreference) getPreferenceScreen().findPreference(
                "torrentleech_password");
        if (!spePreferences.getString("torrentleech_password", "").isEmpty())
            etpPassword.setSummary(spePreferences.getString("torrentleech_password", "")
                    .replaceAll("(?s).", "*"));
    }
	
}