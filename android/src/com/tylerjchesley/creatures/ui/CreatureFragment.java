package com.tylerjchesley.creatures.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;

/**
 * Author: Tyler Chesley
 */
public class CreatureFragment extends Fragment {

//------------------------------------------
//  Static Methods
//------------------------------------------

    public static CreatureFragment newInstance(Bundle creature) {
        final CreatureFragment fragment = new CreatureFragment();
        fragment.setArguments(creature);
        return fragment;
    }

//------------------------------------------
//  Variables
//------------------------------------------

    private ProgressBar mProgress;

    private String mUrl;

    private String mTitle;

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = getArguments().getString(Creature.URL);
        mTitle = getArguments().getString(Creature.TITLE);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_creature, container, false);
        final WebView webView = (WebView) view.findViewById(R.id.web_view);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        webView.setWebChromeClient(new CreatureChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.loadUrl(mUrl);
        return view;
    }

//------------------------------------------
//  Inner Classes
//------------------------------------------

    final class CreatureChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgress.setProgress(newProgress);
            mProgress.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
        }

    }

}
