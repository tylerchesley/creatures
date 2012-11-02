package com.tylerjchesley.creatures.model;

import com.google.api.services.fusiontables.Fusiontables;

import java.io.IOException;

/**
 * Author: Tyler Chesley
 */
public class Creature {

//------------------------------------------
//  Constants
//------------------------------------------

    public static final String TITLE_COLUMN = "title";

    public static final String URL_COLUMN = "url";

    public static final String IMAGE_COLUMN = "image";

    public static final String CREATURES_TABLE_ID = "1976sLRAlK8q0ktrCHYXMtWL4XzTQQ0elilqkDtg";

//------------------------------------------
//  Variables
//------------------------------------------

    private String mTitle;

    private String mUrl;

//------------------------------------------
//  Constructor
//------------------------------------------

    public Creature(String title, String url) {
        mTitle = title;
        mUrl = url;
    }

//------------------------------------------
//  Methods:Properties
//------------------------------------------

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

//------------------------------------------
//  Methods
//------------------------------------------

    public void insert(Fusiontables client) throws IOException {
        try {
            client.query().sql("INSERT INTO " + CREATURES_TABLE_ID +
                    " (" + TITLE_COLUMN + ", "
                    + URL_COLUMN  + ", "
                    + IMAGE_COLUMN + ") VALUES ('"
                    + mTitle + "', '"
                    + mUrl + "', '"
                    + mUrl + "')").execute();
        } catch (IllegalArgumentException e) {
            // This exception is always thrown for 1.7.2 of the Fusion Tables API.
        }
    }

}
