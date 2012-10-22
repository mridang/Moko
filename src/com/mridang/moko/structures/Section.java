package com.mridang.moko.structures;

import com.mridang.moko.interfaces.Row;

/*
 * This class is used to indicate a category.
 */
public class Section implements Row {

    /*
     * The name of the category
     */
    private final String strName;

    /*
     * This sets the expanded state of the row
     *
     * @param  booExpanded  the state of the row
     */
    public void setExpanded(Boolean booExpanded) {

        return;

    }

    /*
     * This initializes the instance variables
     *
     * @return
     */
    public Section(String strName) {

        this.strName = strName;

    }

    /*
     * This returns the name.
     *
     * @return  the name of the category
     */
    public String getName() {

        return this.strName;

    }

    /*
     * This returns the expanded state of the row
     *
     * @return  the expanded state of the section
     */
    public Boolean isExpanded() {

        return false;
    }

}
