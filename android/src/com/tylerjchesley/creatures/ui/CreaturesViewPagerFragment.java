package com.tylerjchesley.creatures.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.util.UiUtils;
import xxx.tylerchesley.android.util.ImageFetcher;
import xxx.tylerchesley.android.view.CursorFragmentStatePagerAdapter;

/**
 * Author: Tyler Chesley
 */
public class CreaturesViewPagerFragment extends CreaturesFragment {

//------------------------------------------
//  Constants
//------------------------------------------

    public static final String SELECTED_CREATURE_POSITION = "selected_creature_position";

    private static ImageFetcher sImageFetcher;

    public static ImageFetcher getImageFetcher(FragmentActivity activity) {
        if (sImageFetcher == null) {
            sImageFetcher = UiUtils.getImageFetcher(activity);
        }

        return sImageFetcher;
    }

//------------------------------------------
//  Variables
//------------------------------------------

    private int mSelectedPosition;

    private CreaturesPagerAdapter mAdapter;

    private ViewPager mPager;

    private boolean mShouldSetPosition = true;

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedPosition = getArguments().getInt(SELECTED_CREATURE_POSITION, 0);
        if (savedInstanceState != null) {
            mSelectedPosition = savedInstanceState.getInt(
                    SELECTED_CREATURE_POSITION, mSelectedPosition);
        }

        getImageFetcher(getActivity());

        mAdapter = new CreaturesPagerAdapter(getFragmentManager());
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPager = new ViewPager(getActivity());
        mPager.setId(R.id.pager);
        mPager.setAdapter(mAdapter);
        return mPager;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_CREATURE_POSITION,
                mPager.getCurrentItem());
    }

//------------------------------------------
//  Methods
//------------------------------------------

    /**---- LoaderCallbacks ----**/

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        if (mShouldSetPosition) {
            mPager.setCurrentItem(mSelectedPosition, false);
            mShouldSetPosition = false;
        }
        setContentShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

//------------------------------------------
//  Inner Classes
//------------------------------------------

    static final class CreaturesPagerAdapter extends CursorFragmentStatePagerAdapter {

        public CreaturesPagerAdapter(FragmentManager fm) {
            super(fm, null, -1);
        }

        @Override
        public Fragment getItem(int position, Cursor cursor) {
            return CreatureFragment.newInstance(cursor);
        }

    }

}
