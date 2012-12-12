package com.mridang.moko;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
import com.bugsense.trace.BugSenseHandler;
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.mridang.moko.activities.SettingsActivity;
import com.mridang.moko.adapters.TrendingTorrentsAdapter;
import com.mridang.moko.asynctasks.Downloader;
import com.mridang.moko.asynctasks.Scraper;
import com.mridang.moko.asynctasks.Viewer;
import com.mridang.moko.fragments.SettingsFragment;
import com.mridang.moko.managers.TrendingManager;
import com.mridang.moko.managers.TrendingManager.Filter;
import com.mridang.moko.managers.TrendingManager.Group;
import com.mridang.moko.managers.TrendingManager.Sort;
import com.mridang.moko.search.Search;
import com.mridang.moko.search.managers.ResultsManager;

public class Trend extends SherlockActivity {

    /*
     * The trending torrents adapter that will power the listview
     */
    public TrendingTorrentsAdapter objAdapter;
    /*
     * The sorting option that should be used when showing results
     */
    public Sort srtSort = Sort.BY_SEEDERS;
    /*
     * The filtering option that should be used when showing results
     */
    public Filter fltFilter = Filter.ONLY_PUBLIC;
    /*
     * The grouping option that should be used when showing results
     */
    public Group grpGroup = Group.BY_TYPE;
    /*
     * The instance of the background asynchronous task
     */
    private Scraper objScraper;

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
            new Viewer(Trend.this).execute((URI) vewView.getTag());

        }

    };

    /*
     * This is the listener for the enqueue button
     */
    public View.OnClickListener oclEnqueue = new OnClickListener() {

        public void onClick(View vewView) {

            EasyTracker.getTracker().trackEvent("OnClicks", "Enqueue", "Enqueue Torrent", null);

            AsyncHttpClient ahcClient = new AsyncHttpClient();
            PersistentCookieStore pscCookies = new PersistentCookieStore(Trend.this);
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

                        filTorrent = File.createTempFile("xxx", ".torrent", Trend.this.getCacheDir());
                        fosOutput = new FileOutputStream(filTorrent);
                        fosOutput.write(bytBytes);
                        fosOutput.close();

                        Intent ittEnqueue = new Intent(android.content.Intent.ACTION_VIEW);
                        ittEnqueue.setDataAndType(Uri.fromFile(filTorrent), "application/x-bittorrent");
                        Trend.this.startActivity(ittEnqueue);

                    } catch (IOException e) {
                        sendFailureMessage(e, bytBytes);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(Trend.this, getResources().getString(R.string.no_torrent_handlers), Toast.LENGTH_LONG).show();
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

                    Toast.makeText(Trend.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    EasyTracker.getTracker().trackException(e.getMessage(), e, false);

                }

            });

        }

    };

    /*
     * @see android.app.Activity.onCreate
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, "da66b24c");
        setContentView(R.layout.trend);

        ActionBar abrAction = getSupportActionBar();
        abrAction.setDisplayHomeAsUpEnabled(false);

        scrapeTorrents();

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
     * This method scrapes the sites for the trending torrents
     */
    public void scrapeTorrents() {

        this.objScraper = new Scraper(this);
        this.objScraper.execute();

    }

    /*
     * @see android.app.Activity.onOptionsItemSelected
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem itmMenuitem) {

        if (itmMenuitem.getItemId() == R.id.search) {

            startActivity(new Intent(getBaseContext(), Search.class));
            return true;

        } else if (itmMenuitem.getItemId() == R.id.group) {

            final ArrayList<CharSequence> lstChoices = new ArrayList<CharSequence>();

            for (TrendingManager.Group grpGroup : TrendingManager.Group.values()) {

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
                    Trend.this.grpGroup.ordinal(), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int item) {
                            Trend.this.grpGroup = Group.values()[item];
                            Trend.this.objAdapter.notifyDataSetChanged();
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

            for (TrendingManager.Sort srtSort : TrendingManager.Sort.values()) {

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
                    Trend.this.srtSort.ordinal(), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int item) {
                            Trend.this.srtSort = Sort.values()[item];
                            Trend.this.objAdapter.notifyDataSetChanged();
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

            for (TrendingManager.Filter fltFilter : TrendingManager.Filter.values()) {

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
                    Trend.this.fltFilter.ordinal(), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int item) {
                            Trend.this.fltFilter = Filter.values()[item];
                            Trend.this.objAdapter.notifyDataSetChanged();
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
            return true;

        }

        return false;

    }

    /*
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.trend, menu);

        return true;

    }

    /*
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {

        if (this.objAdapter == null) {
            menu.findItem(R.id.sort).setVisible(false);
            menu.findItem(R.id.filter).setVisible(false);
            menu.findItem(R.id.group).setVisible(false);
        } else {
            menu.findItem(R.id.sort).setVisible(true);
            menu.findItem(R.id.filter).setVisible(true);
            menu.findItem(R.id.group).setVisible(true);

        }
        return true;
    }

    /*
     * This is the listener for the share button
     */
    public View.OnClickListener oclShare = new OnClickListener() {

        public void onClick(final View vewView) {

            EasyTracker.getTracker().trackEvent("OnClicks", "Share", "Share Torrent", null);

            AsyncHttpClient ahcClient = new AsyncHttpClient();
            PersistentCookieStore pscCookies = new PersistentCookieStore(Trend.this);
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

                        filTorrent = File.createTempFile("xxx", ".html", Trend.this.getCacheDir());
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
                        Toast.makeText(Trend.this, getResources().getString(R.string.no_torrent_handlers), Toast.LENGTH_LONG).show();
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

                    Toast.makeText(Trend.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
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
            new Downloader(Trend.this).execute(Uri.parse(((URI) vewView.getTag()).toString()));

        }

    };

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

        if (this.objScraper != null) {
            if (this.objScraper.getStatus() != Status.FINISHED) {
                this.objScraper.cancel(true);
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
                Trend.this.fltFilter.ordinal(), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        Trend.this.fltFilter = Filter.values()[item];
                        Trend.this.objAdapter.notifyDataSetChanged();
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