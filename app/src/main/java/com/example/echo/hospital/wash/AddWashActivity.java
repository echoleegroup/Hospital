package com.example.echo.hospital.wash;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TableRow;

import com.example.echo.hospital.MainActivity;
import com.example.echo.hospital.MenuActivity;
import com.example.echo.hospital.R;
import com.example.echo.hospital.bundle.CVPBundleActivity;
import com.example.echo.hospital.bundle.FoleyBundleActivity;
import com.example.echo.hospital.bundle.VAPBundleActivity;
import com.example.echo.hospital.mdro.AddMdroActivity;
import com.example.echo.hospital.mdro.MdroActivity;
import com.example.echo.hospital.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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

public class AddWashActivity extends AppCompatActivity {
    private EditText date, unit, title, auditor;
    private RadioButton handWashTrue, handWashFalse, equipmentTrue, equipmentFalse, tissueTrue, tissueFalse, washTypeW, washTypeA, contactPatientTrue, contactPatientFalse,
        executeTrue, executeFalse, bodyFluidTrue, bodyFluidFalse, contactPatientAfterTrue, contactPatientAfterFalse, surroundingTrue, surroundingFalse,
        openFaucetTrue, openFaucetFalse, useWashHandTrue, useWashHandFalse, soupHandKeepDownTrue, soupHandKeepDownFalse, heartToHeartTrue, heartToHeartFalse,
        heartToBackTrue, heartToBackFalse, sewToSewTrue, sewToSewFalse, backToHeartTrue, backToHeartFalse, handToThumbTrue, handToThumbFalse, sharpToHeartTrue,
        sharpToHeartFalse, fifteensecTrue, fifteensecFalse, washHandTrue, washHandFalse, wipeTrue, wipeFalse, closeFaucetTrue, closeFaucetFalse, completeTrue, completeFalse;
    private String dateValue, unitValue, titleValue, handWashValue, equipmentValue, tissueValue, washTypeValue, contactPatientValue, executeValue, bodyFluidValue,
        contactPatientAfterValue, surroundingValue, openFaucetValue, useWashHandValue, soupHandKeepDownValue, heartToHeartValue, heartToBackValue, sewToSewValue, backToHeartValue,
        handToThumbValue, sharpToHeartValue, fifteensecValue, washHandValue, wipeValue, closeFaucetValue, completeValue, correctValue, complianceRateValue, auditorValue;
    private TableRow washFirst, washThird, washFifth, washSixth, washSeventh;
    private Button saveBtn;
    private int mYear, mMonth, mDay; //西元年
    private int cYear, cMonth; //民國年月
    private String cDay; //民國日
    private AlertDialog.Builder MyAlertDialog;
    private AsyncTask<Void, Void, String> task;

    //store account and password -----start
    private final String DefaultNameValue = "";
    private String NameValue;
    //store account and password -----end

    //google sheet api -----start
    static final int REQUEST_AUTHORIZATION = 1001;
    // The ID of the spreadsheet to update.
    String spreadsheetId = "1MniPeatGluYz89kmMzbndpPXjIpU7vJ7JRVgq6Bvzsk";
    // The A1 notation of a range to search for a logical table of data.
    // Values will be appended after the last row of the table.
    String range;
    ValueRange body = new ValueRange();
    //google sheet api -----end
    final Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init
        MyAlertDialog = new AlertDialog.Builder(this);
        MyAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        //set rows
        washFirst = (TableRow) findViewById(R.id.tableRow14);
        washThird = (TableRow) findViewById(R.id.tableRow16);
        washFifth = (TableRow) findViewById(R.id.tableRow26);
        washSixth = (TableRow) findViewById(R.id.tableRow27);
        washSeventh = (TableRow) findViewById(R.id.tableRow28);

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
            new DatePickerDialog(AddWashActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        unit = (EditText) findViewById(R.id.EditText2);

        //set title
        title = (EditText) findViewById(R.id.EditText3);

        //set hand wash radiobutton
        handWashTrue = (RadioButton) findViewById(R.id.RadioButton1);
        handWashFalse = (RadioButton) findViewById(R.id.RadioButton2);

        //set equipment radiobutton
        equipmentTrue = (RadioButton) findViewById(R.id.RadioButton3);
        equipmentFalse = (RadioButton) findViewById(R.id.RadioButton4);

        //set tissue radiobutton
        tissueTrue = (RadioButton) findViewById(R.id.RadioButton5);
        tissueFalse = (RadioButton) findViewById(R.id.RadioButton6);

        //set choose hand wash type radiobutton
        washTypeW = (RadioButton) findViewById(R.id.RadioButton7);
        washTypeW.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                changeWashType(true);
            }
        });
        washTypeA = (RadioButton) findViewById(R.id.RadioButton8);
        washTypeA.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                changeWashType(false);
            }
        });
        //wash time
        //set contact patient before radiobutton
        contactPatientTrue = (RadioButton) findViewById(R.id.RadioButton9);
        contactPatientFalse = (RadioButton) findViewById(R.id.RadioButton10);

        //set execute radiobutton
        executeTrue = (RadioButton) findViewById(R.id.RadioButton11);
        executeFalse = (RadioButton) findViewById(R.id.RadioButton12);

        //set body fluid radiobutton
        bodyFluidTrue = (RadioButton) findViewById(R.id.RadioButton13);
        bodyFluidFalse = (RadioButton) findViewById(R.id.RadioButton14);

        //set contact patient after radiobutton
        contactPatientAfterTrue = (RadioButton) findViewById(R.id.RadioButton15);
        contactPatientAfterFalse = (RadioButton) findViewById(R.id.RadioButton16);

        //set surrounding radiobutton
        surroundingTrue = (RadioButton) findViewById(R.id.RadioButton17);
        surroundingFalse = (RadioButton) findViewById(R.id.RadioButton18);

        //set 1 open faucet radiobutton
        openFaucetTrue = (RadioButton) findViewById(R.id.RadioButton19);
        openFaucetFalse = (RadioButton) findViewById(R.id.RadioButton20);

        //set 2 use wash hand radiobutton
        useWashHandTrue = (RadioButton) findViewById(R.id.RadioButton21);
        useWashHandFalse = (RadioButton) findViewById(R.id.RadioButton22);

        //set 3 soup and hand keep down radiobutton
        soupHandKeepDownTrue = (RadioButton) findViewById(R.id.RadioButton23);
        soupHandKeepDownFalse = (RadioButton) findViewById(R.id.RadioButton24);

        //set 4a heart to heart radiobutton
        heartToHeartTrue = (RadioButton) findViewById(R.id.RadioButton25);
        heartToHeartFalse = (RadioButton) findViewById(R.id.RadioButton26);

        //set 4b heart to back radiobutton
        heartToBackTrue = (RadioButton) findViewById(R.id.RadioButton27);
        heartToBackFalse = (RadioButton) findViewById(R.id.RadioButton28);

        //set 4c sew to sew radiobutton
        sewToSewTrue = (RadioButton) findViewById(R.id.RadioButton29);
        sewToSewFalse = (RadioButton) findViewById(R.id.RadioButton30);

        //set 4d back to heart radiobutton
        backToHeartTrue = (RadioButton) findViewById(R.id.RadioButton31);
        backToHeartFalse = (RadioButton) findViewById(R.id.RadioButton32);

        //set 4e hand to thumb radiobutton
        handToThumbTrue = (RadioButton) findViewById(R.id.RadioButton33);
        handToThumbFalse = (RadioButton) findViewById(R.id.RadioButton34);

        //set 4f sharp to heart radiobutton
        sharpToHeartTrue = (RadioButton) findViewById(R.id.RadioButton35);
        sharpToHeartFalse = (RadioButton) findViewById(R.id.RadioButton36);

        //set 4g fifteensec radiobutton
        fifteensecTrue = (RadioButton) findViewById(R.id.RadioButton37);
        fifteensecFalse = (RadioButton) findViewById(R.id.RadioButton38);

        //set 5 wash hand radiobutton
        washHandTrue = (RadioButton) findViewById(R.id.RadioButton39);
        washHandFalse = (RadioButton) findViewById(R.id.RadioButton40);

        //set 6 wipe radiobutton
        wipeTrue = (RadioButton) findViewById(R.id.RadioButton41);
        wipeFalse = (RadioButton) findViewById(R.id.RadioButton42);

        //set 7 close faucet radiobutton
        closeFaucetTrue = (RadioButton) findViewById(R.id.RadioButton43);
        closeFaucetFalse = (RadioButton) findViewById(R.id.RadioButton44);

        //set 8 complete radiobutton
        completeTrue = (RadioButton) findViewById(R.id.RadioButton45);
        completeFalse = (RadioButton) findViewById(R.id.RadioButton46);

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
            unitValue = unit.getText().toString();
            titleValue = title.getText().toString();
            handWashValue = handWashTrue.isChecked() ? handWashTrue.getText().toString() :  handWashFalse.isChecked() ? handWashFalse.getText().toString() : "";
            equipmentValue = equipmentTrue.isChecked() ? equipmentTrue.getText().toString() : equipmentFalse.isChecked() ? equipmentFalse.getText().toString() : "";
            tissueValue = tissueTrue.isChecked() ? tissueTrue.getText().toString() : tissueFalse.isChecked() ? tissueFalse.getText().toString() : "";
            washTypeValue = washTypeW.isChecked() ? washTypeW.getText().toString() : washTypeA.isChecked() ? washTypeA.getText().toString() : "";
            contactPatientValue = contactPatientTrue.isChecked() ? contactPatientTrue.getText().toString(): contactPatientFalse.isChecked() ? contactPatientFalse.getText().toString() : "";
            executeValue = executeTrue.isChecked() ? executeTrue.getText().toString() :  executeFalse.isChecked() ? executeFalse.getText().toString() : "";
            bodyFluidValue = bodyFluidTrue.isChecked() ? bodyFluidTrue.getText().toString() : bodyFluidFalse.isChecked() ? bodyFluidFalse.getText().toString() : "";
            contactPatientAfterValue = contactPatientAfterTrue.isChecked() ? contactPatientAfterTrue.getText().toString() : contactPatientAfterFalse.isChecked() ? contactPatientAfterFalse.getText().toString() : "";
            surroundingValue = surroundingTrue.isChecked() ? surroundingTrue.getText().toString(): surroundingFalse.isChecked() ? surroundingFalse.getText().toString() : "";
            openFaucetValue = washTypeA.isChecked() ? "" : openFaucetTrue.isChecked() ? openFaucetTrue.getText().toString(): openFaucetFalse.isChecked() ? openFaucetFalse.getText().toString() : "";
            useWashHandValue =  useWashHandTrue.isChecked() ? useWashHandTrue.getText().toString(): useWashHandFalse.isChecked() ? useWashHandFalse.getText().toString() : "";
            soupHandKeepDownValue = washTypeA.isChecked() ? "" : soupHandKeepDownTrue.isChecked() ? soupHandKeepDownTrue.getText().toString() :  soupHandKeepDownFalse.isChecked() ? soupHandKeepDownFalse.getText().toString() : "";
            heartToHeartValue = heartToHeartTrue.isChecked() ? heartToHeartTrue.getText().toString() : heartToHeartFalse.isChecked() ? heartToHeartFalse.getText().toString() : "";
            heartToBackValue = heartToBackTrue.isChecked() ? heartToBackTrue.getText().toString() : heartToBackFalse.isChecked() ? heartToBackFalse.getText().toString() : "";
            sewToSewValue = sewToSewTrue.isChecked() ? sewToSewTrue.getText().toString(): sewToSewFalse.isChecked() ? sewToSewFalse.getText().toString() : "";
            backToHeartValue = backToHeartTrue.isChecked() ? backToHeartTrue.getText().toString(): backToHeartFalse.isChecked() ? backToHeartFalse.getText().toString() : "";
            handToThumbValue = handToThumbTrue.isChecked() ? handToThumbTrue.getText().toString() :  handToThumbFalse.isChecked() ? handToThumbFalse.getText().toString() : "";
            sharpToHeartValue = sharpToHeartTrue.isChecked() ? sharpToHeartTrue.getText().toString() : sharpToHeartFalse.isChecked() ? sharpToHeartFalse.getText().toString() : "";
            fifteensecValue = fifteensecTrue.isChecked() ? fifteensecTrue.getText().toString() : fifteensecFalse.isChecked() ? fifteensecFalse.getText().toString() : "";
            washHandValue = washTypeA.isChecked() ? "" : washHandTrue.isChecked() ? washHandTrue.getText().toString(): washHandFalse.isChecked() ? washHandFalse.getText().toString() : "";
            wipeValue = washTypeA.isChecked() ? "" : wipeTrue.isChecked() ? wipeTrue.getText().toString(): wipeFalse.isChecked() ? wipeFalse.getText().toString() : "";
            closeFaucetValue = washTypeA.isChecked() ? "" : closeFaucetTrue.isChecked() ? closeFaucetTrue.getText().toString() :  closeFaucetFalse.isChecked() ? closeFaucetFalse.getText().toString() : "";
            completeValue = completeTrue.isChecked() ? completeTrue.getText().toString() : completeFalse.isChecked() ? completeFalse.getText().toString() : "";
            //correct
            if(washTypeValue.equals("W")){
                correctValue = (openFaucetValue.equals("Y") && useWashHandValue.equals("Y") && soupHandKeepDownValue.equals("Y") && heartToHeartValue.equals("Y") && heartToBackValue.equals("Y")
                        && sewToSewValue.equals("Y") && backToHeartValue.equals("Y") && handToThumbValue.equals("Y") && sharpToHeartValue.equals("Y")
                        && fifteensecValue.equals("Y") && washHandValue.equals("Y") && wipeValue.equals("Y") && closeFaucetValue.equals("Y") && completeValue.equals("Y")
                        ) ? "Y" : "N";

            }else{
                correctValue = (useWashHandValue.equals("Y") && heartToHeartValue.equals("Y") && heartToBackValue.equals("Y") && sewToSewValue.equals("Y") && backToHeartValue.equals("Y")
                        && handToThumbValue.equals("Y") && sharpToHeartValue.equals("Y") && fifteensecValue.equals("Y") && completeValue.equals("Y")
                         ) ? "Y" : "N";
            }
            //compliance
            complianceRateValue = (contactPatientValue.equals("Y") && executeValue.equals("Y") && bodyFluidValue.equals("Y") && contactPatientAfterValue.equals("Y") && surroundingValue.equals("Y")) ? "Y" : "N";
            auditorValue = NameValue.toString();

            //validate
            //choose w all value must be filled, otherwise A 1, 3, 5, 6, 7 don't filled
            if(washTypeValue.equals("W")){
                if(dateValue.length() == 0 || unitValue.length() == 0 || titleValue.length() == 0 || handWashValue.length() == 0 || equipmentValue.length() == 0 || tissueValue.length() == 0
                        || washTypeValue.length() == 0 || contactPatientValue.length() == 0 || executeValue.length() == 0 || handWashValue.length() == 0 || bodyFluidValue.length() == 0
                        || contactPatientAfterValue.length() == 0 || surroundingValue.length() == 0 || openFaucetValue.length() == 0 || useWashHandValue.length() == 0
                        || soupHandKeepDownValue.length() == 0 || heartToHeartValue.length() == 0 || heartToBackValue.length() == 0 || sewToSewValue.length() == 0 || backToHeartValue.length() == 0
                        || handToThumbValue.length() == 0 || sharpToHeartValue.length() == 0 || fifteensecValue.length() == 0 || washHandValue.length() == 0 || wipeValue.length() == 0
                        || closeFaucetValue.length() == 0 || completeValue.length() == 0){
                    MyAlertDialog.setTitle("Message");
                    MyAlertDialog.setMessage("請填寫正確資料");
                    MyAlertDialog.show();
                }else{
                    //add value to google sheet
                    new MakeRequestTask(MainActivity.mCredential).execute();
                }
            }else{//choose wash a
                if(dateValue.length() == 0 || unitValue.length() == 0 || titleValue.length() == 0 || handWashValue.length() == 0 || equipmentValue.length() == 0 || tissueValue.length() == 0
                        || washTypeValue.length() == 0 || contactPatientValue.length() == 0 || executeValue.length() == 0 || handWashValue.length() == 0 || bodyFluidValue.length() == 0
                        || contactPatientAfterValue.length() == 0 || surroundingValue.length() == 0 || useWashHandValue.length() == 0 || heartToHeartValue.length() == 0
                        || heartToBackValue.length() == 0 || sewToSewValue.length() == 0 || backToHeartValue.length() == 0 || handToThumbValue.length() == 0 || sharpToHeartValue.length() == 0
                        || fifteensecValue.length() == 0 || completeValue.length() == 0){
                    MyAlertDialog.setTitle("Message");
                    MyAlertDialog.setMessage("請填寫正確資料");
                    MyAlertDialog.show();
                }else{
                    //add value to google sheet
                    new MakeRequestTask(MainActivity.mCredential).execute();
                }
            }
            }
        });
    }

    private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
        cYear = year - 1911;
        cMonth = monthOfYear + 1;
        String adjMonthOfYear = String.valueOf(cMonth).length() == 1 ? "0" + String.valueOf(cMonth) : String.valueOf(cMonth);
        cDay = String.valueOf(dayOfMonth).length() == 1 ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
        return cYear + adjMonthOfYear + cDay;
    }

    private void changeWashType(boolean wash){
        if(wash){//show 1, 3, 5, 6, 7
            washFirst.setVisibility(View.VISIBLE);
            washThird.setVisibility(View.VISIBLE);
            washFifth.setVisibility(View.VISIBLE);
            washSixth.setVisibility(View.VISIBLE);
            washSeventh.setVisibility(View.VISIBLE);
        }else{//hide 1, 3, 5, 6, 7
            washFirst.setVisibility(View.GONE);
            washThird.setVisibility(View.GONE);
            washFifth.setVisibility(View.GONE);
            washSixth.setVisibility(View.GONE);
            washSeventh.setVisibility(View.GONE);
        }
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
                intent.setClass(AddWashActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent();
                intent.setClass(AddWashActivity.this, WashActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent();
                intent.setClass(AddWashActivity.this, MdroActivity.class);
                startActivity(intent);
                break;
            case 3:
                MenuActivity.bundleName = "CVP";
                intent = new Intent();
                intent.setClass(AddWashActivity.this, CVPBundleActivity.class);
                startActivity(intent);
                break;
            case 4:
                MenuActivity.bundleName = "VAP";
                intent = new Intent();
                intent.setClass(AddWashActivity.this, VAPBundleActivity.class);
                startActivity(intent);
                break;
            case 5:
                MenuActivity.bundleName = "Foley";
                intent = new Intent();
                intent.setClass(AddWashActivity.this, FoleyBundleActivity.class);
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
            String sheetName = String.valueOf(cYear);
            range = sheetName+"!A:AC";
            try {
                Spreadsheet sheet_metadata = mService.spreadsheets().get(spreadsheetId).execute();
                List<Sheet> sheetList = sheet_metadata.getSheets();
                boolean matchSheetName = false;
                for(Sheet sheet:sheetList){
                    if(sheetName.equals(sheet.getProperties().getTitle())) {
                        matchSheetName = true;
                    }
                }
                if(!matchSheetName){//沒有此年度的wash, 先建立此年度的sheet

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
                    data1.add("月份");//月份
                    //data1.add("稽核日期");//稽核日期
                    data1.add("單位");//單位
                    data1.add("職稱");//職稱
                    data1.add("設備1");//設備1
                    data1.add("設備2");//設備2
                    data1.add("設備3");//設備3
                    data1.add("方式");//方式
                    data1.add("1病人前");//1病人前
                    data1.add("2清潔無菌技術前");//2清潔無菌技術前
                    data1.add("3體液後");//3體液後
                    data1.add("4病人後");//4病人後
                    data1.add("5環境後");//5環境後
                    data1.add("步驟1");//步驟1
                    data1.add("步驟2");//步驟2
                    data1.add("步驟3");//步驟3
                    data1.add("步驟4A");//步驟4A
                    data1.add("步驟4B");//步驟4B
                    data1.add("步驟4C");//步驟4C
                    data1.add("步驟4D");//步驟4D
                    data1.add("步驟4E");//步驟4E
                    data1.add("步驟4F");//步驟4F
                    data1.add("步驟4G");//步驟4G
                    data1.add("步驟5");//步驟5
                    data1.add("步驟6");//步驟6
                    data1.add("步驟7");//步驟7
                    data1.add("步驟8");//步驟8
                    data1.add("正確率");//正確率
                    data1.add("遵從率");//遵從率
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
                data1.add(cMonth);//月份
                //data1.add("稽核日期");//稽核日期
                data1.add(unitValue);//單位
                data1.add(titleValue);//職稱
                data1.add(handWashValue);//設備1
                data1.add(equipmentValue);//設備2
                data1.add(tissueValue);//設備3
                data1.add(washTypeValue);//方式
                data1.add(contactPatientValue);//1病人前
                data1.add(executeValue);//2清潔無菌技術前
                data1.add(bodyFluidValue);//3體液後
                data1.add(contactPatientAfterValue);//4病人後
                data1.add(surroundingValue);//5環境後
                data1.add(openFaucetValue);//步驟1
                data1.add(useWashHandValue);//步驟2
                data1.add(soupHandKeepDownValue);//步驟3
                data1.add(heartToHeartValue);//步驟4A
                data1.add(heartToBackValue);//步驟4B
                data1.add(sewToSewValue);//步驟4C
                data1.add(backToHeartValue);//步驟4D
                data1.add(handToThumbValue);//步驟4E
                data1.add(sharpToHeartValue);//步驟4F
                data1.add(fifteensecValue);//步驟4G
                data1.add(washHandValue);//步驟5
                data1.add(wipeValue);//步驟6
                data1.add(closeFaucetValue);//步驟7
                data1.add(completeValue);//步驟8
                //TODO 確認 correctValue, complianceRateValue 邏輯待處理
                data1.add(correctValue);//正確率
                data1.add(complianceRateValue);//遵從率
                //
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
                    intent.setClass(AddWashActivity.this, WashActivity.class);
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
