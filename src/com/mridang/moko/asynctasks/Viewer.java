package com.mridang.moko.asynctasks;

import java.net.URI;

import org.apache.http.cookie.Cookie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.loopj.android.http.PersistentCookieStore;

/*
 * This is the class used to download and view a webpage.
 * It is used a asynchronous activity so that it does not hold
 * up the main UI thread.
 */
@SuppressLint("SetJavaScriptEnabled")
public class Viewer extends AsyncTask<URI, Integer, URI> {

	/*
	 * The instance of the calling class
	 */
    private Activity objContext = null;

	/*
	 * @see android.os.AsyncTask#doInBs[])
	 */
    @Override
    protected URI doInBackground(URI... uriWebpage) {

        Log.d("asynctasks.Viewer", String.format("Trying to view: %s", uriWebpage[0].toString()));

    	try {

            CookieSyncManager.createInstance(this.objContext);

            CookieManager cmrCookies = CookieManager.getInstance();
            cmrCookies.removeSessionCookie();
            cmrCookies.acceptCookie();

            PersistentCookieStore pscJar = new PersistentCookieStore(this.objContext);
    	    SystemClock.sleep(1000);

    	    //byte[] buffer = new byte[1024];
    	    //Cookie cooCookie = null;
            for (Cookie cooCookie : pscJar.getCookies())
            	cmrCookies.setCookie(cooCookie.getName(), cooCookie.getValue());

            CookieSyncManager.getInstance().sync();

    	    return uriWebpage[0];

    	} catch (Exception e) {

    		e.printStackTrace();

    	}

		return null;

    }

    /*
     * Initializes this task
     *
     * @param  objContext    The instance of the calling class
     */
    public Viewer(Activity objContext) {

    	this.objContext = objContext;

    }

    /*
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
	@Override
    public void onPostExecute(URI uriWebpage) {

	    Intent ittWebview = new Intent("android.intent.action.VIEW");
	    ittWebview.setData(Uri.parse(uriWebpage.toString()));
	    this.objContext.startActivity(ittWebview);

    }

}