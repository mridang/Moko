package com.example.testapp.interfaces;

/*
 * This is an interface for the torrent results and categories.
 */
public interface Row {

    /*
     * This variable keeps the state of the row
     */
    void setExpanded(Boolean booExpanded);

    /*
     * This method should return the expanded state of the row
     */
    Boolean isExpanded();

}
