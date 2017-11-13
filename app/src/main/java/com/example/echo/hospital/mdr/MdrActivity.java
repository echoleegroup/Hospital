package com.example.echo.hospital.mdr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.echo.hospital.MainActivity;
import com.example.echo.hospital.R;
import com.example.echo.hospital.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MdrActivity extends AppCompatActivity {

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
    String spreadsheetId = "1HS0viTR_5v7KenH0ExQszyLJ0z2dE3-VYXJJW4WbRQw";
    // The A1 notation of a range to search for a logical table of data.
    // Values will be appended after the last row of the table.
    String range;
    ValueRange body = new ValueRange();
    //google sheet api -----end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdr);

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

        if(currentUser.getIdentity() == User.NURSES){//read only
            //Do nothing
        }else{//editable
            addBtn = (FloatingActionButton) findViewById(R.id.floatingActionButton);
            addBtn.setVisibility(View.VISIBLE);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(MdrActivity.this, AddMdrActivity.class);
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
                String sheetName = String.valueOf(correntYear-1911);
                range = sheetName+"!C2:V";

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
                            results.add(row.get(0) + ", " + row.get(1) + ", " + row.get(2) + ", " + row.get(3) + ", " + row.get(4) + ", " + row.get(5) + ", " + row.get(6) + ", " + row.get(7)
                                    + ", " + row.get(8) + ", " + row.get(9) + ", " + row.get(10) + ", " + row.get(11) + ", " + row.get(12) + ", " + row.get(13) + ", " + row.get(14)
                                    + ", " + row.get(15) + ", " + row.get(16) + ", " + row.get(17) + ", " + row.get(18) + ", " + row.get(19)
                            );
                        }
                    }
                }
                return results;
            }
            catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }


        @Override
        protected void onPostExecute(List<String> output) {
            try {
                if (output.size() == 0) {
                    adapter.add("目前本年度尚未未有MDR稽核表");
                } else {
                    //title
                    adapter.add("MDR稽核列表");
                    for (String str : output) {
                        //稽核日期, 單位, 床號, 病歷號, 1~15, 達成, 稽核者
                        String[] array = str.split(",");
                        String dateValue = array[0].trim();
                        String unitValue = array[1].trim();
                        String bedValue = array[2].trim();
                        /*String firstValue = array[3].trim();
                        String secondValue = array[4].trim();
                        String thirdValue = array[5].trim();*/
                        String completeValue = array[18].trim();
                        String auditorValue = array[19].trim();

                        adapter.add(dateValue + ", 單位：" + unitValue + ", 床位：" + bedValue + ", 達成：" + completeValue + ", 稽核者：" + auditorValue);
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
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
