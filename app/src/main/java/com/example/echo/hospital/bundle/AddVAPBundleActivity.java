package com.example.echo.hospital.bundle;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.echo.hospital.MainActivity;
import com.example.echo.hospital.MenuActivity;
import com.example.echo.hospital.R;
import com.example.echo.hospital.mdro.MdroActivity;
import com.example.echo.hospital.model.User;
import com.example.echo.hospital.wash.WashActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddVAPBundleActivity extends AppCompatActivity {

    private EditText date, bed, patient, auditor;
    private RadioButton unitOne, unitTwo, evaluateTrue, evaluateFalse, palliativeTrue, palliativeFalse, mouthTrue, mouthFalse, bedHeadTrue, bedHeadFalse,
            waterTrue, waterFalse, doctorSignTrue, doctorSignFalse, nurseSignTrue, nurseSignFalse;
    private Spinner unitSpinner;
    private String dateValue, unitValue, bedValue, patientValue, evaluateValue, palliativeValue, mouthValue, bedHeadValue, waterValue, doctorSignValue, nurseSignValue,
            completeValue, auditorValue;
    private Button saveBtn;
    private int mYear, mMonth, mDay; //西元年
    private int cYear, cMonth; //民國年月
    private String cDay; //民國日
    private AlertDialog.Builder MyAlertDialog;

    //store account and password -----start
    private final String DefaultNameValue = "";
    private String NameValue;
    //store account and password -----end

    //google sheet api -----start
    static final int REQUEST_AUTHORIZATION = 1001;
    // The ID of the spreadsheet to update.
    String spreadsheetId = "1ND5tMIyA_G1GnXwKPpFJGWVGeNUIQjcwGqF6cZokGNE";
    // The A1 notation of a range to search for a logical table of data.
    // Values will be appended after the last row of the table.
    String range;
    ValueRange body = new ValueRange();
    //google sheet api -----end
    final Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vap_bundle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init
        MyAlertDialog = new AlertDialog.Builder(this);
        MyAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        //set datePicker
        date = (EditText)findViewById(R.id.EditText1);

        if(date.getText().length() == 0){
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            date.setText(setDateFormat(mYear, mMonth, mDay));//default today
        }

        date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            new DatePickerDialog(AddVAPBundleActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                String format = setDateFormat(year,month,day);
                mYear = year;
                mMonth = month;
                mDay = day;
                date.setText(format);
                }
            }, mYear,mMonth, mDay).show();
            }

        });

        //set unit
        //unitOne = (RadioButton) findViewById(R.id.RadioButton1);
        //unitTwo = (RadioButton) findViewById(R.id.RadioButton2);
        unitSpinner = (Spinner)findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"ICU-1", "ICU-2", "ICU-3", "ICU-5", "7B", "8B"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        unitSpinner.setAdapter(adapter);

        //set bed number
        bed = (EditText) findViewById(R.id.EditText2);

        //set patient
        patient = (EditText) findViewById(R.id.EditText3);

        //評估適應症 radiobutton
        evaluateTrue = (RadioButton) findViewById(R.id.RadioButton3);
        evaluateFalse = (RadioButton) findViewById(R.id.RadioButton4);

        //暫停鎮靜劑 radiobutton
        palliativeTrue = (RadioButton) findViewById(R.id.RadioButton5);
        palliativeFalse = (RadioButton) findViewById(R.id.RadioButton6);

        //口腔照護 radiobutton
        mouthTrue = (RadioButton) findViewById(R.id.RadioButton7);
        mouthFalse = (RadioButton) findViewById(R.id.RadioButton8);

        //床頭抬高 radiobutton
        bedHeadTrue = (RadioButton) findViewById(R.id.RadioButton9);
        bedHeadFalse = (RadioButton) findViewById(R.id.RadioButton10);

        //排空積水 radiobutton
        waterTrue = (RadioButton) findViewById(R.id.RadioButton11);
        waterFalse = (RadioButton) findViewById(R.id.RadioButton12);

        //set doctor sign radiobutton
        doctorSignTrue = (RadioButton) findViewById(R.id.RadioButton13);
        doctorSignFalse = (RadioButton) findViewById(R.id.RadioButton14);

        //set nurse sign radiobutton
        nurseSignTrue = (RadioButton) findViewById(R.id.RadioButton15);
        nurseSignFalse = (RadioButton) findViewById(R.id.RadioButton16);

        //set auditor name
        //get input account and password  ---- start
        SharedPreferences settings = getSharedPreferences(User.PREFS_NAME,
                Context.MODE_PRIVATE);
        //Get value
        NameValue = settings.getString(User.PREF_NAME, DefaultNameValue);
        //get input account and password  ---- end

        auditor = (EditText) findViewById(R.id.EditText4);
        auditor.setText(NameValue);

        //save button click event
        saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            //get all fill value
            dateValue = date.getText().toString();
            //unitValue = unitOne.isChecked() ? "ICU-1": unitTwo.isChecked() ? "ICU-2" : "";
            unitValue = unitSpinner.getSelectedItem().toString();
            bedValue = bed.getText().toString();
            patientValue = patient.getText().toString();
            evaluateValue = evaluateTrue.isChecked() ? "Y": evaluateFalse.isChecked() ? "N" : "";
            palliativeValue = palliativeTrue.isChecked() ? "Y": palliativeFalse.isChecked() ? "N" : "";
            mouthValue = mouthTrue.isChecked() ? "Y": mouthFalse.isChecked() ? "N" : "";
            bedHeadValue = bedHeadTrue.isChecked() ? "Y": bedHeadFalse.isChecked() ? "N" : "";
            waterValue = waterTrue.isChecked() ? "Y": waterFalse.isChecked() ? "N" : "";
            doctorSignValue = doctorSignTrue.isChecked() ? "Y": doctorSignFalse.isChecked() ? "N" : "";
            nurseSignValue = nurseSignTrue.isChecked() ? "Y": nurseSignFalse.isChecked() ? "N" : "";
            completeValue = doctorSignValue.equals("Y") && nurseSignValue.equals("Y") ? "Y" : "N";
            auditorValue = NameValue.toString();

            //validate
            if(dateValue.length() == 0 || unitValue.length() == 0 || bedValue.length() == 0 || patientValue.length() == 0 || doctorSignValue.length() == 0 ||
                    nurseSignValue.length() == 0  || auditorValue.length() == 0){
                MyAlertDialog.setTitle("Message");
                MyAlertDialog.setMessage("請填寫正確資料");
                MyAlertDialog.show();
            }else{
                //save to excel
                new MakeRequestTask(MainActivity.mCredential).execute();
            }
            }
        });
    }

    //set tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        menu.add(0, 0, 0, "主選單");
        menu.add(0, 1, 1, "手部衛生稽核列表");
        menu.add(0, 2, 2, "MDRO稽核列表");
        menu.add(0, 3, 3, "Bundle CVP稽核列表");
        menu.add(0, 4, 4, "Bundle VAP稽核列表");
        menu.add(0, 5, 5, "Bundle Foley稽核列表");
        menu.add(0, 6, 6, "登出");
        menu.add(0, 7, 7, "離開");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //依據itemId來判斷使用者點選哪一個item
        switch(item.getItemId()) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(AddVAPBundleActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent();
                intent.setClass(AddVAPBundleActivity.this, WashActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent();
                intent.setClass(AddVAPBundleActivity.this, MdroActivity.class);
                startActivity(intent);
                break;
            case 3:
                MenuActivity.bundleName = "CVP";
                intent = new Intent();
                intent.setClass(AddVAPBundleActivity.this, CVPBundleActivity.class);
                startActivity(intent);
                break;
            case 4:
                MenuActivity.bundleName = "VAP";
                intent = new Intent();
                intent.setClass(AddVAPBundleActivity.this, VAPBundleActivity.class);
                startActivity(intent);
                break;
            case 5:
                MenuActivity.bundleName = "Foley";
                intent = new Intent();
                intent.setClass(AddVAPBundleActivity.this, FoleyBundleActivity.class);
                startActivity(intent);
                break;
            case 6:

                break;
            case 7:
                //結束此程式
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
        cYear = year - 1911;
        cMonth = monthOfYear + 1;
        String adjMonthOfYear = String.valueOf(cMonth).length() == 1 ? "0" + String.valueOf(cMonth) : String.valueOf(cMonth);
        cDay = String.valueOf(dayOfMonth).length() == 1 ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
        return cYear + adjMonthOfYear + cDay;
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, String> {
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
        protected String doInBackground(Void... params) {
            List<String> results = new ArrayList<String>();
            Exception mLastError = null;
            String sheetName = cYear+ MenuActivity.bundleName;
            range = sheetName+"!A:O";
            try {
                Spreadsheet sheet_metadata = mService.spreadsheets().get(spreadsheetId).execute();
                List<Sheet> sheetList = sheet_metadata.getSheets();
                boolean matchSheetName = false;
                for(Sheet sheet:sheetList){
                    if(sheetName.equals(sheet.getProperties().getTitle())) {
                        matchSheetName = true;
                    }
                }
                if(!matchSheetName){//沒有此年度的Bundle, 先建立此年度的sheet

                    //Create a new AddSheetRequest
                    AddSheetRequest addSheetRequest = new AddSheetRequest();
                    SheetProperties sheetProperties = new SheetProperties();

                    //Add the sheetName to the sheetProperties
                    addSheetRequest.setProperties(sheetProperties);
                    addSheetRequest.setProperties(sheetProperties.setTitle(sheetName));

                    //Create batchUpdateSpreadsheetRequest
                    BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
                    //Create requestList and set it on the batchUpdateSpreadsheetRequest
                    List<Request> requestsList = new ArrayList<Request>();
                    batchUpdateSpreadsheetRequest.setRequests(requestsList);

                    //Create a new request with containing the addSheetRequest and add it to the requestList
                    Request request = new Request();
                    request.setAddSheet(addSheetRequest);
                    requestsList.add(request);

                    //Add the requestList to the batchUpdateSpreadsheetRequest
                    batchUpdateSpreadsheetRequest.setRequests(requestsList);

                    //Call the sheets API to execute the batchUpdate
                    mService.spreadsheets().batchUpdate(spreadsheetId,batchUpdateSpreadsheetRequest).execute();

                    List<Object> data1 = new ArrayList<Object>();
                    data1.add("年度");//年度
                    data1.add("月份");//月份
                    data1.add("稽核日期");//稽核日期
                    data1.add("單位");//單位
                    data1.add("床號");//床號
                    data1.add("病歷號");//病歷號
                    data1.add("評估適應症");//評估適應症
                    data1.add("暫停鎮靜劑");//暫停鎮靜劑
                    data1.add("檢視敷料日");//口腔照護
                    data1.add("檢視部位");//床頭抬高
                    data1.add("排空積水");//排空積水
                    data1.add("醫師/NP");//醫師/NP
                    data1.add("護理師");//護理師
                    data1.add("總完整");//總完整
                    data1.add("稽核者");//稽核者

                    List<List<Object>> data = new ArrayList<List<Object>>();
                    data.add (data1);

                    List<List<Object>> values = data;

                    body = new ValueRange()
                            .setValues(values);
                    Sheets.Spreadsheets.Values.Append requestAddFirstColumnName =
                            mService.spreadsheets().values().append(spreadsheetId, range, body).setValueInputOption("RAW");

                    AppendValuesResponse response = requestAddFirstColumnName.setInsertDataOption("INSERT_ROWS").execute();
                }

                List<Object> data1 = new ArrayList<Object>();
                data1.add(cYear);//年度
                data1.add(cMonth);//月份
                data1.add(dateValue);//稽核日期
                data1.add(unitValue);//單位 //下拉式選單
                data1.add(bedValue);//床號
                data1.add(patientValue);//病歷號
                data1.add(evaluateValue);//評估適應症
                data1.add(palliativeValue);//暫停鎮靜劑
                data1.add(mouthValue);//口腔照護
                data1.add(bedHeadValue);//床頭抬高
                data1.add(waterValue);//排空積水
                data1.add(doctorSignValue);//醫師/NP
                data1.add(nurseSignValue);//護理師
                data1.add(completeValue);//總完整
                data1.add(auditorValue);//稽核者

                List<List<Object>> data = new ArrayList<List<Object>>();
                data.add (data1);

                List<List<Object>> values = data;

                body = new ValueRange()
                        .setValues(values);

                Sheets.Spreadsheets.Values.Append request =
                        mService.spreadsheets().values().append(spreadsheetId, range, body).setValueInputOption("RAW");

                AppendValuesResponse response = request.setInsertDataOption("INSERT_ROWS").execute();
                return "successful";
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return "failure";
            }
        }

        @Override
        protected void onPostExecute(String output) {
            try {
                if(output.equals("successful")){
                    Intent intent = new Intent();
                    intent.setClass(AddVAPBundleActivity.this, VAPBundleActivity.class);
                    startActivity(intent);
                }else{
                    MyAlertDialog.setTitle("Message");
                    MyAlertDialog.setMessage("新增失敗");
                    MyAlertDialog.show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
