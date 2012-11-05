package com.tylerjchesley.creatures.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.google.api.services.fusiontables.Fusiontables;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;

import java.io.IOException;
import java.util.List;

public class CreaturesActivity extends CreaturesAuthActivity implements
        LoaderManager.LoaderCallbacks<List<Creature>> {

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
        mAdapter = new CreaturesPagerAdapter(getFragmentManager());
        mPager.setAdapter(mAdapter);
    }

    @Override
    protected void onAuthToken() {
        getLoaderManager().initLoader(0, null, this);
    }

//------------------------------------------
//  Methods
//------------------------------------------

    @Override
    public Loader<List<Creature>> onCreateLoader(int i, Bundle bundle) {
        return new CreaturesLoader(this, getClient());
    }

    @Override
    public void onLoadFinished(Loader<List<Creature>> listLoader, List<Creature> creatures) {
        mAdapter.setCreatures(creatures);
        mTitles.requestLayout();
        mProgress.startAnimation(AnimationUtils.loadAnimation(
                this, android.R.anim.fade_out));
        mPager.startAnimation(AnimationUtils.loadAnimation(
                this, android.R.anim.fade_in));
        mProgress.setVisibility(View.GONE);
        mPager.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<List<Creature>> listLoader) {
        mAdapter.setCreatures(null);
    }

//------------------------------------------
//  Inner Classes
//------------------------------------------

    public static final class CreaturesPagerAdapter extends FragmentPagerAdapter {

        private List<Creature> mCreatures;

        public CreaturesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setCreatures(List<Creature> creatures) {
            mCreatures = creatures;
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCreatures != null ? mCreatures.get(position).getTitle() : null;
        }

        @Override
        public Fragment getItem(int i) {
            if (mCreatures == null) {
                return null;
            }

            return CreatureFragment.newInstance(mCreatures.get(i));
        }

        @Override
        public int getCount() {
            return mCreatures != null ? mCreatures.size() : 0;
        }

    }

    private static final class CreaturesLoader extends AsyncTaskLoader<List<Creature>> {

        private List<Creature> mData;

        private final Fusiontables mClient;

        public CreaturesLoader(Context context, Fusiontables client) {
            super(context);
            mClient = client;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override
        public void deliverResult(List<Creature> data) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (data != null) {
                    onReleaseData(mData);
                }
            }

            List<Creature> oldData = mData;
            mData = data;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(data);
            }

            // At this point we can release the resources associated with
            // 'data' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldData != null) {
                onReleaseData(oldData);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading() {
            if (mData != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mData);
            }

            if (takeContentChanged() || mData == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override
        public void onCanceled(List<Creature> data) {
            super.onCanceled(data);

            // At this point we can release the resources associated with 'data'
            // if needed.
            onReleaseData(data);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override
        protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (mData != null) {
                onReleaseData(mData);
                mData = null;
            }
        }

        @Override
        public List<Creature> loadInBackground() {
            try {
                return Creature.all(mClient);
            } catch (IOException e) {
                return null;
            }
        }

        private void onReleaseData(List<Creature> creatures) {

        }

    }

}
