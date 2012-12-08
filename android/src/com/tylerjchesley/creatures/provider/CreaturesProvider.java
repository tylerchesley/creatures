package com.tylerjchesley.creatures.provider;

import android.database.sqlite.SQLiteDatabase;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import xxx.tylerchesley.android.util.SelectionBuilder;

import java.util.Arrays;

import static xxx.tylerchesley.android.util.LogUtils.LOGV;

/**
 * Author: Tyler Chesley
 */
public class CreaturesProvider extends ContentProvider {

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

    private SelectionBuilder buildSimpleSelection(Uri uri) {
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

    /**---- ContentProvider ----**/

    @Override
    public boolean onCreate() {
        mOpenHelper = new CreaturesDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String order) {
        LOGV(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        return builder.where(selection, selectionArgs).query(db, projection, order);
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

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sMatcher.match(uri);
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        switch (match) {
            case CREATURES: {
                final long id = database.insertOrThrow(Creatures.PATH, null, values);
                getContext().getContentResolver().notifyChange(uri, null, true);
                return Creatures.buildCreatureUri(id);
            }
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LOGV(TAG, "delete(uri=" + uri + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null, true);
        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LOGV(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null, true);
        return retVal;
    }

}
