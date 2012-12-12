package com.tylerjchesley.creatures.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;
import com.tylerjchesley.creatures.ui.widget.CreatureThumbnailImageView;
import com.tylerjchesley.creatures.util.UiUtils;
import xxx.tylerchesley.android.util.ImageFetcher;

/**
 * Author: Tyler Chesley
 */
public class CreaturesGalleryFragment extends CreaturesFragment implements
        AbsListView.OnScrollListener {

//------------------------------------------
//  Interfaces
//------------------------------------------

    public static interface OnCreatureSelectedListener {

        void onCreatureSelected(int position, int filter);

    }

//------------------------------------------
//  Variables
//------------------------------------------

    private ImageFetcher mImageFetcher;

    private GridView mGridView;

    private CreaturesAdapter mAdapter;

    private OnCreatureSelectedListener mCreatureSelectedListener;

    private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            final Cursor cursor = mAdapter.getCursor();
            if (cursor.moveToPosition(position)) {
                mCreatureSelectedListener.onCreatureSelected(position, getFilter());
            }
        }

    };

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof OnCreatureSelectedListener)) {
            throw new ClassCastException("Activity must implement OnCreatureSelectedListener.");
        }

        mCreatureSelectedListener = (OnCreatureSelectedListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageFetcher = UiUtils.getImageFetcher(getActivity());
        mImageFetcher.setImageFadeIn(false);

        mAdapter = new CreaturesAdapter(getActivity());

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_creatures, menu);

        final MenuItem item = menu.findItem(R.id.menu_search);
        final SearchView searchView = new SearchView(getActivity());
        searchView.setQueryHint(getString(R.string.hint_search));
        item.setActionView(searchView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new:
                onNewSelected();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_creatures_gallery, container, false);
        final View emptyView = view.findViewById(R.id.empty);
        mGridView = (GridView) view.findViewById(R.id.grid);
        mGridView.setEmptyView(emptyView);
        mGridView.setOnScrollListener(this);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(mOnClickListener);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mImageFetcher != null) {
            mImageFetcher.closeCache();
        }
    }

//------------------------------------------
//  Methods
//------------------------------------------

    private void onNewSelected() {
        final Intent intent = new Intent(Intent.ACTION_INSERT, Creatures.CONTENT_URI);
        startActivity(intent);
    }

    /**---- LoaderCallbacks ----**/

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        setContentShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**---- OnScrollListener ----**/

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
        // Pause disk cache access to ensure smoother scrolling
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING ||
                scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            mImageFetcher.setPauseDiskCache(true);
        } else {
            mImageFetcher.setPauseDiskCache(false);
        }
    }

    @Override
    public void onScroll(AbsListView view, int i, int i1, int i2) {
    }

//------------------------------------------
//  Inner Classes
//------------------------------------------

    final class CreaturesAdapter extends CursorAdapter  {

        public CreaturesAdapter(Context context) {
            super(context, null, true);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            final int columnWidth = mGridView.getColumnWidth();
            final CreatureThumbnailImageView view = new CreatureThumbnailImageView(context);
            view.setLayoutParams(new GridView.LayoutParams(columnWidth,
                    columnWidth));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final CreatureThumbnailImageView imageView = (CreatureThumbnailImageView) view;
            mImageFetcher.setImageSize(mGridView.getColumnWidth());
            mImageFetcher.loadImage(cursor.getString(Creature.IMAGE_INDEX),
                    imageView);
            imageView.setIsFavorite(cursor.getInt(Creature.IS_FAVORITE_INDEX) == 1);
            imageView.setIsNew(cursor.getInt(Creature.IS_NEW_INDEX) == 1);
        }

    }

}
