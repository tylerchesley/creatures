package com.tylerjchesley.creatures.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.model.Sqlresponse;
import com.tylerjchesley.creatures.provider.CreaturesContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Tyler Chesley
 */
public class Creature implements CreaturesContract.CreaturesColumns, BaseColumns {

//------------------------------------------
//  Constants
//------------------------------------------

    public static final String CREATURES_TABLE_ID = "1976sLRAlK8q0ktrCHYXMtWL4XzTQQ0elilqkDtg";

    public static final String[] CONTENT_PROJECTION = {
            _ID,
            TITLE,
            URL,
            IMAGE,
            IS_NEW,
            IS_FAVORITE,
            CREATED_AT
    };

    public static final int _ID_INDEX = 0;

    public static final int TITLE_INDEX = 1;

    public static final int URL_INDEX = 2;

    public static final int IMAGE_INDEX = 3;

    public static final int IS_NEW_INDEX = 4;

    public static final int IS_FAVORITE_INDEX = 5;

    public static final int CREATED_AT_INDEX = 6;

//------------------------------------------
//  Static Methods
//------------------------------------------

    public static List<Creature> all(Fusiontables client) throws IOException{
        final Sqlresponse response = client.query().sql("SELECT " + TITLE + ", "
                + URL + ", " + IMAGE + " FROM " + CREATURES_TABLE_ID).execute();
        final List<List<Object>> rows = response.getRows();
        final List<Creature> creatures = new ArrayList<Creature>(rows.size());

        for (List<Object> row : rows) {
            final String title = (String) row.get(0);
            final String url = (String) row.get(1);
            creatures.add(new Creature(title, url));
        }

        return creatures;
    }

    public static Bundle buildArgumentsFromCursor(Cursor cursor) {
        final Bundle args = new Bundle();
        args.putLong(_ID, cursor.getLong(_ID_INDEX));
        args.putString(TITLE, cursor.getString(TITLE_INDEX));
        args.putString(URL, cursor.getString(URL_INDEX));
        args.putString(IMAGE, cursor.getString(IMAGE_INDEX));
        args.putBoolean(IS_NEW, cursor.getInt(IS_NEW_INDEX) == 1);
        args.putBoolean(IS_FAVORITE, cursor.getInt(IS_FAVORITE_INDEX) == 1);
        return args;
    }

    public static Creature restoreCreature(Cursor cursor) {
        final Creature creature = new Creature();
        creature.mId = cursor.getLong(_ID_INDEX);
        creature.mTitle = cursor.getString(TITLE_INDEX);
        creature.mUrl = cursor.getString(URL_INDEX);
        creature.mImage = cursor.getString(IMAGE_INDEX);
        creature.mIsNew = cursor.getInt(IS_NEW_INDEX) == 1;
        creature.mIsFavorite = cursor.getInt(IS_FAVORITE_INDEX) == 1;
        return creature; 
    }
    
    public static Creature restoreCreature(Bundle arguments) {
        final Creature creature = new Creature();
        creature.mId = arguments.getLong(_ID);
        creature.mTitle = arguments.getString(TITLE);
        creature.mUrl = arguments.getString(URL);
        creature.mImage = arguments.getString(IMAGE);
        creature.mIsNew = arguments.getBoolean(IS_NEW);
        creature.mIsFavorite = arguments.getBoolean(IS_FAVORITE);
        return creature;
    }

//------------------------------------------
//  Variables
//------------------------------------------

    private long mId;

    private String mTitle;

    private String mUrl;

    private String mImage;

    private boolean mIsNew;

    private boolean mIsFavorite;

    private long mCreatedAt;

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

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

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

    public boolean isNew() {
        return mIsNew;
    }

    public void setIsNew(boolean isNew) {
        mIsNew = isNew;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        mIsFavorite = isFavorite;
    }

//------------------------------------------
//  Methods
//------------------------------------------

    public Bundle toArguments() {
        final Bundle arguments = new Bundle(2);
        arguments.putString(TITLE, mTitle);
        arguments.putString(URL, mUrl);
        arguments.putString(IMAGE, mImage);
        return arguments;
    }

    public ContentValues toValues() {
        final ContentValues values = new ContentValues(3);
        values.put(TITLE, mTitle);
        values.put(URL, mUrl);
        values.put(IMAGE, mImage);
        values.put(CREATED_AT, System.currentTimeMillis());
        return values;
    }

    public void insert(Fusiontables client) throws IOException {
        client.query().sql("INSERT INTO " + CREATURES_TABLE_ID +
                " (" + TITLE + ", "
                + URL  + ", "
                + IMAGE + ") VALUES ('"
                + mTitle + "', '"
                + mUrl + "', '"
                + mImage + "')").execute();
    }

}
