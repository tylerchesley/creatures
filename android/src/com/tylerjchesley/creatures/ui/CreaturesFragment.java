package com.tylerjchesley.creatures.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import com.tylerjchesley.creatures.model.Creature;
import com.tylerjchesley.creatures.provider.CreaturesContract;
import xxx.tylerchesley.android.app.ContentFragment;

/**
 * Author: Tyler Chesley
 */
public abstract class CreaturesFragment extends ContentFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

//------------------------------------------
//  Constants
//------------------------------------------

    public static final int CREATURES_LOADER = 0x0;

    public static final String SELECTED_FILTER = "selected_filter";

    public static final int FILTER_ALL = 0;

    public static final int FILTER_NEW = 1;

    public static final int FILTER_FAVORITES = 2;

//------------------------------------------
//  Variables
//------------------------------------------

    private int mFilter = FILTER_ALL;

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFilter = getArguments() != null ?
                getArguments().getInt(SELECTED_FILTER, FILTER_ALL) : FILTER_ALL;

        if (savedInstanceState != null) {
            mFilter = savedInstanceState.getInt(SELECTED_FILTER, FILTER_ALL);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(CREATURES_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_FILTER, mFilter);
    }

//------------------------------------------
//  Methods
//------------------------------------------

    public int getFilter() {
        return mFilter;
    }

    public void setFilter(int filter) {
        if (mFilter == filter) {
            return;
        }

        mFilter = filter;
        if (isAdded()) {
            setContentShown(false);
            getLoaderManager().restartLoader(CREATURES_LOADER, null, this);
        }
    }

    /**--- LoaderManager.Callbacks ---**/

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String selection = null;
        if (mFilter == FILTER_NEW) {
            selection = CreaturesContract.Creatures.IS_NEW + " = 1";
        }
        else if (mFilter == FILTER_FAVORITES) {
            selection = CreaturesContract.Creatures.IS_FAVORITE + " = 1";
        }

        return new CursorLoader(getActivity(), CreaturesContract.Creatures.CONTENT_URI,
                Creature.CONTENT_PROJECTION, selection, null, CreaturesContract.Creatures.CREATED_AT + " DESC");
    }

}
