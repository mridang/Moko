package com.example.testapp.enums;

/*
 * This is a list of categories under which the torrents
 * are categorised.
 */
public enum Category {

    UNKNOWN, MOVIE, ALBUM, GAME, BOOK, SHOW, APP, MISC, EVERYTHING;

    /*
     * This methods returns the enum corresponding to a string
     *
     * @param   strCategory  The string description of the event
     * @return  The enum associted with the string
     */
    public static Category getEnum(String strCategory) {

        if (strCategory.toUpperCase().equals("MOVIES")) {
            return MOVIE;
        } else if (strCategory.toUpperCase().equals("MUSIC")) {
            return ALBUM;
        } else if (strCategory.toUpperCase().equals("TV")) {
            return SHOW;
        } else if (strCategory.toUpperCase().equals("GAMES")) {
            return GAME;
        } else if (strCategory.toUpperCase().equals("APPLICATIONS")) {
            return APP;
        } else {
            return UNKNOWN;
        }

    }

}