package com.mridang.moko.scrapers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.mridang.moko.enums.Category;
import com.mridang.moko.generics.Indexer;
import com.mridang.moko.helpers.DateConverter;
import com.mridang.moko.helpers.SizeConverter;
import com.mridang.moko.structures.Torrent;

/*
 * This class is used to search Fenopy Europe
 */
public class Fenopy extends Indexer {

	/* The URL of the scrape page */
	private final String SCRAPE_URL = "http://fenopy.eu/";

    /*
     * Initialises this task
     *
     * @param  ctxContext  the context of the calling class
     */
    public Fenopy(Context ctxContext) {

        super(ctxContext);

    }

	/*
	 * This method is the method that searches the site.
	 *
	 * @return a list of torrents.
	 */
	public ArrayList<Torrent> doScrape() throws Exception {

		Document objDocument;
		ArrayList<Torrent> objTorrents = new ArrayList<Torrent>();
        String strUrl = SCRAPE_URL;

		Log.d("scrapers.Fenopy", String.format("URL: %s", strUrl));

		try {

			Log.d("scrapers.Fenopy", "Fetching page");

			objDocument = Jsoup.parse(super.doGet(strUrl, null), strUrl);

		} catch (Exception e) {

            EasyTracker.getTracker().trackException(e.getMessage(), e, false);
			Log.w("scrapers.Fenopy", "Error fetching and parsing page", e);
            throw e;

		}

		for (Element eleTable : objDocument.select("table#search_table")) {

			Category catCategory = null;

			if (eleTable.previousElementSibling().text().contains("Movies"))
				catCategory = Category.MOVIE;
			else if (eleTable.previousElementSibling().text().contains("Music"))
				catCategory = Category.ALBUM;
			else if (eleTable.previousElementSibling().text().contains("Show"))
				catCategory = Category.SHOW;
			else if (eleTable.previousElementSibling().text().contains("Games"))
				catCategory = Category.GAME;
			else if (eleTable.previousElementSibling().text().contains("Applications"))
				catCategory = Category.APP;
			else
				continue;

			for (Element div : eleTable.select("tr:gt(0)")) {

				Log.d("scrapers.Fenopy", "Found a row");

				try {

					Boolean booPrivate = false;

					String strName = "";
					try {
						strName = div.select("td:eq(0) a").text();
					} catch (Exception e) {
						throw e;
					}

					Date datDate = null;
					try {
						datDate = DateConverter.parseHumanDate(div.select("td:eq(4)").text());
					} catch (Exception e) {
						throw e;
					}

					Integer intSeeders = 0;
					try {
						intSeeders = Integer
								.parseInt(div.select("td:eq(5)").text());
					} catch (Exception e) {
						throw e;
					}

					Boolean booVerified = null;
					try {
						booVerified = div.select("img.ttip").first() == null ? false
								: true;
					} catch (Exception e) {
						throw e;
					}

					Integer intLeechers = 0;
					try {
						intLeechers = Integer.parseInt(div.select("td:eq(6)")
								.text());
					} catch (Exception e) {
						throw e;
					}

					URI uriLocation = null;
					try {
						uriLocation = new URI(objDocument.baseUri() + div.select("a[onclick~=\\.torrent]")
								.attr("onclick").replaceAll(".*=\\'/", "").replaceAll("\\';.*", ""));
					} catch (Exception e) {
						throw e;
					}

					Long lngSize = null;
					try {
						lngSize = SizeConverter.parseSize(div.select(
								"td:eq(3)").text());
					} catch (Exception e) {
						throw e;
					}

					URI uriWebpage = null;

					try {
						uriWebpage = new URI(div.select("td:eq(0) a").attr("abs:href"));
					} catch (Exception e) {
						throw e;
					}

					String strIndexer = "Fenopy Europe";

					objTorrents.add(new Torrent(catCategory, strName, uriLocation,
							intSeeders, intLeechers, datDate, booPrivate,
							strIndexer, lngSize, booVerified, uriWebpage));

				} catch (Exception e) {

				    EasyTracker.getTracker().trackException(e.getMessage(), e, false);
					Log.w("scrapers.Fenopy", "Error parsing row", e);

				}

			}

		}

		Log.d("scrapers.Fenopy", String.format("Scraped %d rows", objTorrents.size()));

		Collections.reverse(objTorrents);

		return objTorrents;

	}

}