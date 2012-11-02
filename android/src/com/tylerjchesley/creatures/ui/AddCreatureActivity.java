package com.tylerjchesley.creatures.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.google.api.services.fusiontables.Fusiontables;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;

import java.io.IOException;

/**
 * Author: Tyler Chesley
 */
public class AddCreatureActivity extends CreaturesAuthActivity {

//------------------------------------------
//  Constants
//------------------------------------------

    public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_creature);
    }

//------------------------------------------
//  Methods
//------------------------------------------

    /**---- CreaturesAuthActivity ----**/

    @Override
    protected void onAuthToken() {
        final Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) &&
                MIME_TYPE_TEXT_PLAIN.equals(intent.getType())) {
            final String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            final String url = intent.getStringExtra(Intent.EXTRA_TEXT);
            final Creature creature = new Creature(title, url);
            new InsertCreatureTask().execute(creature);
        }
    }

//------------------------------------------
//  Inner Classes
//------------------------------------------

    private class InsertCreatureTask extends AsyncTask<Creature, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Creature... creatures) {
            final Fusiontables client = getClient();
            try {
                for (Creature creature : creatures) {
                    creature.insert(client);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Toast.makeText(AddCreatureActivity.this,
                    success ? R.string.creature_insert_success :
                            R.string.creature_insert_failure, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

}
