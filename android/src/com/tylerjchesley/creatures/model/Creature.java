package com.tylerjchesley.creatures.model;

import android.os.Bundle;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.model.Sqlresponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
//  Static Methods
//------------------------------------------

    public static List<Creature> all(Fusiontables client) throws IOException{
        final Sqlresponse response = client.query().sql("SELECT " + TITLE_COLUMN + ", "
                + URL_COLUMN + " FROM " + CREATURES_TABLE_ID).execute();
        final List<List<Object>> rows = response.getRows();
        final List<Creature> creatures = new ArrayList<Creature>(rows.size());

        for (List<Object> row : rows) {
            final String title = (String) row.get(0);
            final String url = (String) row.get(1);
            creatures.add(new Creature(title, url));
        }

        return creatures;
    }

//------------------------------------------
//  Variables
//------------------------------------------

    private String mTitle;

    private String mUrl;

    private String mImage;

//------------------------------------------
//  Constructor
//------------------------------------------

    public Creature() {

    }

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

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

//------------------------------------------
//  Methods
//------------------------------------------

    public Bundle toArguments() {
        final Bundle arguments = new Bundle(2);
        arguments.putString(TITLE_COLUMN, mTitle);
        arguments.putString(URL_COLUMN, mUrl);
        return arguments;
    }

    public void insert(Fusiontables client) throws IOException {
        client.query().sql("INSERT INTO " + CREATURES_TABLE_ID +
                " (" + TITLE_COLUMN + ", "
                + URL_COLUMN  + ", "
                + IMAGE_COLUMN + ") VALUES ('"
                + mTitle + "', '"
                + mUrl + "', '"
                + mImage + "')").execute();
    }

}
