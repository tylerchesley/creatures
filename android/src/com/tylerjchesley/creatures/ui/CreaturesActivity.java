package com.tylerjchesley.creatures.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;
import com.tylerjchesley.creatures.ui.widget.CursorFragmentPagerAdapter;

public class CreaturesActivity extends CreaturesAuthActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

//------------------------------------------
//  Variables
//------------------------------------------

    private CreaturesPagerAdapter mAdapter;

    private View mProgress;

    private ViewPager mPager;

    private PagerTitleStrip mTitles;

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatures);

        mProgress = findViewById(R.id.progress);
        mTitles = (PagerTitleStrip) findViewById(R.id.titles);
        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new CreaturesPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_creatures, menu);
        return super.onCreateOptionsMenu(menu);
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
    protected void onAuthToken() {

    }

//------------------------------------------
//  Methods
//------------------------------------------

    private void onNewSelected() {
        final Intent intent = new Intent(Intent.ACTION_INSERT, Creatures.CONTENT_URI);
        startActivity(intent);
    }

    /**---- LoaderManager.LoaderCallbacks ----**/

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Creatures.CONTENT_URI,
                Creature.CONTENT_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        mTitles.requestLayout();
        mProgress.startAnimation(AnimationUtils.loadAnimation(
                this, android.R.anim.fade_out));
        mPager.startAnimation(AnimationUtils.loadAnimation(
                this, android.R.anim.fade_in));
        mProgress.setVisibility(View.GONE);
        mPager.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
//        mAdapter.swapCursor(null);
    }

//------------------------------------------
//  Inner Classes
//------------------------------------------

    public static final class CreaturesPagerAdapter extends CursorFragmentPagerAdapter {

        public CreaturesPagerAdapter(Context context, FragmentManager fm) {
            super(context, fm, null);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            final Cursor cursor = getCursor();
            if (cursor != null && cursor.moveToPosition(position)) {
                return cursor.getString(Creature.TITLE_INDEX);
            }
            return null;
        }

        @Override
        public Fragment getItem(Context context, Cursor cursor) {
            return CreatureFragment.newInstance(Creature.buildArgumentsFromCursor(cursor));
        }

    }

}
