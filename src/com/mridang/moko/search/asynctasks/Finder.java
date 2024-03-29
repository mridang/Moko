package com.mridang.moko.search.asynctasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.mridang.moko.R;
import com.mridang.moko.animations.ExpandAnimation;
import com.mridang.moko.enums.Category;
import com.mridang.moko.generics.Indexer;
import com.mridang.moko.search.Search;
import com.mridang.moko.search.adapters.SearchResultsAdapter;
import com.mridang.moko.search.plugins.Kickass;
import com.mridang.moko.search.plugins.Torleech;
import com.mridang.moko.structures.Torrent;

/*
 * This is the class used to search for torrents.
 * It is used a asynchronous activity so that it does not hold
 * up the main UI thread.
 */
public class Finder extends AsyncTask<String, Integer, ArrayList<Torrent>> {

    /*
     * The instance of the calling class
     */
    private Search objSearch = null;
    /*
     *  The category to search
     */
    private Category enmCatergory = null;
    /*
     * The instance of the preferences class to fetch preferences
     */
    private SharedPreferences shpSettings = null;
    /*
     * The previous toolbar that was vewPrevious
     */
    private View vewPrevious;
    /*
     * The instance of the annimator that animates the toolbar
     */
    private ExpandAnimation objExpander;
    /*
     * The time taken to execute
     */
    private Long lngTiming;

    /*
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected ArrayList<Torrent> doInBackground(final String... strQuery) {

        ArrayList<Torrent> objResults = new ArrayList<Torrent>();
        ExecutorService esrExecutor = Executors.newFixedThreadPool(2);
        Set<Callable<ArrayList<Torrent>>> setCallables = new HashSet<Callable<ArrayList<Torrent>>>();

        if (this.shpSettings.getBoolean("use_torrentleech", false)) {

            setCallables.add(new Callable<ArrayList<Torrent>>() {

                public ArrayList<Torrent> call() throws Exception {

                    Log.d("asynctasks.Finder", "Searching Torrentleech");
                    ArrayList<Torrent> objTorleechResults = new ArrayList<Torrent>();

                    try {

                        Torleech objTorleech = new Torleech(Finder.this.objSearch);
                        objTorleechResults = objTorleech.doSearch(strQuery[0], Finder.this.enmCatergory);

                    } catch (Indexer.LoginException e) {
                        Log.w("asynctask.Finder", "Error logging in to Torrentleech");
                        EasyTracker.getTracker().trackException(e.getMessage(), e, false);
                    }

                    return objTorleechResults;

                }

            });

        }

        if (this.shpSettings.getBoolean("use_kickasstorrents", true)) {

            setCallables.add(new Callable<ArrayList<Torrent>>() {

                public ArrayList<Torrent> call() throws Exception {

                    Log.d("asynctasks.Finder", "Searching Kickass Torrents");
                    ArrayList<Torrent> objKickassResults = new ArrayList<Torrent>();

                    try {

                        Kickass objKickass = new Kickass(Finder.this.objSearch);
                        objKickassResults = objKickass.doSearch(strQuery[0], Finder.this.enmCatergory);

                    } catch (Exception e) {
                        Log.w("asynctask.Finder", "Error logging in to KickassTorrents");
                        EasyTracker.getTracker().trackException(e.getMessage(), e, false);
                        //TODO: Show message to the user that we were unable to query this site.
                    }

                    return objKickassResults;

                }

            });

        }

        List<Future<ArrayList<Torrent>>> lstFutures;

        try {

            lstFutures = esrExecutor.invokeAll(setCallables);

            for(Future<ArrayList<Torrent>> futFuture : lstFutures){

                if (futFuture.get().isEmpty() == false)
                    objResults.addAll(futFuture.get());

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        esrExecutor.shutdown();

        return objResults;

    }

    /*
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {

        this.objSearch.showProgress();
        this.lngTiming = System.nanoTime();

    }

    /*
     * Initializes this task
     *
     * @param  objSearch    The instance of the calling Search class
     * @param  enmCatergory  The category which should be searched
     */
    public Finder(Search objContext, Category enmCatergory) {

        this.enmCatergory = enmCatergory;
        this.objSearch = objContext;
        this.shpSettings = PreferenceManager.getDefaultSharedPreferences(objContext);

    }

    /*
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(ArrayList<Torrent> objResults) {

        this.objSearch.hideProgress();
        this.lngTiming = System.nanoTime() - this.lngTiming;

        EasyTracker.getTracker().trackTiming("AsyncTasks", this.lngTiming, "Finder", "Get Searched Torrents");

        if (objResults.size() == 0) {

            Toast.makeText(this.objSearch,
                    R.string.no_results_found, Toast.LENGTH_LONG)
                    .show();

        } else {

            ListView lvwResults = (ListView) this.objSearch.findViewById(R.id.results);
            this.objSearch.objAdapter = new SearchResultsAdapter(this.objSearch, objResults);
            lvwResults.setAdapter(this.objSearch.objAdapter);

            lvwResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                    if (Finder.this.objExpander == null || Finder.this.objExpander.hasEnded()) {

                        if(vewPrevious != null) {
                            Finder.this.objSearch.objAdapter.objRows.get(position).setExpanded(false);
                            View close = vewPrevious.findViewById(R.id.toolbar);
                            Finder.this.objExpander = new ExpandAnimation(close, 500);
                            close.startAnimation(Finder.this.objExpander);
                        }

                        Finder.this.objSearch.objAdapter.objRows.get(position).setExpanded(true);
                        View toolbar = view.findViewById(R.id.toolbar);
                        Finder.this.objExpander = new ExpandAnimation(toolbar, 500);
                        toolbar.startAnimation(Finder.this.objExpander);

                        vewPrevious = view;

                    }

                }

            });

        }

    }

}