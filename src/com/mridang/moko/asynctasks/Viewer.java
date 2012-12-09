package com.mridang.moko.asynctasks;

import java.net.URI;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.PersistentCookieStore;

/*
 * This is the class used to download and view a webpage.
 * It is used a asynchronous activity so that it does not hold
 * up the main UI thread.
 */
public class Viewer extends AsyncTask<URI, Integer, URI> {

	/*
	 * The context of the calling activity
	 */
    private Context ctxContext = null;

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

    }

    /*
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
	@Override
    public void onPostExecute(URI uriWebpage) {

	    Intent ittWebview = new Intent("android.intent.action.VIEW");
	    ittWebview.setData(Uri.parse(uriWebpage.toString()));
	    this.ctxContext.startActivity(ittWebview);

    }

}