package com.example.echo.hospital.mdro;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.echo.hospital.MainActivity;
import com.example.echo.hospital.MenuActivity;
import com.example.echo.hospital.R;
import com.example.echo.hospital.bundle.AddVAPBundleActivity;
import com.example.echo.hospital.bundle.CVPBundleActivity;
import com.example.echo.hospital.bundle.FoleyBundleActivity;
import com.example.echo.hospital.bundle.VAPBundleActivity;
import com.example.echo.hospital.model.User;
import com.example.echo.hospital.wash.WashActivity;
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

public class AddMdroActivity extends AppCompatActivity {

    private EditText date, bed, comment, auditor;
    private RadioButton firstY, firstN, firstNA, secondY, secondN, secondNA, thirdY, thirdN, thirdNA, fourthY, fourthN, fourthNA, fifthY, fifthN, fifthNA,
                        sixthY, sixthN, sixthNA, seventhY, seventhN, seventhNA, eighthY, eighthN, eighthNA, ninethY, ninethN, ninethNA, tenthY, tenthN, tenthNA,
                        eleventhY, eleventhN, eleventhNA, twelfthY, twelfthN, twelfthNA, thirteenthY, thirteenthN, thirteenthNA, fourteenthY, fourteenthN, fourteenthNA,
                        fifteenthY, fifteenthN, fifteenthNA;
    private Spinner unitSpinner;
    private String dateValue, unitValue, bedValue, firstValue, secondValue, thirdValue, fourthValue, fifthValue, sixthValue, seventhValue, eighthValue, ninethValue, tenthValue,
            eleventhValue, twelfthValue, thirteenthValue, fourteenthValue, fifteenthValue, commentValue, completeValue, auditorValue;
    private String trueValue = "Y", falseValue = "N";
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
    String spreadsheetId = "1Wjgw8k4kaFmW4O3FfSLXHxO55c9HeWJwyX1k3Vu1yj8";
    // The A1 notation of a range to search for a logical table of data.
    // Values will be appended after the last row of the table.
    String range;
    ValueRange body = new ValueRange();
    //google sheet api -----end
    final Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mdro);
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
            new DatePickerDialog(AddMdroActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        unitSpinner = (Spinner)findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"ICU-1", "ICU-2", "ICU-3", "ICU-5", "5B", "6A", "6C", "7A", "7B", "8A", "8B", "9A", "9B", "10A", "10B", "12A", "12B", "13A", "13B", "HD"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        unitSpinner.setAdapter(adapter);

        //set bed number
        bed = (EditText) findViewById(R.id.EditText2);

        //set radiobutton
        firstY = (RadioButton) findViewById(R.id.RadioButton1);
        firstN = (RadioButton) findViewById(R.id.RadioButton2);
        firstNA = (RadioButton) findViewById(R.id.RadioButton3);
        secondY = (RadioButton) findViewById(R.id.RadioButton4);
        secondN = (RadioButton) findViewById(R.id.RadioButton5);
        secondNA = (RadioButton) findViewById(R.id.RadioButton6);
        thirdY = (RadioButton) findViewById(R.id.RadioButton7);
        thirdN = (RadioButton) findViewById(R.id.RadioButton8);
        thirdNA = (RadioButton) findViewById(R.id.RadioButton9);
        fourthY = (RadioButton) findViewById(R.id.RadioButton10);
        fourthN = (RadioButton) findViewById(R.id.RadioButton11);
        fourthNA = (RadioButton) findViewById(R.id.RadioButton12);
        fifthY = (RadioButton) findViewById(R.id.RadioButton13);
        fifthN = (RadioButton) findViewById(R.id.RadioButton14);
        fifthNA = (RadioButton) findViewById(R.id.RadioButton15);
        sixthY = (RadioButton) findViewById(R.id.RadioButton16);
        sixthN = (RadioButton) findViewById(R.id.RadioButton17);
        sixthNA = (RadioButton) findViewById(R.id.RadioButton18);
        seventhY = (RadioButton) findViewById(R.id.RadioButton19);
        seventhN = (RadioButton) findViewById(R.id.RadioButton20);
        seventhNA = (RadioButton) findViewById(R.id.RadioButton21);
        eighthY = (RadioButton) findViewById(R.id.RadioButton22);
        eighthN = (RadioButton) findViewById(R.id.RadioButton23);
        eighthNA = (RadioButton) findViewById(R.id.RadioButton24);
        ninethY = (RadioButton) findViewById(R.id.RadioButton25);
        ninethN = (RadioButton) findViewById(R.id.RadioButton26);
        ninethNA = (RadioButton) findViewById(R.id.RadioButton27);
        tenthY = (RadioButton) findViewById(R.id.RadioButton28);
        tenthN = (RadioButton) findViewById(R.id.RadioButton29);
        tenthNA = (RadioButton) findViewById(R.id.RadioButton30);
        eleventhY = (RadioButton) findViewById(R.id.RadioButton31);
        eleventhN = (RadioButton) findViewById(R.id.RadioButton32);
        eleventhNA = (RadioButton) findViewById(R.id.RadioButton33);
        twelfthY = (RadioButton) findViewById(R.id.RadioButton34);
        twelfthN = (RadioButton) findViewById(R.id.RadioButton35);
        twelfthNA = (RadioButton) findViewById(R.id.RadioButton36);
        thirteenthY = (RadioButton) findViewById(R.id.RadioButton37);
        thirteenthN = (RadioButton) findViewById(R.id.RadioButton38);
        thirteenthNA = (RadioButton) findViewById(R.id.RadioButton39);
        fourteenthY = (RadioButton) findViewById(R.id.RadioButton40);
        fourteenthN = (RadioButton) findViewById(R.id.RadioButton41);
        fourteenthNA = (RadioButton) findViewById(R.id.RadioButton42);
        fifteenthY= (RadioButton) findViewById(R.id.RadioButton43);
        fifteenthN = (RadioButton) findViewById(R.id.RadioButton44);
        fifteenthNA = (RadioButton) findViewById(R.id.RadioButton45);

        //set comment
        comment = (EditText)findViewById(R.id.EditText3);

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
            unitValue = unitSpinner.getSelectedItem().toString();
            bedValue = bed.getText().toString();
            firstValue = firstY.isChecked() ? firstY.getText().toString() : firstN.isChecked() ? firstN.getText().toString() : "";
            secondValue = secondY.isChecked() ? secondY.getText().toString() : secondN.isChecked() ? secondN.getText().toString() : "";
            thirdValue = thirdY.isChecked() ? thirdY.getText().toString() : thirdN.isChecked() ? thirdN.getText().toString() : "";
            fourthValue = fourthY.isChecked() ? fourthY.getText().toString() : fourthN.isChecked() ? fourthN.getText().toString() : "";
            fifthValue = fifthY.isChecked() ? fifthY.getText().toString() : fifthN.isChecked() ? fifthN.getText().toString() : "";
            sixthValue = sixthY.isChecked() ? sixthY.getText().toString() : sixthN.isChecked() ? sixthN.getText().toString() : "";
            seventhValue = seventhY.isChecked() ? seventhY.getText().toString() : seventhN.isChecked() ? seventhN.getText().toString() : "";
            eighthValue = eighthY.isChecked() ? eighthY.getText().toString() : eighthN.isChecked() ? eighthN.getText().toString() : "";
            ninethValue = ninethY.isChecked() ? ninethY.getText().toString() : ninethN.isChecked() ? ninethN.getText().toString() : "";
            tenthValue = tenthY.isChecked() ? tenthY.getText().toString() : tenthN.isChecked() ? tenthN.getText().toString() : "";
            eleventhValue = eleventhY.isChecked() ? eleventhY.getText().toString() : eleventhN.isChecked() ? eleventhN.getText().toString()  : "";
            twelfthValue = twelfthY.isChecked() ? twelfthY.getText().toString() : twelfthN.isChecked() ? twelfthN.getText().toString() : "";
            thirteenthValue = thirteenthY.isChecked() ? thirteenthY.getText().toString() : thirteenthN.isChecked() ? thirteenthN.getText().toString() : "";
            fourteenthValue = fourteenthY.isChecked() ? fourteenthY.getText().toString() : fourteenthN.isChecked() ? fourteenthN.getText().toString() : "";
            fifteenthValue = fifteenthY.isChecked() ? fifteenthY.getText().toString() : fifteenthN.isChecked() ? fifteenthN.getText().toString() : "";
            completeValue = ( firstValue.equals(falseValue) || secondValue.equals(falseValue) || thirdValue.equals(falseValue) || fourthValue.equals(falseValue) ||
                    fifthValue.equals(falseValue) || sixthValue.equals(falseValue) || seventhValue.equals(falseValue) || eighthValue.equals(falseValue) ||
                    ninethValue.equals(falseValue) || tenthValue.equals(falseValue) || eleventhValue.equals(falseValue) || twelfthValue.equals(falseValue) ||
                    thirteenthValue.equals(falseValue) || fourteenthValue.equals(falseValue) || fifteenthValue.equals(falseValue) ) ? falseValue : trueValue;
            commentValue = comment.getText().toString();
            auditorValue = NameValue.toString();

            //validate
            if(dateValue.length() == 0 || unitValue.length() == 0 || bedValue.length() == 0 || auditorValue.length() == 0){
                MyAlertDialog.setTitle("Message");
                MyAlertDialog.setMessage("請填寫正確資料");
                MyAlertDialog.show();
            }else{
                //save to excel
                new AddMdroActivity.MakeRequestTask(MainActivity.mCredential).execute();
            }
            }
        });

    }

    //set tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        menu.add(0, 0, 0, "主選單");
        menu.add(0, 1, 1, "洗手稽核列表");
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
                intent.setClass(AddMdroActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent();
                intent.setClass(AddMdroActivity.this, WashActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent();
                intent.setClass(AddMdroActivity.this, MdroActivity.class);
                startActivity(intent);
                break;
            case 3:
                MenuActivity.bundleName = "CVP";
                intent = new Intent();
                intent.setClass(AddMdroActivity.this, CVPBundleActivity.class);
                startActivity(intent);
                break;
            case 4:
                MenuActivity.bundleName = "VAP";
                intent = new Intent();
                intent.setClass(AddMdroActivity.this, VAPBundleActivity.class);
                startActivity(intent);
                break;
            case 5:
                MenuActivity.bundleName = "Foley";
                intent = new Intent();
                intent.setClass(AddMdroActivity.this, FoleyBundleActivity.class);
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
            String sheetName = String.valueOf(cYear);
            range = sheetName+"!A:V";
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
                    data1.add("1");//第1題
                    data1.add("2");//第2題
                    data1.add("3");//第3題
                    data1.add("4");//第4題
                    data1.add("5");//第5題
                    data1.add("6");//第6題
                    data1.add("7");//第7題
                    data1.add("8");//第8題
                    data1.add("9");//第9題
                    data1.add("10");//第10題
                    data1.add("11");//第11題
                    data1.add("12");//第12題
                    data1.add("13");//第13題
                    data1.add("14");//第14題
                    data1.add("15");//第15題
                    data1.add("達成");//達成
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
                data1.add(firstValue);//第1題
                data1.add(secondValue);//第2題
                data1.add(thirdValue);//第3題
                data1.add(fourthValue);//第4題
                data1.add(fifthValue);//第5題
                data1.add(sixthValue);//第6題
                data1.add(seventhValue);//第7題
                data1.add(eighthValue);//第8題
                data1.add(ninethValue);//第9題
                data1.add(tenthValue);//第10題
                data1.add(eleventhValue);//第11題
                data1.add(twelfthValue);//第12題
                data1.add(thirteenthValue);//第13題
                data1.add(fourteenthValue);//第14題
                data1.add(fifteenthValue);//第15題
                data1.add(completeValue);//達成
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
                    intent.setClass(AddMdroActivity.this, MdroActivity.class);
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
