package com.example.echo.hospital.bundle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.echo.hospital.MainActivity;
import com.example.echo.hospital.MenuActivity;
import com.example.echo.hospital.R;
import com.example.echo.hospital.mdro.AddMdroActivity;
import com.example.echo.hospital.mdro.MdroActivity;
import com.example.echo.hospital.model.User;
import com.example.echo.hospital.utils.ListViewAdapter;
import com.example.echo.hospital.wash.WashActivity;
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
import java.util.SortedSet;
import java.util.TreeSet;

public class VAPBundleActivity extends AppCompatActivity {

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
    /*private View headerView;
    private TextView headerTextView;
    private LinearLayout menuLayout;*/
    private ProgressDialog mProgress;

    //google sheet api -----start
    static final int REQUEST_AUTHORIZATION = 1001;
    String spreadsheetId = "1ND5tMIyA_G1GnXwKPpFJGWVGeNUIQjcwGqF6cZokGNE";
    // The A1 notation of a range to search for a logical table of data.
    // Values will be appended after the last row of the table.
    String range;
    ValueRange body = new ValueRange();
    //google sheet api -----end

    private String backgroundColor = "#a1c4fd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vap_bundle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init view
        listView = (ListView) findViewById(R.id.listView);

        //set loading
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Sheets API ...");

        //get VAP bundle data
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
                intent.setClass(VAPBundleActivity.this, AddVAPBundleActivity.class);
                startActivity(intent);
                }
            });
        }
    }

    //set tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        MenuItem item = menu.add(0, 0, 0, "home"); //your desired title here
        item.setIcon(R.drawable.ic_action_name); //your desired icon here
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 1, 1, "手部衛生稽核列表");
        menu.add(0, 2, 2, "MDRO稽核列表");
        menu.add(0, 3, 3, "Bundle CVP稽核列表");
        menu.add(0, 4, 4, "Bundle VAP稽核列表");
        menu.add(0, 5, 5, "Bundle Foley稽核列表");
        menu.add(0, 6, 6, "登出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //依據itemId來判斷使用者點選哪一個item
        switch(item.getItemId()) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(VAPBundleActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent();
                intent.setClass(VAPBundleActivity.this, WashActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent();
                intent.setClass(VAPBundleActivity.this, MdroActivity.class);
                startActivity(intent);
                break;
            case 3:
                MenuActivity.bundleName = "CVP";
                intent = new Intent();
                intent.setClass(VAPBundleActivity.this, CVPBundleActivity.class);
                startActivity(intent);
                break;
            case 4:
                MenuActivity.bundleName = "VAP";
                intent = new Intent();
                intent.setClass(VAPBundleActivity.this, VAPBundleActivity.class);
                startActivity(intent);
                break;
            case 5:
                MenuActivity.bundleName = "Foley";
                intent = new Intent();
                intent.setClass(VAPBundleActivity.this, FoleyBundleActivity.class);
                startActivity(intent);
                break;
            case 6:
                SharedPreferences preferences = getSharedPreferences(User.PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                intent = new Intent();
                intent.setClass(VAPBundleActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
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
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            try {
                Map<String, Integer> map = new HashMap<String, Integer>();
                String monthValue, unitValue = "";
                ArrayList list = new ArrayList<HashMap<String,String>>();
                HashMap<String,String> temp = new HashMap<String, String>();
                if (output.size() == 0) {
                    //header
                    ArrayAdapter adapter = new ArrayAdapter(VAPBundleActivity.this, R.layout.menu_adapter);
                    adapter.add("目前本年度尚未未有" + MenuActivity.bundleName + "稽核表");
                    listView.setAdapter(adapter);
                } else {
                    for (String str : output) {
                        //月份, 稽核日期, 單位
                        String[] array = str.split(",");
                        monthValue = array[0].trim();
                        unitValue = array[2].trim();
                        if(!map.containsKey(monthValue+"_"+unitValue))
                            map.put(monthValue+"_"+unitValue, 0);
                        map.put(monthValue+"_"+unitValue, map.get(monthValue+"_"+unitValue) + 1);
                    }
                    //新增表頭
                    temp.put(ListViewAdapter.FIRST_COLUMN, "月份");
                    temp.put(ListViewAdapter.SECOND_COLUMN, "單位");
                    temp.put(ListViewAdapter.THIRD_COLUMN, "件數");
                    list.add(temp);
                    //order
                    SortedSet<String> keys = new TreeSet<String>(map.keySet()).descendingSet();
                    for(String key: keys){
                        String[] item = key.split("_");
                        monthValue = item[0];
                        unitValue = item[1];

                        temp = new HashMap<String, String>();
                        temp.put(ListViewAdapter.FIRST_COLUMN, monthValue);
                        temp.put(ListViewAdapter.SECOND_COLUMN, unitValue);
                        temp.put(ListViewAdapter.THIRD_COLUMN, String.valueOf(map.get(key)));
                        list.add(temp);
                    }
                    ListViewAdapter adapter = new ListViewAdapter(VAPBundleActivity.this, list);
                    listView.setAdapter(adapter);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
