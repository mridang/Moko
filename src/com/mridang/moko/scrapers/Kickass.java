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

import com.mridang.moko.enums.Category;
import com.mridang.moko.generics.Indexer;
import com.mridang.moko.helpers.DateConverter;
import com.mridang.moko.helpers.SizeConverter;
import com.mridang.moko.structures.Torrent;

/*
 * This class is used to search KickassTorrents
 */
public class Kickass extends Indexer {

	/* The URL of the scrape page */
	private final String SCRAPE_URL = "http://kat.ph/";

    /*
     * Initializes this task
     *
     * @param  ctxContext  the context of the calling class
     */
    public Kickass(Context ctxContext) {

        super(ctxContext);

    }

	/*
	 * This method is the method that searches the site.
	 *
	 * @return a list of torrents.
	 */
	public ArrayList<Torrent> doScrape() {

		Document objDocument;
		ArrayList<Torrent> objTorrents = new ArrayList<Torrent>();
        String strUrl = SCRAPE_URL;

		Log.d("scrapers.Kickass", String.format("URL: %s", strUrl));

		try {

			Log.d("scrapers.Kickass", "Fetching page");

			objDocument = Jsoup.parse(super.doGet(strUrl, null), strUrl);

		} catch (Exception e) {

			Log.w("scrapers.Kickass", "Error fetching and parsing page", e);
			return null;

		}

		for (Element eleTable : objDocument.select("table.data")) {

			Category catCategory = null;

			if (eleTable.parent().previousElementSibling().text().contains("Movies"))
				catCategory = Category.MOVIE;
			else if (eleTable.parent().previousElementSibling().text().contains("Music"))
				catCategory = Category.ALBUM;
			else if (eleTable.parent().previousElementSibling().text().contains("Show"))
				catCategory = Category.SHOW;
			else if (eleTable.parent().previousElementSibling().text().contains("Games"))
				catCategory = Category.GAME;
			else if (eleTable.parent().previousElementSibling().text().contains("Applications"))
				catCategory = Category.APP;
			else
				continue;

			for (Element div : eleTable.select("tr:gt(0)")) {

				Log.d("scrapers.Kickass", "Found a row");

				try {

					Boolean booPrivate = false;

					String strName = "";
					try {
						strName = div.select("div.torrentname a:eq(1)").text();
					} catch (Exception e) {
						throw e;
					}

					Date datDate = null;
					try {
						datDate = DateConverter.parseHumanDate(div.select("td:eq(3)").text());
					} catch (Exception e) {
						throw e;
					}

					Integer intSeeders = 0;
					try {
						intSeeders = Integer
								.parseInt(div.select("td:eq(4)").text());
					} catch (Exception e) {
						throw e;
					}

					Boolean booVerified = null;
					try {
						booVerified = div.select("a.iverify").first() == null ? false
								: true;
					} catch (Exception e) {
						throw e;
					}

					Integer intLeechers = 0;
					try {
						intLeechers = Integer.parseInt(div.select("td:eq(5)")
								.text());
					} catch (Exception e) {
						throw e;
					}

					URI uriLocation = null;
					try {
						uriLocation = new URI(div.select("a.idownload").last()
								.attr("abs:href").toString().split("\\?")[0]);
					} catch (Exception e) {
						throw e;
					}

					Long lngSize = null;
					try {
						lngSize = SizeConverter.parseSize(div.select(
								"td:eq(1)").text());
					} catch (Exception e) {
						throw e;
					}

					URI uriWebpage = null;

					try {
						uriWebpage = new URI(div.select("div.torrentname a:eq(1)").attr("abs:href"));
					} catch (Exception e) {
						throw e;
					}

					String strIndexer = "Kickass Torrents";

					objTorrents.add(new Torrent(catCategory, strName, uriLocation,
							intSeeders, intLeechers, datDate, booPrivate,
							strIndexer, lngSize, booVerified, uriWebpage));

				} catch (Exception e) {

					Log.w("scrapers.Kickass", "Error parsing row", e);

				}

			}

		}

		Log.d("scrapers.Kickass", String.format("Scraped %d rows", objTorrents.size()));

		Collections.reverse(objTorrents);

		return objTorrents;

	}

}