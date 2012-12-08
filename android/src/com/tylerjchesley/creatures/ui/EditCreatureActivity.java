package com.tylerjchesley.creatures.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;
import xxx.tylerchesley.android.util.ImageFetcher;
import xxx.tylerchesley.android.util.UIUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Author: Tyler Chesley
 */
public class EditCreatureActivity extends CreaturesAuthActivity {

//------------------------------------------
//  Constants
//------------------------------------------

    private static final String MIME_TYPE_TEXT_PLAIN = "text/plain";

//------------------------------------------
//  Static Methods
//------------------------------------------

    private static String getMimeType(String url) {
        String type = null;
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private static boolean isImage(String url) {
        final String mimeType = getMimeType(url);
        return mimeType != null && mimeType.startsWith("image");
    }

//------------------------------------------
//  Variables
//------------------------------------------

    private Creature mCreature = new Creature();

    private ImageFetcher mImageFetcher;

    /**---- Views ----**/

    private EditText mTitle;

    private EditText mUrl;

    private ImageView mImage;

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_creature);

        mTitle = (EditText) findViewById(R.id.title);
        mUrl = (EditText) findViewById(R.id.url);
        mImage = (ImageView) findViewById(R.id.image);

        mImageFetcher = UIUtils.getImageFetcher(this);
        mImageFetcher.setImageFadeIn(true);

        final Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) &&
                MIME_TYPE_TEXT_PLAIN.equals(intent.getType())) {
            final String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            final String url = intent.getStringExtra(Intent.EXTRA_TEXT);

            mCreature.setTitle(title);
            mCreature.setUrl(url);

            if (isImage(url)) {
                mCreature.setImage(url);
                bindView();
            }
            else {
                new ScrapePageTask().execute(url);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_accept:
                onAcceptSelected();
                return true;

            case R.id.menu_cancel:
                onCancelSelected();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_edit_creature, menu);
        return super.onCreateOptionsMenu(menu);
    }

//------------------------------------------
//  Methods
//------------------------------------------

    private void onCancelSelected() {
        finish();
    }

    private void onAcceptSelected() {
        if (isValid()) {
            final CreatureQueryHandler handler = new CreatureQueryHandler(getContentResolver());
            handler.startInsert(0, null, Creatures.CONTENT_URI, mCreature.toValues());
        }
    }

    private boolean isValid() {
        return true;
    }

    private void onCreatureSaved() {
        Toast.makeText(EditCreatureActivity.this,
                R.string.creature_save_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**---- CreaturesAuthActivity ----**/

    @Override
    protected void onAuthToken() {

    }

//------------------------------------------
//  Methods
//------------------------------------------

    private void bindView() {
        mTitle.setText(mCreature.getTitle());
        mUrl.setText(mCreature.getUrl());
        mImageFetcher.loadImage(mCreature.getImage(), mImage);
    }

//------------------------------------------
//  Inner Classes
//------------------------------------------

    private final class ScrapePageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            CreatureProgressDialog.show(getSupportFragmentManager());
        }

        @Override
        protected void onPostExecute(Boolean success) {
            CreatureProgressDialog.dismiss(getSupportFragmentManager());

            if (success) {
                bindView();
            }
            else {
                Toast.makeText(EditCreatureActivity.this,
                        R.string.creature_insert_failure, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            final String url = urls[0];

            if (TextUtils.isEmpty(url)) {
                return false;
            }

            try {
                final Connection.Response response = Jsoup.connect(url).timeout(7000).execute();
                final Document document = response.parse();
                final Element element = document.select("link[rel=image_src]").first();
                if (element != null) {
                    final String imageUrl = element.attr("href");
                    if (isImage(imageUrl)) {
                        mCreature.setImage(imageUrl);
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return false;
        }

    }

    private static class CreatureProgressDialog extends DialogFragment {

        private static final String TAG = "CreatureProgressDialog";

        public static void show(FragmentManager fm) {
            final FragmentTransaction ft = fm.beginTransaction();
            Fragment prev = fm.findFragmentByTag(TAG);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            new CreatureProgressDialog().show(ft, TAG);
        }

        public static void dismiss(FragmentManager fm) {
            final Fragment fragment = fm.findFragmentByTag(TAG);
            if (fragment != null) {
                fm.beginTransaction().remove(fragment).commit();
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return ProgressDialog.show(getActivity(), null,
                    getString(R.string.creature_insert_progress_message), true, false);
        }

    }

    final class CreatureQueryHandler extends AsyncQueryHandler {

        public CreatureQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            onCreatureSaved();
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            onCreatureSaved();
        }

    }

}
