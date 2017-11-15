package com.example.echo.hospital.bundle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.echo.hospital.MainActivity;
import com.example.echo.hospital.MenuActivity;
import com.example.echo.hospital.R;
import com.example.echo.hospital.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FoleyBundleActivity extends AppCompatActivity {

    //store account and password -----start
    private final String DefaultAccountValue = "";
    private String AccountValue;

    private final String DefaultPasswordValue = "";
    private String PasswordValue;

    private final String DefaultNameValue = "";
    private String NameValue;

    private final int DefaultIdentityValue = 0;
    private int IdentityValue;
    //store account and password -----end

    User currentUser = new User();
    //view
    private FloatingActionButton addBtn;
    private ListView listView;
    private ArrayAdapter adapter;

    //google sheet api -----start
    static final int REQUEST_AUTHORIZATION = 1001;
    String spreadsheetId = "1ythc41RFh9JmO0hXyZYNghXfXNPn7-NcPbRHosof_sE";
    // The A1 notation of a range to search for a logical table of data.
    // Values will be appended after the last row of the table.
    String range;
    ValueRange body = new ValueRange();
    //google sheet api -----end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foley_bundle);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1);

        //get bundle data
        new MakeRequestTask(MainActivity.mCredential).execute();

        //get input account and password  ---- start
        SharedPreferences settings = getSharedPreferences(User.PREFS_NAME,
                Context.MODE_PRIVATE);

        //Get value
        AccountValue = settings.getString(User.PREF_ACCOUNT, DefaultAccountValue);
        PasswordValue = settings.getString(User.PREF_PASSWORD, DefaultPasswordValue);
        NameValue = settings.getString(User.PREF_NAME, DefaultNameValue);
        IdentityValue = settings.getInt(User.PREF_IDENTITY, DefaultIdentityValue);
        currentUser.setAccount(AccountValue);
        currentUser.setPassword(PasswordValue);
        currentUser.setName(NameValue);
        currentUser.setIdentity(IdentityValue);
        //get input account and password  ---- end

        if (currentUser.getIdentity() == User.NURSES) {//read only
            //Do nothing
        } else {//editable
            addBtn = (FloatingActionButton) findViewById(R.id.fab);
            addBtn.setVisibility(View.VISIBLE);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(FoleyBundleActivity.this, AddFoleyBundleActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> results = new ArrayList<String>();
            Exception mLastError = null;
            try {
                int correntYear = Calendar.getInstance().get(Calendar.YEAR);
                String sheetName = correntYear - 1911 + MenuActivity.bundleName;
                range = sheetName + "!C2:O";

                Spreadsheet sheet_metadata = mService.spreadsheets().get(spreadsheetId).execute();
                List<Sheet> sheetList = sheet_metadata.getSheets();
                boolean matchSheetName = false;
                for (Sheet s : sheetList) {
                    if (sheetName.equals(s.getProperties().getTitle())) {
                        matchSheetName = true;
                        break;
                    }
                }
                if (!matchSheetName) {//沒有此年度的Bundle
                    //Do nothing
                } else {
                    ValueRange response = mService.spreadsheets().values()
                            .get(spreadsheetId, range)
                            .execute();
                    List<List<Object>> values = response.getValues();
                    if (values != null) {
                        for (List row : values) {
                            results.add(row.get(0) + ", " + row.get(1) + ", " + row.get(2) + ", " + row.get(3) + ", " + row.get(4) + ", "
                                    + row.get(5) + ", " + row.get(6) + ", " + row.get(7) + row.get(8) + ", " + row.get(9) + ", " + row.get(10)
                                    + row.get(11) + ", " + row.get(12));
                        }
                    }
                }
                return results;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }


        @Override
        protected void onPostExecute(List<String> output) {
            try {
                if (output.size() == 0) {
                    adapter.add("目前本年度尚未未有" + MenuActivity.bundleName + "稽核表");
                } else {
                    //title
                    adapter.add(MenuActivity.bundleName + " Bundle稽核列表");
                    for (String str : output) {
                        //稽核日期, 單位, 床號, 病歷號, 評估適應症, 手部衛生, 固定位置, 無菌通暢, 尿道口清潔, 醫師/NP, 護理師, 總完整, 稽核者
                        String[] array = str.split(",");
                        String dateValue = array[0].trim();
                        String unitValue = array[1].trim();
                        /*String bedValue = array[2].trim();
                        String patientValue = array[3].trim();
                        String doctorSignValue = array[4].trim();
                        String nurseSignValue = array[5].trim();
                        String completeValue = array[6].trim();
                        String auditorValue = array[7].trim();*/

                        //adapter.add(dateValue + ", 單位：" + unitValue + ", 床位：" + bedValue + ", 病歷號：" + patientValue + ", 總完整：" + completeValue  + ", 稽核者：" + auditorValue);
                        adapter.add(dateValue + ", 單位：" + unitValue);
                    }
                        /*TODO:需要檢視？！
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                                    long arg3) {

                                if(arg3 != 0L){//洗手稽核表
                                    Intent intent = new Intent();
                                    intent.setClass(MenuActivity.this, BundleActivity.class);
                                    startActivity(intent);
                                }

                            }
                        });*/
                }
                listView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
