package com.example.echo.hospital.bundle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.echo.hospital.MainActivity;
import com.example.echo.hospital.MenuActivity;
import com.example.echo.hospital.R;
import com.example.echo.hospital.model.User;
import com.example.echo.hospital.utils.ListViewAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CVPBundleActivity extends AppCompatActivity {

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
    private View headerView;
    private TextView headerTextView;
    private LinearLayout menuLayout;

    //google sheet api -----start
    static final int REQUEST_AUTHORIZATION = 1001;
    String spreadsheetId = "1ythc41RFh9JmO0hXyZYNghXfXNPn7-NcPbRHosof_sE";
    // The A1 notation of a range to search for a logical table of data.
    // Values will be appended after the last row of the table.
    String range;
    ValueRange body = new ValueRange();
    //google sheet api -----end

    private String backgroundColor = "#a1c4fd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cvp_bundle);

        //init view
        listView = (ListView) findViewById(R.id.listView);

        //set header
        headerView = (View)getLayoutInflater().inflate(R.layout.menu_header_view, null);
        listView.addHeaderView(headerView);
        headerTextView = (TextView)findViewById(R.id.menuHeader);
        menuLayout = (LinearLayout)findViewById(R.id.menuLayout);
        menuLayout.setBackgroundColor(Color.parseColor(backgroundColor));

        //get CVP bundle data
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

        if(currentUser.getIdentity() == User.NURSES){//read only
            //Do nothing
        }else{//editable
            addBtn = (FloatingActionButton) findViewById(R.id.fab);
            addBtn.setVisibility(View.VISIBLE);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(CVPBundleActivity.this, AddCVPBundleActivity.class);
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
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> results = new ArrayList<String>();
            Exception mLastError = null;
            try {
                int correntYear = Calendar.getInstance().get(Calendar.YEAR);
                String sheetName = correntYear-1911+ MenuActivity.bundleName;
                range = sheetName+"!B2:D";

                Spreadsheet sheet_metadata = mService.spreadsheets().get(spreadsheetId).execute();
                List<Sheet> sheetList = sheet_metadata.getSheets();
                boolean matchSheetName = false;
                for(Sheet s: sheetList){
                    if(sheetName.equals(s.getProperties().getTitle())){
                        matchSheetName = true;
                        break;
                    }
                }
                if(!matchSheetName){//沒有此年度的Bundle
                    //Do nothing
                }else{
                    ValueRange response = mService.spreadsheets().values()
                            .get(spreadsheetId, range)
                            .execute();
                    List<List<Object>> values = response.getValues();
                    if (values != null) {
                        for (List row : values) {
                            results.add(row.get(0) + ", " + row.get(1) + ", " + row.get(2));
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
                Map<String, Integer> map = new HashMap<String, Integer>();
                String monthValue, unitValue = "";
                ArrayList list = new ArrayList<HashMap<String,String>>();

                if (output.size() == 0) {
                    //header
                    headerTextView.setText("目前本年度尚未未有" + MenuActivity.bundleName + "稽核表");
                } else {
                    //header
                    headerTextView.setText(MenuActivity.bundleName +" Bundle稽核列表");
                    for (String str : output) {
                        //月份, 稽核日期, 單位
                        String[] array = str.split(",");
                        monthValue = array[0].trim();
                        unitValue = array[2].trim();
                        if(!map.containsKey(monthValue+"_"+unitValue))
                            map.put(monthValue+"_"+unitValue, 0);
                        map.put(monthValue+"_"+unitValue, map.get(monthValue+"_"+unitValue)+1);
                    }
                    for(String key: map.keySet()){
                        String[] item = key.split("_");
                        monthValue = item[0].trim();
                        unitValue = item[1].trim();

                        HashMap<String,String> temp = new HashMap<String, String>();
                        temp.put(ListViewAdapter.FIRST_COLUMN, monthValue);
                        temp.put(ListViewAdapter.SECOND_COLUMN, unitValue);
                        temp.put(ListViewAdapter.THIRD_COLUMN, String.valueOf(map.get(key)));
                        list.add(temp);
                    }
                }
                ListViewAdapter adapter = new ListViewAdapter(CVPBundleActivity.this, list);
                listView.setAdapter(adapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
