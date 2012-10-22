package com.mridang.huntr.search.asynctasks;

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

import com.mridang.huntr.R;
import com.mridang.huntr.animations.ExpandAnimation;
import com.mridang.huntr.enums.Category;
import com.mridang.huntr.generics.Indexer;
import com.mridang.huntr.search.Search;
import com.mridang.huntr.search.adapters.SearchResultsAdapter;
import com.mridang.huntr.search.plugins.Kickass;
import com.mridang.huntr.search.plugins.Torleech;
import com.mridang.huntr.structures.Torrent;

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

	    	    	try {

    	    			Log.d("asynctasks.Finder", "Searching Torrentleech");
    	    			String strUsername = Finder.this.shpSettings.getString("torrentleech_username", null);
    	    			String strPassword = Finder.this.shpSettings.getString("torrentleech_password", null);
    		    		Torleech objTorleech = new Torleech(Finder.this.objSearch);
    		    		ArrayList<Torrent> objTorleechResults = new ArrayList<Torrent>();

    		    		objTorleechResults = objTorleech.doSearch(strQuery[0], Finder.this.enmCatergory, strUsername, strPassword);

    		    		return objTorleechResults;

	    	    	} catch (Indexer.LoginException e) {

	    	    		Log.w("asynctask.Finder", "Error logging in to Torrentleech");

	    	    	}

					return null;

	    	    }

	    	});

		}

		if (this.shpSettings.getBoolean("use_kickasstorrents", true)) {

	    	setCallables.add(new Callable<ArrayList<Torrent>>() {

	    	    public ArrayList<Torrent> call() throws Exception {

	    	    	try {

	    	    		Log.d("asynctasks.Finder", "Searching Kickass Torrents");
			    		Kickass objKickass = new Kickass(Finder.this.objSearch);
			    		ArrayList<Torrent> objKickassResults = new ArrayList<Torrent>();
			    		objKickassResults = objKickass.doSearch(strQuery[0], Finder.this.enmCatergory);
			    		return objKickassResults;


			    	} catch (Exception e) {

			    		Log.w("asynctask.Finder", "Error logging in to KickassTorrents");

			    	}

			    	return null;

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

    	if (objResults.size() == 0) {

			Toast.makeText(this.objSearch,
					R.string.no_results_found, Toast.LENGTH_LONG)
					.show();

    	} else {

	    	ListView lvwResults = (ListView) this.objSearch.findViewById(R.id.results);
	    	this.objSearch.objAdapter = new SearchResultsAdapter(this.objSearch, objResults);
	    	lvwResults.setAdapter(this.objSearch.objAdapter);
	    	this.objSearch.invalidateOptionsMenu();

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
