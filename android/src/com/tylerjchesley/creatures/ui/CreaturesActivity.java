package com.tylerjchesley.creatures.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;
import xxx.tylerchesley.android.app.SinglePaneActivity;

public class CreaturesActivity extends SinglePaneActivity implements
        CreaturesGalleryFragment.OnCreatureSelectedListener, ActionBar.OnNavigationListener {

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.creatures_filter_options, R.layout.sherlock_spinner_item);
        adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(adapter, this);
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
    public void onCreatureSelected(long id, Bundle arguments) {
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Creatures.buildCreatureUri(id));
        intent.putExtras(arguments);
        startActivity(intent);
    }

}
