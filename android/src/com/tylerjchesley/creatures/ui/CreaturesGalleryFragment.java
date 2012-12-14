package com.tylerjchesley.creatures.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;
import com.tylerjchesley.creatures.ui.widget.CreatureThumbnailImageView;
import com.tylerjchesley.creatures.util.CreaturesHelper;
import com.tylerjchesley.creatures.util.UiUtils;
import xxx.tylerchesley.android.util.ImageFetcher;

import java.util.ArrayList;

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

    private static void logData(Cursor cursor) {
        final ArrayList<String> items = new ArrayList<String>(cursor.getCount());

        if (!cursor.moveToFirst()) {
            return;
        }

        while (!cursor.isAfterLast()) {
            final ArrayList<String> item = new ArrayList<String>();
            item.add(cursor.getString(Creature.TITLE_INDEX));
            item.add(cursor.getString(Creature.URL_INDEX));
            item.add(cursor.getString(Creature.IMAGE_INDEX));
            item.add(cursor.getString(Creature.IS_NEW_INDEX));
            item.add(cursor.getString(Creature.IS_FAVORITE_INDEX));
            item.add(cursor.getString(Creature.CREATED_AT_INDEX));
            items.add('"' + TextUtils.join("~", item) + '"');

            cursor.moveToNext();
        }

        final String s = TextUtils.join(",\n", items);
        Log.d("XXXXXXXXX", s);
    }

//------------------------------------------
//  Variables
//------------------------------------------

    private Creature mLongClickedCreature;

    private View mLongClickedView;

    private ActionMode mActionMode;

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

    private final AdapterView.OnItemLongClickListener mOnLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            // Called when the user long-clicks on someView
            if (mActionMode != null) {
                return false;
            }

            final Cursor cursor = mAdapter.getCursor();
            if (!cursor.moveToPosition(position)) {
                return false;
            }

            mLongClickedCreature = Creature.restoreCreature(cursor);
            mLongClickedView = view;
            mLongClickedView.setActivated(true);
            // Start the CAB using the ActionMode.Callback defined above
            mActionMode = ((SherlockFragmentActivity) getActivity()).startActionMode(mActionModeCallback);
            return true;
        }
    };

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            final MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_creature, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (mLongClickedCreature == null) {
                return false;
            }

            switch (item.getItemId()) {
                case R.id.menu_favorite:
                    CreaturesHelper.setCreatureFavorited(getActivity(),
                            mLongClickedCreature.getId(), !mLongClickedCreature.isFavorite());
                    mode.finish();
                    return true;

                case R.id.menu_share:
                    CreaturesHelper.shareCreature(getActivity(),
                            mLongClickedCreature.getTitle(), mLongClickedCreature.getUrl());
                    mode.finish();
                    return true;

                case R.id.menu_website:
                    CreaturesHelper.openCreatureWebsite(getActivity(), mLongClickedCreature.getUrl());
                    mode.finish();
                    return true;

                case R.id.menu_edit:
                    return true;

                case R.id.menu_delete:
                    CreaturesHelper.maybeDeleteCreature(getActivity(), mLongClickedCreature.getId());
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mLongClickedCreature = null;

            if (mLongClickedView != null) {
                mLongClickedView.setActivated(false);
                mLongClickedView = null;
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
        mGridView.setOnItemLongClickListener(mOnLongClickListener);
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
//        logData(cursor);
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
                    imageView, R.drawable.creature_thumbnail_placeholder);
            imageView.setIsFavorite(cursor.getInt(Creature.IS_FAVORITE_INDEX) == 1);
            imageView.setIsNew(cursor.getInt(Creature.IS_NEW_INDEX) == 1);
        }

    }

}
