package com.tylerjchesley.creatures.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;
import com.tylerjchesley.creatures.util.CreaturesHelper;
import xxx.tylerchesley.android.util.ImageFetcher;

/**
 * Author: Tyler Chesley
 */
public class CreatureFragment extends SherlockFragment {

//------------------------------------------
//  Static Methods
//------------------------------------------

    public static CreatureFragment newInstance(Cursor cursor) {
        final CreatureFragment fragment = new CreatureFragment();
        fragment.setArguments(Creature.buildArgumentsFromCursor(cursor));
        return fragment;
    }

//------------------------------------------
//  Variables
//------------------------------------------

    private Creature mCreature;

    private ImageFetcher mImageFetcher;

    private ImageView mImage;

    private MenuItem mFavoriteMenuItem;

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageFetcher = CreaturesViewPagerFragment.getImageFetcher(getActivity());

        mCreature = Creature.restoreCreature(getArguments());

        CreaturesHelper.setCreatureIsNew(getActivity(), mCreature.getId(), false);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mImageFetcher.loadImage(mCreature.getImage(), mImage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_creature, container, false);
        mImage = (ImageView) view.findViewById(R.id.image);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mImageFetcher != null) {
            mImageFetcher.closeCache();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_creature, menu);
        mFavoriteMenuItem = menu.findItem(R.id.menu_favorite);
        showFavorited(mCreature.isFavorite());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                onFavoriteSelected();
                return true;

            case R.id.menu_share:
                onShareSelected();
                return true;

            case R.id.menu_website:
                onWebsiteSelected();
                return true;

            case R.id.menu_edit:
                onEditSelected();
                return true;

            case R.id.menu_delete:
                onDeleteSelected();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//------------------------------------------
//  Methods
//------------------------------------------

    private void showFavorited(boolean favorited) {
        mFavoriteMenuItem.setTitle(favorited
                ? R.string.description_unfavorite
                : R.string.description_favorite);
        mFavoriteMenuItem.setIcon(favorited
                ? R.drawable.ic_action_unfavorite
                : R.drawable.ic_action_favorit);
        mCreature.setIsFavorite(favorited);
    }

    private void onShareSelected() {
        CreaturesHelper.shareCreature(getActivity(), mCreature.getTitle(), mCreature.getUrl());
    }

    private void onFavoriteSelected() {
        final boolean favorited = !mCreature.isFavorite();
        showFavorited(favorited);
        Toast.makeText(getActivity(), favorited ?
                R.string.toast_favorited : R.string.toast_unfavorited,
                Toast.LENGTH_SHORT).show();
        CreaturesHelper.setCreatureFavorited(getActivity(), mCreature.getId(), favorited);
    }

    private void onWebsiteSelected() {
        final String url = getArguments().getString(Creature.URL);
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.error_unable_open_url,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void onEditSelected() {

    }

    private void onDeleteSelected() {
        CreaturesHelper.maybeDeleteCreature(getActivity(), mCreature.getId());
    }

}
