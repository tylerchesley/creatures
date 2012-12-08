package com.tylerjchesley.creatures.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;
import xxx.tylerchesley.android.app.SinglePaneActivity;

public class CreaturesActivity extends SinglePaneActivity implements
        CreaturesGalleryFragment.OnCreatureSelectedListener {

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//------------------------------------------
//  Methods
//------------------------------------------

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
