package com.example.testapp.plugins;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.util.Log;

import com.example.testapp.Search;
import com.example.testapp.enums.Category;
import com.example.testapp.generics.Indexer;
import com.example.testapp.helpers.DateConverter;
import com.example.testapp.helpers.SizeConverter;
import com.example.testapp.structures.Torrent;

/*
 * This class is used to search KickassTorrents
 */
public class Kickass extends Indexer  {

	/* The URL of the search page */
	private final String SEARCH_URL = "http://kat.ph/usearch/%s/";

    /*
     * Initializes this task
     *
     * @param  objContext  the instance of the calling Search class
     */
    public Kickass(Search objContext) {

        super(objContext);

    }

	/*
	 * This method is the method that searches the site.
	 *
	 * @param strQuery   the search string that should be used
	 * @param catSection the section in which to search
	 *
	 * @return a list of results.
	 */
	public ArrayList<Torrent> doSearch(String strQuery, Category catSection) {

		Document objDocument;
		ArrayList<Torrent> objResults = new ArrayList<Torrent>();
        String strUrl = SEARCH_URL;

		switch (catSection) {

			case ALBUM:
				strUrl = strUrl + "?categories[]=music";
				break;

			case APP:
				strUrl = strUrl + "?categories[]=applications";
				break;

			case EVERYTHING:
				strUrl = strUrl + "";
				break;

			case GAME:
				strUrl = strUrl + "?categories[]=games";
				break;

			case MOVIE:
				strUrl = strUrl + "?categories[]=movies";
				break;

			case SHOW:
				strUrl = strUrl + "?categories[]=tv";
				break;

			default:
				break;

		}

		Log.d("plugins.Kickass", String.format("Category: %s", catSection.name()));
		Log.d("plugins.Kickass", String.format("Query: %s", strQuery));
		Log.d("plugins.Kickass", String.format("URL: %s", strUrl));

		try {

			Log.d("plugins.Kickass", "Fetching page");

			objDocument = Jsoup.parse(
					super.doGet(String.format(strUrl,
							URLEncoder.encode(strQuery, "UTF-8")), null), strUrl);

		} catch (Exception e) {

			Log.w("plugins.Kickass", "Error fetching and parsing page", e);
			return null;

		}

		for (Element div : objDocument.select("table.data tr:gt(1)")) {

			Log.d("plugins.Kickass", "Found a row");

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
					booVerified = objDocument.select("a.iverify").first() == null ? false
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

				Category catCategory = null;
				try {
					catCategory = Category.getEnum(div.select(
							"span.lightgrey strong a:eq(0)").text());
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

				objResults.add(new Torrent(catCategory, strName, uriLocation,
						intSeeders, intLeechers, datDate, booPrivate,
						strIndexer, lngSize, booVerified, uriWebpage));

			} catch (Exception e) {

				Log.w("plugins.Kickass", "Error parsing row", e);

			}

		}

		Log.d("plugins.Kickass", String.format("Scraped %d rows", objResults.size()));

		Collections.reverse(objResults);

		return objResults;

	}

}