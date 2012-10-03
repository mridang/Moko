package com.mridang.huntr.providers;

import android.app.SearchManager;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.mridang.huntr.R;
import com.mridang.huntr.Search;

/*
 * This is custom provider for the search view.
 */
public class QuickSearchProvider extends SearchRecentSuggestionsProvider {

	/*
	 * The unique name of the search provider
	 */
	public final static String AUTHORITY = "com.mridang.huntr.providers.QuickSearchProvider";
	/*
	 * The type of search suggestions to return
	 */
	public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

	/*
	 * @see android.content.SearchRecentSuggestionsProvider#
	 * SearchRecentSuggestionsProvider()
	 */
	public QuickSearchProvider() {

		setupSuggestions(AUTHORITY, MODE);

	}

	/*
	 * @see
	 * android.content.SearchRecentSuggestionsProvider#query(android.net.Uri,
	 * java.lang.String[], java.lang.String, java.lang.String[],
	 * java.lang.String)
	 */
	@Override
    public Cursor query(Uri uriProvider, String[] strProjection,
            String strSelection, String[] lstArguments, String strSort) {

        MatrixCursor mcuCursor = new MatrixCursor(new String[] { BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_QUERY,
                SearchManager.SUGGEST_COLUMN_INTENT_ACTION });

        mcuCursor.addRow(new Object[] {
                0,
                this.getContext().getResources().getString(R.string.movies),
                this.getContext().getResources()
                        .getString(R.string.search_for_movies),
                android.R.drawable.ic_menu_search, lstArguments[0],
                Search.SEARCH_MOVIES });

        mcuCursor.addRow(new Object[] {
                1,
                this.getContext().getResources().getString(R.string.music),
                this.getContext().getResources()
                        .getString(R.string.search_for_music),
                android.R.drawable.ic_menu_search, lstArguments[0],
                Search.SEARCH_MUSIC });

        mcuCursor.addRow(new Object[] {
                2,
                this.getContext().getResources().getString(R.string.games),
                this.getContext().getResources()
                        .getString(R.string.search_for_games),
                android.R.drawable.ic_menu_search, lstArguments[0],
                Search.SEARCH_GAMES });

        mcuCursor.addRow(new Object[] {
                3,
                this.getContext().getResources()
                        .getString(R.string.applications),
                this.getContext().getResources()
                        .getString(R.string.search_for_applications),
                android.R.drawable.ic_menu_search, lstArguments[0],
                Search.SEARCH_APPS });

        mcuCursor.addRow(new Object[] {
                4,
                this.getContext().getResources().getString(R.string.shows),
                this.getContext().getResources()
                        .getString(R.string.search_for_shows),
                android.R.drawable.ic_menu_search, lstArguments[0],
                Search.SEARCH_SHOWS });

        return mcuCursor;

    }

}
