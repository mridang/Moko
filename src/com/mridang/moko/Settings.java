package com.mridang.moko;

import com.actionbarsherlock.app.SherlockActivity;
import com.mridang.moko.fragments.Preferences;

import android.os.Bundle;

public class Settings extends SherlockActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Preferences())
                .commit();
    }
}