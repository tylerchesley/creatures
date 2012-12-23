package com.tylerjchesley.creatures.provider;

import android.content.UriMatcher;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;
import xxx.tylerchesley.android.content.BaseContentProvider;
import xxx.tylerchesley.android.util.SelectionBuilder;

/**
 * Author: Tyler Chesley
 */
public class CreaturesProvider extends BaseContentProvider {

//------------------------------------------
//  Constants
//------------------------------------------

    private static final String TAG = "CreaturesProvider";

    private static final int CREATURES = 100;
    private static final int CREATURES_ID = 101;

    private static final UriMatcher sMatcher = buildUriMatcher();

//------------------------------------------
//  Static Methods
//------------------------------------------

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CreaturesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, Creatures.PATH, CREATURES);
        matcher.addURI(authority, Creatures.PATH + "/*", CREATURES_ID);

        return matcher;
    }

//------------------------------------------
//  Variables
//------------------------------------------

    private CreaturesDatabase mOpenHelper;

//------------------------------------------
//  Methods
//------------------------------------------

    /**---- ContentProvider ----**/

    @Override
    public boolean onCreate() {
        mOpenHelper = new CreaturesDatabase(getContext());
        return true;
    }

    @Override
    protected SQLiteOpenHelper getOpenHelper() {
        return mOpenHelper;
    }

    @Override
    protected SelectionBuilder buildSelection(Uri uri) {
        final int match = sMatcher.match(uri);
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case CREATURES:
                builder.table(Creatures.PATH);
                break;
            case CREATURES_ID:
                final String creatureId = Creatures.getCreatureId(uri);
                builder.table(Creatures.PATH).where(Creatures._ID + " = " + creatureId);
                break;
        }
        return builder;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sMatcher.match(uri);
        switch (match) {
            case CREATURES:
                return Creatures.CONTENT_TYPE;

            case CREATURES_ID:
                return Creatures.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

}
