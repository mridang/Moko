package com.mridang.moko.asynctasks;

import org.apache.http.cookie.Cookie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.CookieSyncManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.PersistentCookieStore;

/*
 * This is the class used to download a torrent file.
 * It is used a asynchronous activity so that it does not hold
 * up the main UI thread.
 */
@SuppressLint("NewApi")
public class Downloader extends AsyncTask<Uri, Integer, Request> {

    /*
     * The context of the calling activity
     */
    private Context ctxContext = null;

    /*
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Request doInBackground(Uri... uriTorrent) {

        Log.d("asynctasks.Downloader", String.format("Trying to download: %s", uriTorrent[0].toString()));

        try {

            CookieSyncManager.createInstance(this.ctxContext);

            Request rqtRequest = new Request(uriTorrent[0]);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                rqtRequest.setShowRunningNotification(true);
            } else {
                rqtRequest.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            rqtRequest.setMimeType("application/x-bittorrent");
            rqtRequest.setVisibleInDownloadsUi(true);

            PersistentCookieStore pscJar = new PersistentCookieStore(this.ctxContext);
            SystemClock.sleep(1000);

            for (Cookie cooCookie : pscJar.getCookies())
                rqtRequest.addRequestHeader(cooCookie.getName(), cooCookie.getValue());

            CookieSyncManager.getInstance().sync();

            return rqtRequest;

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
    public Downloader(Activity objContext) {

        this.ctxContext = objContext;

    }

    /*
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    public void onPostExecute(Request rqtRequest) {

        DownloadManager dlmManager = (DownloadManager) this.ctxContext.getSystemService(Context.DOWNLOAD_SERVICE);
        dlmManager.enqueue(rqtRequest);

    }

}