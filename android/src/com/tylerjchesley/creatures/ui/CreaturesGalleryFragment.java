package com.tylerjchesley.creatures.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;
import xxx.tylerchesley.android.app.ContentFragment;
import xxx.tylerchesley.android.util.ImageFetcher;
import xxx.tylerchesley.android.util.UIUtils;

/**
 * Author: Tyler Chesley
 */
public class CreaturesGalleryFragment extends ContentFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, AbsListView.OnScrollListener {

//------------------------------------------
//  Interfaces
//------------------------------------------

    public static interface OnCreatureSelectedListener {

        void onCreatureSelected(long id, Bundle creature);

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
                mCreatureSelectedListener.onCreatureSelected(id,
                        Creature.buildArgumentsFromCursor(cursor));
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

        mImageFetcher = UIUtils.getImageFetcher(getActivity());
        mImageFetcher.setImageFadeIn(false);

        mAdapter = new CreaturesAdapter(getActivity());

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_creatures, menu);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGridView = new GridView(getActivity());
        mGridView.setNumColumns(GridView.AUTO_FIT);
        mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mGridView.setGravity(Gravity.CENTER);
        mGridView.setOnScrollListener(this);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(mOnClickListener);
        return mGridView;
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), Creatures.CONTENT_URI,
                Creature.CONTENT_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        setContentShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

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
            final ImageView view = new ImageView(mContext);
            view.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mGridView.getColumnWidth()));
            view.setScaleType(ImageView.ScaleType.CENTER);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            mImageFetcher.loadImage(cursor.getString(Creature.IMAGE_INDEX),
                    (ImageView) view, R.drawable.background_leopard_skin);
        }

    }

}
