package com.tylerjchesley.creatures.provider;

import com.tylerjchesley.creatures.provider.CreaturesContract.CreaturesColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Author: Tyler Chesley
 */
public class CreaturesDatabase extends SQLiteOpenHelper {

//------------------------------------------
//  Constants
//------------------------------------------

    private static final String DATABASE_NAME = "creatures.db";

    private static final int DATABASE_VERSION = 1;

    interface Tables {

        String CREATURES = "creatures";

    }

//------------------------------------------
//  Constructors
//------------------------------------------

    public CreaturesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//------------------------------------------
//  Methods
//------------------------------------------

    /**---- SQLiteOpenHelper ----**/

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + Tables.CREATURES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CreaturesColumns.CREATURE_ID + " TEXT,"
                + CreaturesColumns.TITLE + " TEXT NOT NULL,"
                + CreaturesColumns.URL + " TEXT NOT NULL,"
                + CreaturesColumns.IMAGE + " TEXT NOT NULL,"
                + "UNIQUE (" + CreaturesColumns.CREATURE_ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
    }

}
