package com.mridang.moko.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.mridang.moko.R;
import com.mridang.moko.search.plugins.Torleech;

/*
 * This is the class used to show ongoing notifications for private tracker ratios.
 */
public class NotficationReceiver extends BroadcastReceiver {

    /*
     * The unique notification id for the Torrentleech ratios.
     */
    public static final int TORRENTLEECH_NOTIFICATION_ID = 10;

    /*
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context ctxContext, Intent ittIntent) {

        Log.i("receivers.NotificationReceiver", "Checking the the tracker ratios.");

        if (PreferenceManager.getDefaultSharedPreferences(ctxContext).getBoolean("show_tracker_ratios", true)) {

            Log.v("receivers.NotificationReceiver", "Ratio notifications are enabled.");

            if (PreferenceManager.getDefaultSharedPreferences(ctxContext).getBoolean("use_torrentleech", false)) {

                Log.v("receivers.NotificationReceiver", "Torrentleech is in use.");

                try {

                    Float fltRatio = (new Torleech(ctxContext)).getRatio();
                    String strMessage = String.format(ctxContext.getString(R.string.your_ratio_is), fltRatio);
                    String strAlert = String.format(ctxContext.getString(R.string.logged_in_to), "Torrentleech");

                    NotificationManager nfmManager = (NotificationManager) ctxContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    PendingIntent contentIntent = PendingIntent.getActivity(ctxContext, 0, new Intent(), 0);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctxContext);

                    mBuilder.setSmallIcon(R.drawable.ic_action_line_chart);
                    mBuilder.setContentTitle("Torrentleech");
                    mBuilder.setContentText(strMessage);
                    mBuilder.setTicker(strAlert);
                    mBuilder.setContentIntent(contentIntent);
                    mBuilder.setOngoing(true);
                    nfmManager.notify(TORRENTLEECH_NOTIFICATION_ID, mBuilder.getNotification());

                } catch (Exception e) {
                    Log.w("receivers.NotificationReceiver", e);
                    EasyTracker.getTracker().trackException(e.getMessage(), e, false);
                }

            } else {

                Log.v("receivers.NotificationReceiver", "Torrentleech is not in use.");

            }

        } else {

            Log.v("receivers.NotificationReceiver", "Ratio notifications are disabled.");

        }

    }

}