package com.mridang.moko.search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.bugsense.trace.BugSenseHandler;
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.mridang.moko.R;
import com.mridang.moko.activities.SettingsActivity;
import com.mridang.moko.asynctasks.Downloader;
import com.mridang.moko.asynctasks.Viewer;
import com.mridang.moko.enums.Category;
import com.mridang.moko.fragments.SettingsFragment;
import com.mridang.moko.search.adapters.SearchResultsAdapter;
import com.mridang.moko.search.asynctasks.Finder;
import com.mridang.moko.search.managers.ResultsManager;
import com.mridang.moko.search.managers.ResultsManager.Filter;
import com.mridang.moko.search.managers.ResultsManager.Group;
import com.mridang.moko.search.managers.ResultsManager.Sort;

/*
 * This class is the search activity and contains all the necessary
 * UI logic for the activity.
 */
public class Search extends SherlockActivity {

    /*
     * The search results adapter that will power the ListView
     */
    public SearchResultsAdapter objAdapter;
    /*
     * The sorting option that should be used when showing results
     */
    public Sort srtSort = Sort.BY_SEEDERS;
    /*
     * The filtering option that should be used when showing results
     */
    public Filter fltFilter = Filter.ONLY_HQ;
    /*
     * The grouping option that should be used when showing results
     */
    public Group grpGroup = Group.BY_SITE;
    /*
     * The intent for searching for movies
     */
    public static final String SEARCH_MOVIES = "action.SEARCH_MOVIES";
    /*
     * The intent for searching for music
     */
    public static final String SEARCH_MUSIC = "action.SEARCH_MUSIC";
    /*
     * The intent for searching for games
     */
    public static final String SEARCH_GAMES = "action.SEARCH_GAMES";
    /*
     * The intent for searching for applications
     */
    public static final String SEARCH_APPS = "action.SEARCH_APPS";
    /*
     * The intent for searching for shows
     */
    public static final String SEARCH_SHOWS = "action.SEARCH_SHOWS";
    /*
     * The instance of the background asynchronous task
     */
    private Finder objFinder;

    /*
     * This will show the main screen
     * @see android.app.Activity.onCreate
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, "da66b24c");
        setContentView(R.layout.search);

        ActionBar abrAction = getSupportActionBar();
        abrAction.setDisplayHomeAsUpEnabled(false);

        handleIntent(getIntent());

    }

    /*
     * @see android.app.Activity#onStart()
     */
    @Override
    public void onStart() {

      super.onStart();
      EasyTracker.getInstance().activityStart(this);

    }

    /*
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconified(false);
        if (this.objAdapter == null) {
            searchView.requestFocusFromTouch();
        } else {
            searchView.clearFocus();
        }

        return true;

    }

    /*
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {

        if (this.objAdapter == null) {
            menu.findItem(R.id.sort).setEnabled(false);
            menu.findItem(R.id.filter).setEnabled(false);
            menu.findItem(R.id.group).setEnabled(false);
        } else {
            menu.findItem(R.id.sort).setEnabled(true);
            menu.findItem(R.id.filter).setEnabled(true);
            menu.findItem(R.id.group).setEnabled(true);

        }

        return true;
    }

    /*
     * This shows a full screen loading animation
     */
    public void showProgress() {

        LinearLayout lltLinear = (LinearLayout) findViewById(R.id.scroll);
        lltLinear.setVisibility(View.GONE);

        ProgressBar pbrProgress = (ProgressBar) findViewById(R.id.progress);
        pbrProgress.setVisibility(View.VISIBLE);

    }

    /*
     * This is the listener for the website button
     */
    public View.OnClickListener oclWebsite = new OnClickListener() {

        public void onClick(View vewView) {

            EasyTracker.getTracker().trackEvent("OnClicks", "Website", "View Webpage", null);
            new Viewer(Search.this).execute((URI) vewView.getTag());

        }

    };

    /*
     * This is the listener for the enqueue button
     */
    public View.OnClickListener oclEnqueue = new OnClickListener() {

        public void onClick(final View vewView) {

            EasyTracker.getTracker().trackEvent("OnClicks", "Enqueue", "Enqueue Torrent", null);

            AsyncHttpClient ahcClient = new AsyncHttpClient();
            PersistentCookieStore pscCookies = new PersistentCookieStore(Search.this);
            ahcClient.setCookieStore(pscCookies);
            String[] strTypes = new String[] { "application/octet-stream", "application/x-bittorrent" };
            ahcClient.get(((URI) vewView.getTag()).toString(), new BinaryHttpResponseHandler(strTypes) {

                /*
                 * @see com.loopj.android.http.BinaryHttpResponseHandler#onSuccess(byte[])
                 */
                @Override
                public void onSuccess(byte[] bytBytes) {

                    File filTorrent = null;
                    FileOutputStream fosOutput = null;

                    try {

                        filTorrent = File.createTempFile("xxx", ".torrent", Search.this.getCacheDir());
                        fosOutput = new FileOutputStream(filTorrent);
                        fosOutput.write(bytBytes);
                        fosOutput.close();

                        Intent ittEnqueue = new Intent(android.content.Intent.ACTION_VIEW);
                        ittEnqueue.setDataAndType(Uri.fromFile(filTorrent), "application/x-bittorrent");
                        Search.this.startActivity(ittEnqueue);

                    } catch (IOException e) {
                        sendFailureMessage(e, bytBytes);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(Search.this, getResources().getString(R.string.no_torrent_handlers), Toast.LENGTH_LONG).show();
                    } finally {

                        try {
                            if (fosOutput != null)
                                fosOutput.close();
                        } catch (IOException e) {
                            sendFailureMessage(e, bytBytes);
                        }

                    }

                }

                /*
                 * @see com.loopj.android.http.BinaryHttpResponseHandler#onFailure(java.lang.Throwable, byte[])
                 */
                @Override
                public void onFailure(Throwable e, byte[] bytBytes) {

                    Toast.makeText(Search.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    EasyTracker.getTracker().trackException(e.getMessage(), e, false);

                }

            });

        }

    };

    /*
     * Handles the intent passed to the activity i.e. search or view
     *
     * @param  ittIntent  the intent passed to the activity
     */
    public void handleIntent(Intent intent) {

        if  (Search.SEARCH_MOVIES.equals(intent.getAction())) {

            this.objFinder = new Finder(this, Category.MOVIE);
            EasyTracker.getTracker().trackEvent("OnClicks", "Search", "Find Movies", null);
            this.objFinder.execute(intent.getStringExtra(SearchManager.QUERY));
            return;

        } else if (Search.SEARCH_MUSIC.equals(intent.getAction())) {

            this.objFinder = new Finder(this, Category.ALBUM);
            EasyTracker.getTracker().trackEvent("OnClicks", "Search", "Find Albums", null);
            this.objFinder.execute(intent.getStringExtra(SearchManager.QUERY));
            return;

        } else if (Search.SEARCH_GAMES.equals(intent.getAction())) {

            this.objFinder = new Finder(this, Category.GAME);
            EasyTracker.getTracker().trackEvent("OnClicks", "Search", "Find Games", null);
            this.objFinder.execute(intent.getStringExtra(SearchManager.QUERY));
            return;

        } else if (Search.SEARCH_SHOWS.equals(intent.getAction())) {

            this.objFinder = new Finder(this, Category.SHOW);
            EasyTracker.getTracker().trackEvent("OnClicks", "Search", "Find Shows", null);
            this.objFinder.execute(intent.getStringExtra(SearchManager.QUERY));
            return;

        } else if (Search.SEARCH_APPS.equals(intent.getAction())) {

            this.objFinder = new Finder(this, Category.APP);
            EasyTracker.getTracker().trackEvent("OnClicks", "Search", "Find Applications", null);
            this.objFinder.execute(intent.getStringExtra(SearchManager.QUERY));
            return;

        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            this.objFinder = new Finder(this, Category.EVERYTHING);
            EasyTracker.getTracker().trackEvent("OnClicks", "Search", "Find Everything", null);
            this.objFinder.execute(intent.getStringExtra(SearchManager.QUERY));
            return;

        }

        return;

    }

    /*
     * @see android.app.Activity.onOptionsItemSelected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem itmMenuitem) {

        if (itmMenuitem.getItemId() == R.id.search) {

            onSearchRequested();
            return true;

        } else if (itmMenuitem.getItemId() == R.id.group) {

            final ArrayList<CharSequence> lstChoices = new ArrayList<CharSequence>();

            for (ResultsManager.Group grpGroup : ResultsManager.Group.values()) {

                lstChoices
                        .add(getResources()
                                .getString(
                                        getApplicationContext()
                                                .getResources()
                                                .getIdentifier(
                                                        grpGroup.name().toLowerCase(),
                                                        "string",
                                                        getApplicationContext()
                                                                .getApplicationInfo().packageName)));

            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(itmMenuitem.getTitle());

            builder.setSingleChoiceItems(lstChoices.toArray(new CharSequence[lstChoices.size()]),
                    Search.this.grpGroup.ordinal(), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int item) {
                            Search.this.grpGroup = Group.values()[item];
                            Search.this.objAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    lstChoices.get(item), Toast.LENGTH_SHORT)
                                    .show();
                        }

                    });

            AlertDialog alert = builder.create();
            alert.show();

            return true;

        } else if (itmMenuitem.getItemId() == R.id.sort) {

            final ArrayList<CharSequence> lstChoices = new ArrayList<CharSequence>();

            for (ResultsManager.Sort srtSort : ResultsManager.Sort.values()) {

                lstChoices
                .add(getResources()
                        .getString(
                                getApplicationContext()
                                        .getResources()
                                        .getIdentifier(
                                                srtSort.name().toLowerCase(),
                                                "string",
                                                getApplicationContext()
                                                        .getApplicationInfo().packageName)));

            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(itmMenuitem.getTitle());

            builder.setSingleChoiceItems(lstChoices.toArray(new CharSequence[lstChoices.size()]),
                    Search.this.srtSort.ordinal(), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int item) {
                            Search.this.srtSort = Sort.values()[item];
                            Search.this.objAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    lstChoices.get(item), Toast.LENGTH_SHORT)
                                    .show();
                        }

                    });

            AlertDialog alert = builder.create();
            alert.show();

            return true;

        } else if (itmMenuitem.getItemId() == R.id.filter) {

            final ArrayList<CharSequence> lstChoices = new ArrayList<CharSequence>();

            for (ResultsManager.Filter fltFilter : ResultsManager.Filter.values()) {

                lstChoices
                .add(getResources()
                        .getString(
                                getApplicationContext()
                                        .getResources()
                                        .getIdentifier(
                                                fltFilter.name().toLowerCase(),
                                                "string",
                                                getApplicationContext()
                                                        .getApplicationInfo().packageName)));

            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(itmMenuitem.getTitle());

            builder.setSingleChoiceItems(lstChoices.toArray(new CharSequence[lstChoices.size()]),
                    Search.this.fltFilter.ordinal(), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int item) {
                            Search.this.fltFilter = Filter.values()[item];
                            Search.this.objAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    lstChoices.get(item), Toast.LENGTH_SHORT)
                                    .show();
                        }

                    });

            AlertDialog alert = builder.create();
            alert.show();

            return true;

        } else if (itmMenuitem.getItemId() == R.id.settings) {

            Intent ittIntent = new Intent(this, SettingsActivity.class );
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                ittIntent.putExtra(SherlockPreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsFragment.class.getName());
            }
            ittIntent.putExtra(SherlockPreferenceActivity.EXTRA_NO_HEADERS, true );
            startActivity(ittIntent);

        }

        return false;

    }

    /*
     * This is the listener for the share button
     */
    public View.OnClickListener oclShare = new OnClickListener() {

        public void onClick(final View vewView) {

            EasyTracker.getTracker().trackEvent("OnClicks", "Share", "Share Torrent", null);

            AsyncHttpClient ahcClient = new AsyncHttpClient();
            PersistentCookieStore pscCookies = new PersistentCookieStore(Search.this);
            ahcClient.setCookieStore(pscCookies);
            ahcClient.get(((URI) vewView.getTag()).toString(), new AsyncHttpResponseHandler() {

                /*
                 * @see com.loopj.android.http.BinaryHttpResponseHandler#onSuccess(byte[])
                 */
                @Override
                public void onSuccess(String strResponse) {

                    File filTorrent = null;
                    FileOutputStream fosOutput = null;

                    try {

                        filTorrent = File.createTempFile("xxx", ".html", Search.this.getCacheDir());
                        fosOutput = new FileOutputStream(filTorrent);
                        fosOutput.write(strResponse.getBytes());
                        fosOutput.close();

                        Intent ittShare = new Intent("android.intent.action.SEND");
                        ittShare.setType("text/plain");
                        ittShare.putExtra("android.intent.extra.TEXT", ((URI) vewView.getTag()).toString());
                        startActivity(Intent.createChooser(ittShare, "Share using"));

                    } catch (IOException e) {
                        sendFailureMessage(e, strResponse);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(Search.this, getResources().getString(R.string.no_torrent_handlers), Toast.LENGTH_LONG).show();
                    } finally {

                        try {
                            if (fosOutput != null)
                                fosOutput.close();
                        } catch (IOException e) {
                            sendFailureMessage(e, strResponse);
                        }

                    }

                }

                /*
                 * @see com.loopj.android.http.BinaryHttpResponseHandler#onFailure(java.lang.Throwable, byte[])
                 */
                @Override
                public void onFailure(Throwable e, String strResponse) {

                    Toast.makeText(Search.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    EasyTracker.getTracker().trackException(e.getMessage(), e, false);

                }

            });

        }

    };

    /*
     * This is the listener for the download button
     */
    public View.OnClickListener oclDownload = new OnClickListener() {

        public void onClick(View vewView) {

            EasyTracker.getTracker().trackEvent("OnClicks", "Download", "Download Torrent", null);
            new Downloader(Search.this).execute(Uri.parse(((URI) vewView.getTag()).toString()));

        }

    };

    /*
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);

    }

    /*
     * @see com.actionbarsherlock.app.SherlockActivity#onStop()
     */
    @Override
    public void onStop() {

      super.onStop();
      EasyTracker.getInstance().activityStop(this);

    }

    /*
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy () {

        super.onDestroy();

        if (this.objFinder != null) {
            if (this.objFinder.getStatus() != Status.FINISHED) {
                this.objFinder.cancel(true);
            }
        }

    }

    /*
     * This hides the full screen loading animation
     */
    public void hideProgress() {

        ProgressBar pbrProgress = (ProgressBar) findViewById(R.id.progress);
        pbrProgress.setVisibility(View.GONE);

        LinearLayout lltLinear = (LinearLayout) findViewById(R.id.scroll);
        lltLinear.setVisibility(View.VISIBLE);

    }

    /*
     * This will show a dialog with the filtering options
     */
    public void showFilters() {

        final ArrayList<CharSequence> lstChoices = new ArrayList<CharSequence>();

        for (ResultsManager.Filter fltFilter : ResultsManager.Filter.values()) {

            lstChoices
            .add(getResources()
                    .getString(
                            getApplicationContext()
                                    .getResources()
                                    .getIdentifier(
                                            fltFilter.name().toLowerCase(),
                                            "string",
                                            getApplicationContext()
                                                    .getApplicationInfo().packageName)));

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.nothing_after_filtering);

        builder.setSingleChoiceItems(lstChoices.toArray(new CharSequence[lstChoices.size()]),
                Search.this.fltFilter.ordinal(), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        Search.this.fltFilter = Filter.values()[item];
                        Search.this.objAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                lstChoices.get(item), Toast.LENGTH_SHORT)
                                .show();
                    }

                });

        AlertDialog alert = builder.create();
        alert.show();

    }

}