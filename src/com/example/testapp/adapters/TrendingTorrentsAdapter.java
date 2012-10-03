package com.example.testapp.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testapp.R;
import com.example.testapp.Trend;
import com.example.testapp.helpers.SizeConverter;
import com.example.testapp.interfaces.Row;
import com.example.testapp.managers.TrendingManager;
import com.example.testapp.structures.Section;
import com.example.testapp.structures.Torrent;

/*
 * This is custom adapter for the releases listview.
 */
public class TrendingTorrentsAdapter extends BaseAdapter  {

    /*
	 * The layout inflater for inflating the listview rows
	 */
	private final LayoutInflater linInflater;
	/*
	 * The array containing the rows
	 */
	public ArrayList<Torrent> objTorrents;
	/*
	 * The array containing the rows
	 */
	public ArrayList<Row> objRows;
	/*
	 * The instance of the calling class
	 */
    private Trend ctxContext = null;
    /*
     * The holder to hold intance of a row layout
     */
    private static class RowHolder {

        public TextView tvwName;
        public TextView tvwDate;
        public TextView tvwIndexer;
        public TextView tvwSeeders;
        public TextView tvwLeechers;
        public TextView tvwSize;
        public Button btnDownload;
        public Button btnShare;
        public LinearLayout lltEnqueuer;
        public Button btnEnqueue;
        public Button btnWebsite;
        public View vewToolbar;

    }
    /*
     * A boolean value indicating whether an enqueuer is installed
     */
    Boolean booHasEnqueuer = true;

	/*
	 * Constructor
	 */
    public TrendingTorrentsAdapter(Trend ctxContext, ArrayList<Torrent> objTorrents) {

        linInflater = LayoutInflater.from(ctxContext);
        this.ctxContext = ctxContext;
        this.objTorrents = objTorrents;
        this.objRows = TrendingManager.process(new ArrayList<Torrent>(this.objTorrents),
          this.ctxContext.srtSort, this.ctxContext.fltFilter,
          this.ctxContext.grpGroup);

        if (this.objRows.size() == 0)
            this.ctxContext.showFilters();

        try {

            this.ctxContext.getPackageManager().
                    getApplicationInfo("net.torrenttoise", 0 );

        } catch (PackageManager.NameNotFoundException e) {

            this.booHasEnqueuer = false;

        }

    }

	/*
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {

		return objRows.size();

	}

	/*
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Torrent getItem(int intPosition) {

		return (Torrent) objRows.get(intPosition);

	}

	/*
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int intPosition) {

		return intPosition;

	}

	/*
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {

	    return 2;

	}

	/*
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int intPosition) {

	    return (this.objRows.get(intPosition) instanceof Section) ? 0 : 1;

	}

	/*
	 * @see android.widget.Adapter#notifyDataSetChanged()
	 */
	@Override
    public void notifyDataSetChanged() {

		this.objRows = TrendingManager.process(new ArrayList<Torrent>(this.objTorrents),
				this.ctxContext.srtSort, this.ctxContext.fltFilter,
				this.ctxContext.grpGroup);

		super.notifyDataSetChanged();

        if (this.objRows.size() == 0)
            this.ctxContext.showFilters();

	}

	/*
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
    public View getView(int intPosition, View vewView, ViewGroup vgrParent) {

        if (this.objRows.get(intPosition) instanceof Section) {

            View vewSection = linInflater.inflate(R.layout.category, null);
            Section objSection = (Section) this.objRows.get(intPosition);

            vewSection.setOnClickListener(null);
            vewSection.setOnLongClickListener(null);
            vewSection.setLongClickable(false);

            final TextView sectionView = (TextView) vewSection.findViewById(R.id.text);
            sectionView.setText(objSection.getName());

            return vewSection;

        } else {

            RowHolder objHolder;

            if (vewView == null) {

                vewView = linInflater.inflate(R.layout.row, null);

                objHolder  = new RowHolder();
                objHolder.tvwName = (TextView) vewView.findViewById(R.id.name);
                objHolder.tvwDate = (TextView) vewView.findViewById(R.id.date);
                objHolder.tvwIndexer = (TextView) vewView.findViewById(R.id.indexer);
                objHolder.tvwSeeders = (TextView) vewView.findViewById(R.id.seeders);
                objHolder.tvwLeechers = (TextView) vewView.findViewById(R.id.leechers);
                objHolder.tvwSize = (TextView) vewView.findViewById(R.id.filesize);
                objHolder.btnDownload = (Button) vewView.findViewById(R.id.download);
                objHolder.btnShare = (Button) vewView.findViewById(R.id.share);
                objHolder.btnEnqueue = (Button) vewView.findViewById(R.id.enqueue);
                objHolder.lltEnqueuer = (LinearLayout) vewView.findViewById(R.id.enqueuer);
                objHolder.btnWebsite = (Button) vewView.findViewById(R.id.website);
                objHolder.vewToolbar = vewView.findViewById(R.id.toolbar);

                vewView.setTag( objHolder);

            } else {

                objHolder = (RowHolder) vewView.getTag();

            }

            Torrent objResult = (Torrent) this.objRows.get(intPosition);

            objHolder.tvwName.setText(objResult.getName());
            objHolder.tvwDate.setText(new SimpleDateFormat("d MMMMM, yyyy").format(objResult.getDate()));
            objHolder.tvwIndexer.setText(objResult.getIndexer());
            objHolder.tvwSeeders.setText(objResult.getSeeders().toString());
            objHolder.tvwLeechers.setText(objResult.getLeechers().toString());
            objHolder.tvwSize.setText(SizeConverter.printSize(objResult.getSize()));
            objHolder.btnDownload.setOnClickListener(this.ctxContext.oclDownload);
            objHolder.btnDownload.setTag(objResult.getLocation());
            objHolder.btnShare.setOnClickListener(this.ctxContext.oclShare);
            objHolder.btnShare.setTag(objResult.getWebpage());
            objHolder.btnEnqueue.setOnClickListener(this.ctxContext.oclEnqueue);
            objHolder.btnEnqueue.setTag(objResult.getLocation());
            objHolder.lltEnqueuer.setVisibility(this.booHasEnqueuer ? View.VISIBLE : View.GONE);
            objHolder.btnWebsite.setOnClickListener(this.ctxContext.oclWebsite);
            objHolder.btnWebsite.setTag(objResult.getWebpage());
            if (objResult.isExpanded()) {
                ((LinearLayout.LayoutParams) objHolder.vewToolbar.getLayoutParams()).bottomMargin = 0;
                objHolder.vewToolbar.setVisibility(View.VISIBLE);
            } else {
                ((LinearLayout.LayoutParams) objHolder.vewToolbar.getLayoutParams()).bottomMargin = -125;
                objHolder.vewToolbar.setVisibility(View.GONE);
            }

            return vewView;

        }

    }

}