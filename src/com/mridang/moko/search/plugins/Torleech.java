package com.mridang.moko.search.plugins;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.mridang.moko.enums.Category;
import com.mridang.moko.generics.Indexer;
import com.mridang.moko.helpers.SizeConverter;
import com.mridang.moko.search.Search;
import com.mridang.moko.structures.Torrent;

/*
 * This class is used to search Torleech
 */
public class Torleech extends Indexer {

	/* The URL of the search page */
	private final String SEARCH_URL = "http://www.torrentleech.org/torrents/browse/index/query/%s/";
	/* The URL of the login page */
	private final String LOGIN_URL = "http://www.torrentleech.org/user/account/login/";

    /*
     * Initializes this task
     *
     * @param  objContext  the instance of the calling Search class
     */
    public Torleech(Search objContext) {

        super(objContext);

    }

    /*
     * This method checks if the user is logged in by checking for the presence
     * of the login form on the page.
     *
     * @param  objDocument the Jsoup document that will be checked for the element
     * @return Boolean     a boolean value indicating the result of the check
     */
    private Boolean isLoggedIn(Document objDocument) {

    	return (objDocument.select("div#loginform").first() == null) ? true : false;

    }

	/*
	 * This method log the user in to the site
	 *
	 * @param  strUsername The username to use for logging in
	 * @param  strPassword The password to use for logging in
	 * @return A Boolean value indicating whether the login was successful
	 */
	private void logIn(final String strUsername, final String strPassword) throws Indexer.LoginException {

		try {

			Log.d("plugins.Torleech", "Logging in");

		    List<NameValuePair> lstCredentials = new ArrayList<NameValuePair>();

		    lstCredentials.add(new NameValuePair() {

				public String getName() {
					return "username";
				}

				public String getValue() {
					return strUsername;
				}

			});

		    lstCredentials.add(new NameValuePair() {

				public String getName() {
					return "password";
				}

				public String getValue() {
					return strPassword;
				}

			});

		    lstCredentials.add(new NameValuePair() {

				public String getName() {
					return "login";
				}

				public String getValue() {
					return "submit";
				}

			});

		    lstCredentials.add(new NameValuePair() {

				public String getName() {
					return "remember_me";
				}

				public String getValue() {
					return "on";
				}

			});

			String strResponse = super.doPost(this.LOGIN_URL, lstCredentials);
			Document objDocument = Jsoup.parse(strResponse);

			Log.d("plugins.Torleech", "Tried to log in");

			if (this.isLoggedIn(objDocument) == false) {

				Log.e("plugins.Torleech", "Unable to login");
				throw new Indexer.LoginException("");

			}

		} catch (Indexer.LoginException e) {

			Log.e("plugins.Torleech", "There was an error logging in", e);
			throw e;

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	/*
	 * This method is the method that searches the site.
	 *
	 * @param  strQuery   the search string that should be used
	 * @param  catSection the section in which to search
	 * @param  strUsername The username to use for logging in
	 * @param  strPassword The password to use for logging in
	 * @return A list of results scraped from the site.
	 */
	public ArrayList<Torrent> doSearch(String strQuery, Category catSection, String strUsername, String strPassword) throws Exception {

		Document objDocument;
		ArrayList<Torrent> objResults = new ArrayList<Torrent>();
        String strUrl = SEARCH_URL;

		switch (catSection) {

			case ALBUM:
				strUrl = strUrl + "categories/4,31";
				break;

			case APP:
				strUrl = strUrl + "categories/6,23,24,25,33";
				break;

			case EVERYTHING:
				strUrl = strUrl + "";
				break;

			case GAME:
				strUrl = strUrl + "categories/3,17,18,19,20,21,22,28,30";
				break;

			case MOVIE:
				strUrl = strUrl + "categories/1,8,9,10,11,12,13,14,15,29";
				break;

			case SHOW:
				strUrl = strUrl + "categories/2,26,27,32";
				break;

			default:
				break;

		}

		Log.d("plugins.Torleech", String.format("Category: %s", catSection.name()));
		Log.d("plugins.Torleech", String.format("Query: %s", strQuery));
		Log.d("plugins.Torleech", String.format("URL: %s", strUrl));

		try {

			Log.d("plugins.Torleech", "Fetching page");

			objDocument = Jsoup.parse(super.doGet(
					String.format(strUrl,
							URLEncoder.encode(strQuery, "UTF-8")), null), strUrl);

		} catch (Exception e) {

		    EasyTracker.getTracker().trackException(e.getMessage(), e, false);
			Log.w("plugins.Torleech", "Error fetching and parsing page", e);
			throw e;

		}

		Log.d("plugins.Torleech", "Checking for authentication status");

		if (this.isLoggedIn(objDocument) == false) {

			Log.d("plugins.Torleech", "Not authenticated");
			this.logIn(strUsername, strPassword);

			try {

				Log.d("plugins.Torleech", "Fetching page again");

				objDocument = Jsoup.parse(super.doGet(
						String.format(strUrl,
								URLEncoder.encode(strQuery, "UTF-8")), null));

			} catch (Exception e) {

			    EasyTracker.getTracker().trackException(e.getMessage(), e, false);
				Log.w("plugins.Torleech", "Error fetching and parsing page", e);
				throw e;

			}

		}

		Log.d("plugins.Torleech", "Authenticated");

		for (Element div : objDocument.select("table#torrenttable tbody tr")) {

			Log.d("plugins.Kickass", "Found a row");

			try {

				Boolean booPrivate = true;

				String strName = "";
				try {
					strName = div.select("span.title a").text();
				} catch (Exception e) {
					throw e;
				}

				Date datDate = null;
				try {
					datDate = new Date();
					System.out.println(div.select("td.name").text());
					//TODO System.out.println(div.select("td.name").text().replaceAll(".*(\\d\\d\\d\\d-\\d\\d-\\d\\d).*", "x"));
				} catch (Exception e) {
					throw e;
				}

				Integer intSeeders = 0;
				try {
					intSeeders = Integer
							.parseInt(div.select("td:eq(6)").text());
				} catch (Exception e) {
					throw e;
				}

				Integer intLeechers = 0;
				try {
					intLeechers = Integer.parseInt(div.select("td:eq(7)")
							.text());
				} catch (Exception e) {
					throw e;
				}

				Boolean booVerified = true;

				URI uriLocation = null;
				try {
					uriLocation = new URI(div.select("td.quickdownload a").last()
							.attr("abs:href"));
				} catch (Exception e) {
					throw e;
				}

				Category catCategory = null;
				try {
					catCategory = Category.getEnum((div.select("td.name b")
							.text().replaceAll("\\s\\:.*", "")));
				} catch (Exception e) {
					throw e;
				}

				Long lngSize = null;
				try {
					lngSize = SizeConverter.parseSize(div.select(
							"td:eq(4)").text());
				} catch (Exception e) {
					throw e;
				}

				URI uriWebpage = null;

				try {
					uriWebpage = new URI(div.select("span.title a").attr("abs:href"));
				} catch (Exception e) {
					throw e;
				}

				String strIndexer = "Torrent Leech";

				objResults.add(new Torrent(catCategory, strName, uriLocation,
						intSeeders, intLeechers, datDate, booPrivate,
						strIndexer, lngSize, booVerified, uriWebpage));

			} catch (Exception e) {

			    EasyTracker.getTracker().trackException(e.getMessage(), e, false);
				Log.w("plugins.Torleech", "Error parsing row", e);

			}

		}

		Log.d("plugins.Torleech", String.format("Scraped %d rows", objResults.size()));

		Collections.reverse(objResults);

		return objResults;

	}

}