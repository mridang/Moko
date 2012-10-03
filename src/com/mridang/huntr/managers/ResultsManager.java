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
 * This class is the manager for the results and contains the methods
 * for grouping, sorting and filtering the results
 */
public class ResultsManager {

	/*
	 * The list of filtering options
	 */
	public static enum Filter {
		ONLY_PRIVATE(), ONLY_PUBLIC, ONLY_HQ, ONLY_LQ
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
		BY_SITE, BY_DATE, BY_VISIBILITY, BY_QUALITY
	}

	/*
	 * This method will return the results sorted by a filter type. This
	 * method will first filter out the rows as mentioned in the filter
	 * condition. After filtering, it will sort the rows. Once sorted, it
	 * will group the rows into a map structure. Since the adapter doens't
	 * work well with a map structure, we will denormalise the map structure
	 * into a list of rows.
	 *
	 * @param  objResults         the list of results to sort
	 * @param  srtSort            the sort to apply
	 * @param  fltFilter          the filter to apply
	 * @param  grpGroup           the group to apply
 	 * @return ArrayList<Row>     The list of Rows, sorted, filtered and grouped
	 */
	public static ArrayList<Row> process(ArrayList<Torrent> objResults, Sort srtSort, Filter fltFilter, Group grpGroup) {

        Log.d("managers.ResultsManager",
                String.format("Received %d records to filter using the %s filter",
                        objResults.size(), fltFilter.name()));

		Iterator<Torrent> itrResults = objResults.iterator();

		switch (fltFilter) {

		case ONLY_HQ:

			while (itrResults.hasNext()) {
				Torrent objResult = itrResults.next();
				if (objResult.getSize() < 4831838208L)
					itrResults.remove();
			}

			break;

		case ONLY_LQ:

			while (itrResults.hasNext()) {
				Torrent objResult = itrResults.next();
				if (objResult.getSize() > 4831838208L)
					itrResults.remove();
			}

			break;


		case ONLY_PRIVATE:

			while (itrResults.hasNext()) {
				Torrent objResult = itrResults.next();
				if (objResult.isPrivate() == false)
					itrResults.remove();
			}

		case ONLY_PUBLIC:

			while (itrResults.hasNext()) {
				Torrent objResult = itrResults.next();
				if (objResult.isPrivate() == true)
					itrResults.remove();
			}

			break;

		}


		switch (srtSort) {

		case BY_DATE:

			Collections.sort(objResults, new Comparator<Torrent>() {
				public int compare(Torrent objResultA, Torrent objResultB) {
					return objResultB.getDate().compareTo(objResultA.getDate());
				}
			});

			break;

		case BY_LEECHERS:

			Collections.sort(objResults, new Comparator<Torrent>() {
				public int compare(Torrent objResultA, Torrent objResultB) {
					return objResultB.getLeechers().compareTo(objResultA.getLeechers());
				}
			});

			break;

		case BY_SEEDERS:

			Collections.sort(objResults, new Comparator<Torrent>() {
				public int compare(Torrent objResultA, Torrent objResultB) {
					return objResultB.getSeeders().compareTo(objResultA.getSeeders());
				}
			});

			break;

		case BY_SIZE:

			Collections.sort(objResults, new Comparator<Torrent>() {
				public int compare(Torrent objResultA, Torrent objResultB) {
					return objResultB.getSize().compareTo(objResultA.getSize());
				}
			});

			break;

		}


		LinkedHashMap<String, ArrayList<Torrent>> mapResults = new LinkedHashMap<String, ArrayList<Torrent>>();

		switch (grpGroup) {

		case BY_SITE:

			for (Torrent objResult : objResults) {
				if (mapResults.get(objResult.getSite()) == null)
					mapResults.put(objResult.getSite(), new ArrayList<Torrent>());
				mapResults.get(objResult.getSite()).add(objResult);
			}

			break;

		case BY_VISIBILITY:

			for (Torrent objResult : objResults) {
				if (mapResults.get(objResult.getVisibility()) == null)
					mapResults.put(objResult.getVisibility(), new ArrayList<Torrent>());
				mapResults.get(objResult.getVisibility()).add(objResult);
			}

			break;

		case BY_DATE:

			for (Torrent objResult : objResults) {
				if (mapResults.get(objResult.getSite()) == null)
					mapResults.put(objResult.getSite(), new ArrayList<Torrent>());
				mapResults.get(objResult.getSite()).add(objResult);
			}

			break;

		case BY_QUALITY:

			for (Torrent objResult : objResults) {
				if (mapResults.get(objResult.getQuality()) == null)
					mapResults.put(objResult.getQuality(), new ArrayList<Torrent>());
				mapResults.get(objResult.getQuality()).add(objResult);
			}

			break;

		}


		ArrayList<Row> objRows = new ArrayList<Row>();

		switch (grpGroup) {

		case BY_SITE:

			for (Map.Entry<String, ArrayList<Torrent>> mapResult : mapResults.entrySet()) {
				objRows.add(new Section(mapResult.getKey().toString()));
				for (Torrent objResult : mapResult.getValue())
					objRows.add(objResult);
			}

			break;

		case BY_DATE:

			for (Map.Entry<String, ArrayList<Torrent>> mapResult : mapResults.entrySet()) {
				objRows.add(new Section(mapResult.getKey().toString()));
				for (Torrent objResult : mapResult.getValue())
					objRows.add(objResult);
			}

			break;

		case BY_QUALITY:

			for (Map.Entry<String, ArrayList<Torrent>> mapResult : mapResults.entrySet()) {
				objRows.add(new Section(mapResult.getKey().toString()));
				for (Torrent objResult : mapResult.getValue())
					objRows.add(objResult);
			}

			break;

		case BY_VISIBILITY:

			for (Map.Entry<String, ArrayList<Torrent>> mapResult : mapResults.entrySet()) {
				objRows.add(new Section(mapResult.getKey().toString()));
				for (Torrent objResult : mapResult.getValue())
					objRows.add(objResult);
			}

			break;

		}

        Log.d("managers.ResultsManager",
                String.format("Returned %d records after filtering.", objResults.size()));

		return objRows;

	}

}