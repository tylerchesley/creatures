package com.tylerjchesley.creatures.ui;

import android.accounts.*;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;
import com.google.api.client.googleapis.services.GoogleKeyInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.fusiontables.Fusiontables;

import java.io.IOException;

/**
 * Author: Tyler Chesley
 */
public abstract class CreaturesAuthActivity extends Activity {

//------------------------------------------
//  Constants
//------------------------------------------

    private static final String TAG = "CreaturesAuthActivity";

    private static final String AUTH_TOKEN_TYPE = "fusiontables";

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static final String PREF_AUTH_TOKEN = "authToken";

    private static final int REQUEST_AUTHENTICATE = 0;

//------------------------------------------
//  Variables
//------------------------------------------

    private String mAuthToken;

    private String mAccountName;

    private SharedPreferences mSettings;

    private Fusiontables mClient;

    private GoogleAccountManager mAccountManager;

//------------------------------------------
//  Overridden Methods
//------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HttpRequestInitializer requestInitializer = new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.getHeaders().setAuthorization(GoogleHeaders.getGoogleLoginValue(mAuthToken));
            }
        };
        mClient = new Fusiontables.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
                .setApplicationName("Creatures/1.0")
                .setJsonHttpRequestInitializer(new GoogleKeyInitializer(ClientCredentials.KEY))
                .build();
        mSettings = getPreferences(MODE_PRIVATE);
        mAccountName = mSettings.getString(PREF_ACCOUNT_NAME, null);
        mAuthToken = mSettings.getString(PREF_AUTH_TOKEN, null);
        mAccountManager = new GoogleAccountManager(this);
        gotAccount();
    }

//------------------------------------------
//  Methods
//------------------------------------------

    protected Fusiontables getClient() {
        return mClient;
    }

    private void gotAccount() {
        Account account = mAccountManager.getAccountByName(mAccountName);
        if (account == null) {
            chooseAccount();
            return;
        }
        if (mAuthToken != null) {
            onAuthToken();
            return;
        }
        mAccountManager.getAccountManager()
                .getAuthToken(account, AUTH_TOKEN_TYPE, true, new AccountManagerCallback<Bundle>() {

                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();
                            if (bundle.containsKey(AccountManager.KEY_INTENT)) {
                                Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
                                intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent, REQUEST_AUTHENTICATE);
                            } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                                setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
                                onAuthToken();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }, null);
    }

    private void chooseAccount() {
        mAccountManager.getAccountManager().getAuthTokenByFeatures(GoogleAccountManager.ACCOUNT_TYPE,
                AUTH_TOKEN_TYPE,
                null,
                CreaturesAuthActivity.this,
                null,
                null,
                new AccountManagerCallback<Bundle>() {

                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bundle;
                        try {
                            bundle = future.getResult();
                            setAccountName(bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
                            setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
                            onAuthToken();
                        } catch (OperationCanceledException e) {
                            // user canceled
                        } catch (AuthenticatorException e) {
                            Log.e(TAG, e.getMessage(), e);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                },
                null);
    }

    private void setAccountName(String accountName) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
        mAccountName = accountName;
    }

    private void setAuthToken(String authToken) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PREF_AUTH_TOKEN, authToken);
        editor.commit();
        mAuthToken = authToken;
    }

//------------------------------------------
//  Abstract Methods
//------------------------------------------

    protected abstract void onAuthToken();

}
