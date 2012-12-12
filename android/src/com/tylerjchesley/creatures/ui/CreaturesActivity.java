package com.tylerjchesley.creatures.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import xxx.tylerchesley.android.app.SinglePaneActivity;

/**
 * Author: Tyler Chesley
 */
public class CreaturesActivity extends SinglePaneActivity {

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//------------------------------------------
//  Methods
//------------------------------------------

    @Override
    protected Fragment onCreatePane() {
        return new CreaturesViewPagerFragment();
    }

}
