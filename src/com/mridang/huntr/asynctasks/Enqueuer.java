package com.mridang.huntr.asynctasks;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/*
 * This is the class used to download and enqueue a torrent.
 * It is used a asynchronous activity so that it does not hold
 * up the main UI thread.
 */
@SuppressLint("WorldReadableFiles")
public class Enqueuer extends AsyncTask<URI, Integer, URI> {

	/*
	 * The instance of the calling class
	 */
    private Activity objContext = null;

	/*
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
    @Override
    protected URI doInBackground(URI... uriTorrent) {

        Log.d("asynctasks.Enqueuer", String.format("Trying to download: %s", uriTorrent[0].toString()));

    	try {

    	    URL urlTorrent = uriTorrent[0].toURL();

    	    HttpURLConnection hucConnection = (HttpURLConnection) urlTorrent.openConnection();
    	    hucConnection.setRequestMethod("GET");
    	    hucConnection.connect();

    	    FileOutputStream fosOutput = this.objContext.openFileOutput("test.torrent", Context.MODE_WORLD_READABLE);
    	    InputStream ipsInput = hucConnection.getInputStream();

    	    byte[] bytBuffer = new byte[1024];
    	    Integer intLength = 0;
    	    while ((intLength = ipsInput.read(bytBuffer)) > 0)
    	         fosOutput.write(bytBuffer, 0, intLength);

    	    fosOutput.close();

    	    return this.objContext.getFileStreamPath("test.torrent").toURI();

    	} catch (Exception e) {

    		e.printStackTrace();

    	}

		return null;

    }

    /*
     * Initializes this task
     *
     * @param  objContext    The instance of the calling Search class
     */
    public Enqueuer(Activity objContext) {

    	this.objContext = objContext;

    }

    /*
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(URI uriPath) {

		Intent ittEnqueue = new Intent("net.torrenttoise.action.ADD_TORRENT");
		ittEnqueue.putExtra("net.torrenttoise.extra.TORRENT_FILE_URI", uriPath.toString());
		this.objContext.startActivity(ittEnqueue);

    }

}