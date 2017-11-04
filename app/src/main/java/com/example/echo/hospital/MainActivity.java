package com.example.echo.hospital;

import com.example.echo.hospital.model.User;
import com.example.echo.hospital.model.Google;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.Auth;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.text.method.PasswordTransformationMethod;
import android.accounts.Account;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity{

    public static GoogleAccountCredential credential;
    private com.google.api.services.sheets.v4.Sheets service = null;

    //google sheet api -----start
    private TextView mOutputText;
    private Button mCallApiButton;
    private EditText account, password;
    private ImageView logo;
    ProgressDialog mProgress;
    // The ID of the spreadsheet to retrieve data from.
    final String spreadsheetId = "1IvyIKcyzlAqlsbMuoB78Y7LYDkqWkc9iMeUwQ4TaVtA";
    // The A1 notation of the values to retrieve.
    final String range = "account!A2:D";

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Login";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS};
    final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    //google sheet api -----end

    //store account and password -----start
    private String AccountValue;
    private String PasswordValue;
    private String NameValue;
    private int IdentityValue;
    //store account and password -----end

    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/drive-java-quickstart");

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        logo = new ImageView(this);
        logo.setImageResource(R.drawable.hospital);
        activityLayout.addView(logo);

        account = new EditText(this);
        account.setLayoutParams(tlp);
        account.setHint("account");
        activityLayout.addView(account);

        password = new EditText(this);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setLayoutParams(tlp);
        password.setHint("password");
        password.setTransformationMethod(new PasswordTransformationMethod());
        activityLayout.addView(password);

        mCallApiButton = new Button(this);
        mCallApiButton.setText(BUTTON_TEXT);
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!account.getText().toString().trim().equals("") && !password.getText().toString().trim().equals("")){

                    AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
                        private com.google.api.services.sheets.v4.Sheets service = new com.google.api.services.sheets.v4.Sheets.Builder(httpTransport, jsonFactory, credential)
                                .setApplicationName("Google Sheets API Android Quickstart").build();

                        @Override
                        protected List<String> doInBackground(Void... params) {
                            List<String> results = new ArrayList<String>();
                            Exception mLastError = null;
                            try {
                                ValueRange response = service.spreadsheets().values()
                                        .get(spreadsheetId, range)
                                        .execute();
                                List<List<Object>> values = response.getValues();
                                if (values != null) {
                                    for (List row : values) {
                                        results.add(row.get(0) + ", " + row.get(1) + ", " + row.get(2) + ", " + row.get(3));
                                    }
                                }
                                return results;
                            }catch (UserRecoverableAuthIOException e) {
                                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                                return null;
                            }
                            catch (Exception e) {
                                mLastError = e;
                                cancel(true);
                                return null;
                            }

                        }

                        @Override
                        protected void onPostExecute(List<String> output) {
                            if (output == null || output.size() == 0) {
                                mOutputText.setText("No results returned.");
                            } else {
                                for(String str: output){
                                    String[] array = str.split(",");
                                    String googleAccount = array[0].trim();
                                    String googlePassword = array[1].trim();
                                    String googleName = array[2].trim();
                                    int googleIdentity = Integer.parseInt(array[3].trim());

                                    if(googleAccount.equals(account.getText().toString().trim()) && googlePassword.equals(password.getText().toString().trim())){

                                        //store input account and password  ---- start
                                        // Edit and commit
                                        AccountValue = googleAccount;
                                        PasswordValue = googlePassword;
                                        NameValue = googleName;
                                        IdentityValue = googleIdentity;

                                        SharedPreferences settings = getSharedPreferences(User.PREFS_NAME,
                                                Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString(User.PREF_ACCOUNT, AccountValue);
                                        editor.putString(User.PREF_PASSWORD, PasswordValue);
                                        editor.putString(User.PREF_NAME, NameValue);
                                        editor.putInt(User.PREF_IDENTITY, IdentityValue);
                                        editor.commit();
                                        //store input account and password  ---- end

                                        Intent intent = new Intent();
                                        intent.setClass(MainActivity.this, MenuActivity.class);
                                        //startActivity(intent);
                                        startActivityForResult(intent, REQUEST_AUTHORIZATION);
                                        break;
                                    }
                                }
                            }
                        }
                    };
                    task.execute();
                }

            }
        });
        activityLayout.addView(mCallApiButton);

        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        mOutputText.setText("");
        activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Sheets API ...");
        setContentView(activityLayout);

        // Initialize credentials and service object.
        // Google Accounts
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

    }
}