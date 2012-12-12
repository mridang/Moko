package com.mridang.moko.structures;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import com.mridang.moko.enums.Category;
import com.mridang.moko.interfaces.Row;

/*
 * This class is used like a structure to contain a
 * result item. We can also serialise this.
 */
public class Torrent implements Row, Serializable {

    /*
     * A serialization identifier
     */
    private static final long serialVersionUID = -2977539441063094354L;
    /*
     * The name of the torrent
     */
    private final String strName;
    /*
     * The number of seeders
     */
    private final Integer intSeeders;
    /*
     * The number of leechers
     */
    private final Integer intLeechers;
    /*
     * The URI of the torrent file
     */
    private final URI uriLocation;
    /*
     * The date of the release
     */
    private final Date datDate;
    /*
     * The type of the torrent
     */
    private final Category catCategory;
    /*
     * The visibility of the torrent
     */
    private final Boolean booPrivate;
    /*
     * The name of the indexer
     */
    private final String strIndexer;
    /*
     * The size of the torrent
     */
    private final Long lngSize;
    /*
     * The verification state of the torrent
     */
    private final Boolean booVerified;
    /*
     * The webpage of the torrent
     */
    private final URI uriWebpage;
    /*
     * The expanded state of the torrent
     */
    private Boolean booExpanded = false;

    /*
     * This returns the expanded state.
     *
     * @return  a Boolean value indicating the state
     */
    public Boolean isExpanded() {

        return this.booExpanded;

    }

    /*
     * This returns the name.
     *
     * @return  the name of the torrent
     */
    public String getName() {

        return this.strName;

    }

    /*
     * This returns the URI of the torrent file
     *
     * @return  the URI of the torrent
     */
    public URI getLocation() {

        return this.uriLocation;

    }

    /*
     * This returns the release date
     *
     * @return  the date of the release
     */
    public Date getDate() {

        return this.datDate;

    }

    /*
     * This returns the visibility of the torrent
     *
     * @return  the date of the release
     */
    public Boolean isPrivate() {

        return this.booPrivate;

    }

    /*
     * This returns the visibility of the torrent
     *
     * @return the visibility of the torrent
     */
    public String getVisibility() {

        if (this.booPrivate == true) {
            return "Private";
        } else {
            return "Public";
        }

    }

    /*
     * This returns the verification of the torrent
     *
     * @return the verification of the torrent
     */
    public String getVerification() {

        if (this.booVerified == true) {
            return "Verified";
        } else {
            return "Unverified";
        }

    }

    /*
     * This initialises the instance variables
     *
     * @return
     */
    public Torrent(Category catCategory, String strName, URI uriLocation,
            Integer intSeeders, Integer intLeechers, Date datDate,
            Boolean booPrivate, String strIndexer, Long lngSize, Boolean booVerified, URI uriWebpage) {

        this.catCategory = catCategory;
        this.strName = strName;
        this.uriLocation = uriLocation;
        this.intSeeders = intSeeders;
        this.intLeechers = intLeechers;
        this.datDate = datDate;
        this.booPrivate = booPrivate;
        this.strIndexer = strIndexer;
        this.lngSize = lngSize;
        this.booVerified = booVerified;
        this.uriWebpage = uriWebpage;

    }

    /*
     * This returns the quality of the torrent
     *
     * @return the quality of the torrent
     */
    public String getQuality() {

        if (this.lngSize< 4831838208L) {
            return "LQ";
        } else {
            return "HQ";
        }

    }

    /*
     * This returns the verification of the torrent
     *
     * @return  the verification of the torrent
     */
    public Boolean isVerified() {

        return this.booVerified;

    }

    /*
     * This returns the name of the indexer
     *
     * @return  the name of the indexer
     */
    public String getIndexer() {

        return this.strIndexer;

    }

    /*
     * This returns the name of the site
     *
     * @return  the name of the site
     */
    public String getSite() {

        return this.strIndexer;

    }

    /*
     * This returns the number of seeders
     *
     * @return  the number of seeders
     */
    public Integer getSeeders() {

        return this.intSeeders;

    }

    /*
     * This returns the category of the torrent
     *
     * @return  the category of the torrent
     */
    public Category getCategory() {

        return this.catCategory;

    }

    /*
     * This returns the number of leechers
     *
     * @return  the number of leechers
     */
    public Integer getLeechers() {

        return this.intLeechers;

    }

    /*
     * This returns the size of the torrent
     *
     * @return  the size of the torrent
     */
    public Long getSize() {

        return this.lngSize;

    }

    /*
     * This returns the webpage of the torrent
     *
     * @return  the webpage of the torrent
     */
    public URI getWebpage() {

        return this.uriWebpage;

    }

    /*
     * This sets the expanded state of the row
     *
     * @param  booExpanded  the state of the row
     */
    public void setExpanded(Boolean booExpanded) {

        this.booExpanded = booExpanded;

    }

}