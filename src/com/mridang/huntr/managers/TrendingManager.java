package com.mridang.huntr.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.util.Log;

import com.mridang.huntr.interfaces.Row;
import com.mridang.huntr.structures.Section;
import com.mridang.huntr.structures.Torrent;

/*
 * This class is the manager for the torrents and contains the methods
 * for grouping, sorting and filtering the torrents
 */
public class TrendingManager {

	/*
	 * The list of filtering options
	 */
	public static enum Filter {
		ONLY_PRIVATE, ONLY_PUBLIC, ONLY_HQ, ONLY_LQ
	};

	/*
	 * The list of sorting options
	 */
	public static enum Sort {
		BY_SEEDERS, BY_LEECHERS, BY_DATE, BY_SIZE
	}

	/*
	 * The list of grouping options
	 */
	public static enum Group {
		BY_SITE, BY_DATE, BY_TYPE, BY_QUALITY
	}

	/*
	 * This method will return the torrents sorted by a filter type. This
	 * method will first filter out the rows as mentioned in the filter
	 * condition. After filtering, it will sort the rows. Once sorted, it
	 * will group the rows into a map structure. Since the adapter doens't
	 * work well with a map structure, we will denormalise the map structure
	 * into a list of rows.
	 *
	 * @param  objTorrents        the list of torrents to sort
	 * @param  srtSort            the sort to apply
	 * @param  fltFilter          the filter to apply
	 * @param  grpGroup           the group to apply
 	 * @return ArrayList<Row>     The list of Rows, sorted, filtered and grouped
	 */
	public static ArrayList<Row> process(ArrayList<Torrent> objTorrents, Sort srtSort, Filter fltFilter, Group grpGroup) {

        Log.d("managers.TrendingManager",
                String.format("Received %d records to filter using the %s filter",
                        objTorrents.size(), fltFilter.name()));

		Iterator<Torrent> itrTorrents = objTorrents.iterator();

		switch (fltFilter) {

		case ONLY_HQ:

			while (itrTorrents.hasNext()) {
				Torrent objTorrent = itrTorrents.next();
				if (objTorrent.getSize() < 4831838208L)
					itrTorrents.remove();
			}

			break;

		case ONLY_LQ:

			while (itrTorrents.hasNext()) {
				Torrent objTorrent = itrTorrents.next();
				if (objTorrent.getSize() > 4831838208L)
					itrTorrents.remove();
			}

			break;


		case ONLY_PRIVATE:

			while (itrTorrents.hasNext()) {
				Torrent objTorrent = itrTorrents.next();
				if (objTorrent.isPrivate() == false)
					itrTorrents.remove();
			}

		case ONLY_PUBLIC:

			while (itrTorrents.hasNext()) {
				Torrent objTorrent = itrTorrents.next();
				if (objTorrent.isPrivate() == true)
					itrTorrents.remove();
			}

			break;

		}


		switch (srtSort) {

		case BY_DATE:

			Collections.sort(objTorrents, new Comparator<Torrent>() {
				public int compare(Torrent objTorrentA, Torrent objTorrentB) {
					return objTorrentB.getDate().compareTo(objTorrentA.getDate());
				}
			});

			break;

		case BY_LEECHERS:

			Collections.sort(objTorrents, new Comparator<Torrent>() {
				public int compare(Torrent objTorrentA, Torrent objTorrentB) {
					return objTorrentB.getLeechers().compareTo(objTorrentA.getLeechers());
				}
			});

			break;

		case BY_SEEDERS:

			Collections.sort(objTorrents, new Comparator<Torrent>() {
				public int compare(Torrent objTorrentA, Torrent objTorrentB) {
					return objTorrentB.getSeeders().compareTo(objTorrentA.getSeeders());
				}
			});

			break;

		case BY_SIZE:

			Collections.sort(objTorrents, new Comparator<Torrent>() {
				public int compare(Torrent objTorrentA, Torrent objTorrentB) {
					return objTorrentB.getSize().compareTo(objTorrentA.getSize());
				}
			});

			break;

		}


		LinkedHashMap<String, ArrayList<Torrent>> mapTorrents = new LinkedHashMap<String, ArrayList<Torrent>>();

		switch (grpGroup) {

		case BY_SITE:

			for (Torrent objTorrent : objTorrents) {
				if (mapTorrents.get(objTorrent.getSite()) == null)
					mapTorrents.put(objTorrent.getSite(), new ArrayList<Torrent>());
				mapTorrents.get(objTorrent.getSite()).add(objTorrent);
			}

			break;

		case BY_TYPE:

			for (Torrent objTorrent : objTorrents) {
				if (mapTorrents.get(objTorrent.getCategory().name()) == null)
					mapTorrents.put(objTorrent.getCategory().name(), new ArrayList<Torrent>());
				mapTorrents.get(objTorrent.getCategory().name()).add(objTorrent);
			}

			break;

		case BY_DATE:

			for (Torrent objTorrent : objTorrents) {
				if (mapTorrents.get(objTorrent.getSite()) == null)
					mapTorrents.put(objTorrent.getSite(), new ArrayList<Torrent>());
				mapTorrents.get(objTorrent.getSite()).add(objTorrent);
			}

			break;

		case BY_QUALITY:

			for (Torrent objTorrent : objTorrents) {
				if (mapTorrents.get(objTorrent.getQuality()) == null)
					mapTorrents.put(objTorrent.getQuality(), new ArrayList<Torrent>());
				mapTorrents.get(objTorrent.getQuality()).add(objTorrent);
			}

			break;

		}


		ArrayList<Row> objRows = new ArrayList<Row>();

		switch (grpGroup) {

		case BY_SITE:

			for (Map.Entry<String, ArrayList<Torrent>> mapTorrent : mapTorrents.entrySet()) {
				objRows.add(new Section(mapTorrent.getKey().toString()));
				for (Torrent objTorrent : mapTorrent.getValue())
					objRows.add(objTorrent);
			}

			break;

		case BY_DATE:

			for (Map.Entry<String, ArrayList<Torrent>> mapTorrent : mapTorrents.entrySet()) {
				objRows.add(new Section(mapTorrent.getKey().toString()));
				for (Torrent objTorrent : mapTorrent.getValue())
					objRows.add(objTorrent);
			}

			break;

		case BY_QUALITY:

			for (Map.Entry<String, ArrayList<Torrent>> mapTorrent : mapTorrents.entrySet()) {
				objRows.add(new Section(mapTorrent.getKey().toString()));
				for (Torrent objTorrent : mapTorrent.getValue())
					objRows.add(objTorrent);
			}

			break;

		case BY_TYPE:

			for (Map.Entry<String, ArrayList<Torrent>> mapTorrent : mapTorrents.entrySet()) {
				objRows.add(new Section(mapTorrent.getKey().toString()));
				for (Torrent objTorrent : mapTorrent.getValue())
					objRows.add(objTorrent);
			}

			break;

		}

        Log.d("managers.TrendingManager",
                String.format("Returned %d records after filtering.", objTorrents.size()));

		return objRows;

	}

}