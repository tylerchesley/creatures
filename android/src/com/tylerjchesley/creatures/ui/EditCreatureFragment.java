package com.tylerjchesley.creatures.ui;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;
import com.tylerjchesley.creatures.util.UiUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import xxx.tylerchesley.android.app.EditRecordFragment;
import xxx.tylerchesley.android.app.GenericProgressDialogFragment;
import xxx.tylerchesley.android.util.ImageFetcher;
import xxx.tylerchesley.android.util.UIUtils;

import java.io.IOException;

/**
 * Author: Tyler Chesley
 */
public class EditCreatureFragment extends EditRecordFragment<Creature> {

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

    private static boolean isValidHttpUrl(CharSequence url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

//------------------------------------------
//  Variables
//------------------------------------------

    private ImageFetcher mImageFetcher;

    /**---- Views ----**/

    private EditText mTitle;

    private EditText mUrl;

    private ImageView mImage;

    private final TextView.OnEditorActionListener mGoActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                onOkSelected();
                return true;
            }
            return false;
        }
    };

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_creature, container, false);
        mTitle = (EditText) view.findViewById(R.id.title);
        mUrl = (EditText) view.findViewById(R.id.url);
        mUrl.setOnEditorActionListener(mGoActionListener);
        mImage = (ImageView) view.findViewById(R.id.image);

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelSelected();
            }
        });

        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOkSelected();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Bundle arguments = getArguments();
        final Creature creature = getRecord();

        final String action = arguments.getString(UIUtils._ACTION);
        if (Intent.ACTION_SEND.equals(action)) {
            final String title = arguments.getString(Intent.EXTRA_SUBJECT);
            final String url = arguments.getString(Intent.EXTRA_TEXT);

            creature.setTitle(title);
            creature.setUrl(url);

            mUrl.setEnabled(false);

            bindView(getRecord());
        }
        else if (Intent.ACTION_INSERT.equals(action)) {
            if (UIUtils.hasHoneycomb()) {
                final ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(
                        Context.CLIPBOARD_SERVICE);
                if (clipboard.hasPrimaryClip() &&
                        clipboard.getPrimaryClipDescription().hasMimeType(
                                ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    final ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    final CharSequence pasteData = item.getText();
                    if (pasteData != null && isValidHttpUrl(pasteData)) {
                        creature.setUrl(pasteData.toString());
                    }
                }
            }
            bindView(getRecord());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageFetcher = UiUtils.getImageFetcher(getActivity());
        mImageFetcher.setImageFadeIn(true);

        setRetainInstance(true);
    }

    @Override
    protected void onRecordSaved() {
        Toast.makeText(getActivity(),
                R.string.creature_save_success, Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

//------------------------------------------
//  Methods
//------------------------------------------

    @Override
    protected void bindRecord(Creature record) {
        record.setTitle(mTitle.getText().toString());
        record.setUrl(mUrl.getText().toString());
    }

    @Override
    protected void bindView(Creature record) {
        mTitle.setText(record.getTitle());
        mUrl.setText(record.getUrl());
        if (!TextUtils.isEmpty(record.getImage())) {
            mImageFetcher.loadImage(record.getImage(), mImage);
        }
        else if (!TextUtils.isEmpty(record.getUrl())) {
            findImage();
        }
    }

    @Override
    protected Creature onCreateRecord(Uri uri) {
        return new Creature();
    }

    @Override
    protected boolean isValid() {
        boolean valid = isTitleValid();
        return isUrlValid() && valid;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), getUri(),
                Creature.CONTENT_PROJECTION, null, null, null);
    }

    private void onCancelSelected() {
        getActivity().finish();
    }

    private void onOkSelected() {
        final boolean isValid = isValid();
        if (isValid && TextUtils.isEmpty(getRecord().getImage())) {
            findImage();
        }
        else {
            startRecordSave();
        }
    }

    private boolean isUrlValid() {
        return isUrlValid(mUrl.getText().toString());
    }

    private boolean isTitleValid() {
        if (TextUtils.isEmpty(mTitle.getText())) {
            mTitle.setError(getString(R.string.error_missing_title));
            return false;
        }

        return true;
    }

    private boolean isUrlValid(String url) {
        if (!isValidHttpUrl(url)) {
            mUrl.setError(getString(R.string.error_invalid_url));
        }
        return true;
    }

    private void findImage() {
        final String url = mUrl.getText().toString();

        if (!isUrlValid(url)) {
            return;
        }

        if (isImage(url)) {
            getRecord().setImage(url);
            bindView(getRecord());
        }
        else {
            new ScrapePageTask().execute(url);
        }
    }

//------------------------------------------
//  Inner Classes
//------------------------------------------

    private final class ScrapePageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            GenericProgressDialogFragment.show(getFragmentManager(), null,
                    getString(R.string.creature_finding_image_progress_message));
        }

        @Override
        protected void onPostExecute(Boolean success) {
            GenericProgressDialogFragment.dismiss(getFragmentManager());

            if (success) {
                bindView(getRecord());
            }
            else {
                Toast.makeText(getActivity(),
                        R.string.creature_insert_failure, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            final String url = urls[0];
            final Creature creature = getRecord();

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
                        if (TextUtils.isEmpty(creature.getTitle())) {
                            creature.setTitle(document.title());
                        }

                        creature.setImage(imageUrl);
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

}
