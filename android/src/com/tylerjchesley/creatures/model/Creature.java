package com.tylerjchesley.creatures.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;
import com.tylerjchesley.creatures.provider.CreaturesContract.CreaturesColumns;
import xxx.tylerchesley.android.record.RecordBase;

/**
 * Author: Tyler Chesley
 */
public class Creature extends RecordBase implements CreaturesColumns, BaseColumns {

//------------------------------------------
//  Constants
//------------------------------------------

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
        creature.setId(cursor.getLong(_ID_INDEX));
        creature.mTitle = cursor.getString(TITLE_INDEX);
        creature.mUrl = cursor.getString(URL_INDEX);
        creature.mImage = cursor.getString(IMAGE_INDEX);
        creature.mIsCreatureNew = cursor.getInt(IS_NEW_INDEX) == 1;
        creature.mIsFavorite = cursor.getInt(IS_FAVORITE_INDEX) == 1;
        return creature; 
    }
    
    public static Creature restoreCreature(Bundle arguments) {
        final Creature creature = new Creature();
        creature.setId(arguments.getLong(_ID));
        creature.mTitle = arguments.getString(TITLE);
        creature.mUrl = arguments.getString(URL);
        creature.mImage = arguments.getString(IMAGE);
        creature.mIsCreatureNew = arguments.getBoolean(IS_NEW);
        creature.mIsFavorite = arguments.getBoolean(IS_FAVORITE);
        return creature;
    }

//------------------------------------------
//  Variables
//------------------------------------------

    private String mTitle;

    private String mUrl;

    private String mImage;

    private boolean mIsCreatureNew;

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

    public boolean isCreatureNew() {
        return mIsCreatureNew;
    }

    public void setIsNew(boolean isNew) {
        mIsCreatureNew = isNew;
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

    @Override
    public void restore(Cursor cursor) {
        mTitle = cursor.getString(TITLE_INDEX);
        mUrl = cursor.getString(URL_INDEX);
        mImage = cursor.getString(IMAGE_INDEX);
        mIsCreatureNew = cursor.getInt(IS_NEW_INDEX) == 1;
        mIsFavorite = cursor.getInt(IS_FAVORITE_INDEX) == 1;
    }

    @Override
    public Uri toUri() {
        return isNew() ? Creatures.CONTENT_URI : Creatures.buildCreatureUri(getId());
    }

    @Override
    public ContentValues toValues() {
        final ContentValues values = new ContentValues(3);
        values.put(TITLE, mTitle);
        values.put(URL, mUrl);
        values.put(IMAGE, mImage);
        if (isNew()) {
            values.put(CREATED_AT, System.currentTimeMillis());
        }
        return values;
    }

}
