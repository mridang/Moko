package com.mridang.moko.fragments;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mridang.moko.R;
import com.mridang.moko.receivers.NotficationReceiver;

@TargetApi(11)
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    /*
     * @see android.app.Fragment#onResume()
     */
    @Override
    public void onResume() {

        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        SharedPreferences spePreferences = PreferenceManager.getDefaultSharedPreferences(this
                .getActivity().getApplicationContext());

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

        Integer intResource =
                getActivity().getResources()
                .getIdentifier("preferences",
                        "xml",
                        getActivity().getPackageName());

        addPreferencesFromResource(intResource);

    }

    /*
     * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
     */
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

        try {

            this.getActivity().sendBroadcast(new Intent(this.getActivity(), NotficationReceiver.class));
        	DateFormat dftFormat = new SimpleDateFormat("ddMMyyyy");
        	String strFilename = dftFormat.format(new Date());

            if(this.getActivity().getApplicationContext().getFileStreamPath(strFilename).exists()) {
            	this.getActivity().getApplicationContext().getFileStreamPath(strFilename).delete();
            }

        } catch (Exception e) {
           Log.e("fragments.SettingsFragment", "An error occurred when flusing the day's cache after a preference changed.", e);
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
     * @see android.app.Fragment#onPause()
     */
    @Override
    public void onPause() {

        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
        SharedPreferences spePreferences = PreferenceManager.getDefaultSharedPreferences(this
                .getActivity().getApplicationContext());

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