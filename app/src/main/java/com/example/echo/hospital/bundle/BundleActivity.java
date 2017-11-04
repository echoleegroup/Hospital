package com.example.echo.hospital.bundle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.echo.hospital.MainActivity;
import com.example.echo.hospital.MenuActivity;
import com.example.echo.hospital.R;
import com.example.echo.hospital.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BundleActivity extends AppCompatActivity {

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

    // The ID of the spreadsheet to update.
    //private GoogleAccountCredential credential;
    final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    String spreadsheetId = "1ythc41RFh9JmO0hXyZYNghXfXNPn7-NcPbRHosof_sE";
    // The A1 notation of a range to search for a logical table of data.
    // Values will be appended after the last row of the table.
    String range;
    ValueRange body = new ValueRange();
    //google sheet api -----end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundle);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1);

        //get bundle data
        AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
            private com.google.api.services.sheets.v4.Sheets service = new com.google.api.services.sheets.v4.Sheets.Builder(httpTransport, jsonFactory, MainActivity.credential)
                    .setApplicationName("Google Sheets API Android Quickstart").build();

            @Override
            protected List<String> doInBackground(Void... params) {
                List<String> results = new ArrayList<String>();
                Exception mLastError = null;
                try {
                    int correntYear = Calendar.getInstance().get(Calendar.YEAR);
                    String sheetName = correntYear-1911+MenuActivity.bundleName;
                    range = sheetName+"!C2:J";

                    Spreadsheet sheet_metadata = service.spreadsheets().get(spreadsheetId).execute();
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
                        ValueRange response = service.spreadsheets().values()
                                .get(spreadsheetId, range)
                                .execute();
                        List<List<Object>> values = response.getValues();
                        if (values != null) {
                            for (List row : values) {
                                results.add(row.get(0) + ", " + row.get(1) + ", " + row.get(2) + ", " + row.get(3) + ", " + row.get(4) + ", " + row.get(5) + ", " + row.get(6) + ", " + row.get(7));
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
                        adapter.add("目前本年度尚未未有" + MenuActivity.bundleName + "稽核表");
                    } else {
                        //title
                        adapter.add(MenuActivity.bundleName +" Bundle稽核列表");
                        for (String str : output) {
                            //稽核日期, 單位, 床號, 病歷號, 醫師/NP, 護理師, 總完整, 稽核者
                            String[] array = str.split(",");
                            String dateValue = array[0].trim();
                            String unitValue = array[1].trim();
                            String bedValue = array[2].trim();
                            String patientValue = array[3].trim();
                            String doctorSignValue = array[4].trim();
                            String nurseSignValue = array[5].trim();
                            //TODO 項次 commentValue 下拉式選單
                            String completeValue = array[6].trim();
                            String auditorValue = array[7].trim();

                            adapter.add(dateValue + " 單位：" + unitValue + " 床位：" + bedValue + " 病歷號：" + patientValue);
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
        };
        task.execute();


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
                    intent.setClass(BundleActivity.this, AddBundleActivity.class);
                    startActivity(intent);
                }
            });

        }

    }

}
