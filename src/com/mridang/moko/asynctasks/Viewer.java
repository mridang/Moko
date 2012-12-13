package com.mridang.moko.asynctasks;

import java.net.URI;

import org.apache.http.cookie.Cookie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.PersistentCookieStore;
import com.mridang.moko.R;

/*
 * This is the class used to download and view a webpage.
 * It is used a asynchronous activity so that it does not hold
 * up the main UI thread.
 */
@SuppressLint("NewApi")
public class Viewer extends AsyncTask<URI, Integer, URI> {

    /*
     * The context of the calling activity
     */
    private Context ctxContext = null;
    /*
     * progress dialog to show user that the backup is processing.
     */
    private ProgressDialog pdgDialog;

    /*
     * @see android.os.AsyncTask#onPreExecute()
     */
    protected void onPreExecute() {

        this.pdgDialog.show();

    }

    /*
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected URI doInBackground(URI... uriWebpage) {

        Log.d("asynctasks.Viewer", String.format("Trying to view: %s", uriWebpage[0].toString()));

        try {

            CookieSyncManager.createInstance(this.ctxContext);

            CookieManager cmrCookies = CookieManager.getInstance();
            if (cmrCookies.hasCookies() == false) {
                cmrCookies.removeExpiredCookie();
            } else {
                cmrCookies.removeAllCookie();
            }
            cmrCookies.removeSessionCookie();
            cmrCookies.acceptCookie();

            PersistentCookieStore pscJar = new PersistentCookieStore(this.ctxContext);
            SystemClock.sleep(1000);

            for (Cookie cooCookie : pscJar.getCookies())
                cmrCookies.setCookie(cooCookie.getName(), cooCookie.getValue());

            CookieSyncManager.getInstance().sync();

            return uriWebpage[0];

        } catch (Exception e) {
            e.printStackTrace();
            EasyTracker.getTracker().trackException(e.getMessage(), e, false);
        }

        return null;

    }

    /*
     * Initializes this task
     *
     * @param  objContext    The instance of the calling class
     */
	public Viewer(Activity objContext) {

        this.ctxContext = objContext;
        this.pdgDialog = new ProgressDialog(objContext);
        this.pdgDialog.setIndeterminate(true);
        if (Build.VERSION.SDK_INT > 11) {
        	this.pdgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        	this.pdgDialog.setProgressNumberFormat(null); 
        	this.pdgDialog.setProgressPercentFormat(null);
        } else {
        	this.pdgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        this.pdgDialog.setMessage(this.ctxContext.getString(R.string.loading));

    }

    /*
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    public void onPostExecute(URI uriWebpage) {

    	this.pdgDialog.dismiss();
        Intent ittWebview = new Intent("android.intent.action.VIEW");
        ittWebview.setData(Uri.parse(uriWebpage.toString()));
        this.ctxContext.startActivity(ittWebview);

    }

}