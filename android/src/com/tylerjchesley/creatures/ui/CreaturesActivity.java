package com.tylerjchesley.creatures.ui;

import android.os.Bundle;
import com.tylerjchesley.creatures.R;

public class CreaturesActivity extends CreaturesAuthActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatures);
    }

    @Override
    protected void onAuthToken() {
    }

}
