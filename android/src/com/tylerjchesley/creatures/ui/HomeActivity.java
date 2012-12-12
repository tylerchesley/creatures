package com.tylerjchesley.creatures.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;
import xxx.tylerchesley.android.app.SinglePaneActivity;

public class HomeActivity extends SinglePaneActivity implements
        CreaturesGalleryFragment.OnCreatureSelectedListener, ActionBar.OnNavigationListener {

//------------------------------------------
//  Variables
//------------------------------------------

    private ArrayAdapter<CharSequence> mAdapter;

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = ArrayAdapter.createFromResource(this,
                R.array.creatures_filter_options, R.layout.sherlock_spinner_item);
        mAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(mAdapter, this);
        getSupportActionBar().setSelectedNavigationItem(
                ((CreaturesGalleryFragment) getFragment()).getFilter());
    }

//------------------------------------------
//  Methods
//------------------------------------------

    @Override
    public boolean onNavigationItemSelected(int position, long itemId) {
        ((CreaturesGalleryFragment) getFragment()).setFilter(position);
        return true;
    }

    @Override
    protected Fragment onCreatePane() {
        return new CreaturesGalleryFragment();
    }

    @Override
    public void onCreatureSelected(int position, int filter) {
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Creatures.CONTENT_URI);
        intent.putExtra(Intent.EXTRA_TITLE, mAdapter.getItem(filter));
        intent.putExtra(CreaturesViewPagerFragment.SELECTED_CREATURE_POSITION, position);
        intent.putExtra(CreaturesViewPagerFragment.SELECTED_FILTER, filter);
        startActivity(intent);
    }

}
