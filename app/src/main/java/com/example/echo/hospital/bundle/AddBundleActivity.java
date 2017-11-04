package com.example.echo.hospital.bundle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Button;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import com.example.echo.hospital.MainActivity;
import com.example.echo.hospital.MenuActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;



import com.example.echo.hospital.R;
import com.example.echo.hospital.model.User;

public class AddBundleActivity extends Activity{
    private EditText date, bed, patient, comment, auditor;
    private RadioButton unitOne, unitTwo, doctorSignTrue, doctorSignFalse, nurseSignTrue, nurseSignFalse;
    private String dateValue, unitValue, bedValue, patientValue, doctorSignValue, nurseSignValue, commentValue, completeValue, auditorValue;
    private Button saveBtn;
    private int mYear, mMonth, mDay; //西元年
    private int cYear, cMonth; //民國年月
    private String cDay; //民國日
    private Builder MyAlertDialog;

    //store account and password -----start
    private final String DefaultNameValue = "";
    private String NameValue;
    //store account and password -----end

    //google sheet api -----start
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

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
    final Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bundle);

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
                new DatePickerDialog(AddBundleActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        unitOne = (RadioButton) findViewById(R.id.RadioButton1);
        unitTwo = (RadioButton) findViewById(R.id.RadioButton2);

        //set bed number
        bed = (EditText) findViewById(R.id.EditText2);

        //set patient
        patient = (EditText) findViewById(R.id.EditText3);

        //set doctor sign radiobutton
        doctorSignTrue = (RadioButton) findViewById(R.id.RadioButton3);
        doctorSignFalse = (RadioButton) findViewById(R.id.RadioButton4);

        //set nurse sign radiobutton
        nurseSignTrue = (RadioButton) findViewById(R.id.RadioButton5);
        nurseSignFalse = (RadioButton) findViewById(R.id.RadioButton6);

        //set comment
        comment = (EditText) findViewById(R.id.EditText4);

        //set auditor name
        //get input account and password  ---- start
        SharedPreferences settings = getSharedPreferences(User.PREFS_NAME,
                Context.MODE_PRIVATE);
        //Get value
        NameValue = settings.getString(User.PREF_NAME, DefaultNameValue);
        //get input account and password  ---- end

        auditor = (EditText) findViewById(R.id.EditText5);
        auditor.setText(NameValue);

        //save button click event
        saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //get all fill value
                dateValue = date.getText().toString();
                unitValue = unitOne.isChecked() ? "ICU-1": unitTwo.isChecked() ? "ICU-2" : "";
                bedValue = bed.getText().toString();
                patientValue = patient.getText().toString();
                doctorSignValue = doctorSignTrue.isChecked() ? "Y": doctorSignFalse.isChecked() ? "N" : "";
                nurseSignValue = nurseSignTrue.isChecked() ? "Y": nurseSignFalse.isChecked() ? "N" : "";
                commentValue = comment.getText().toString().equals("") ? "NA" : "";
                completeValue = doctorSignValue.equals("Y") && nurseSignValue.equals("Y") ? "Y" : "N";
                auditorValue = NameValue.toString();

                //validate
                if(dateValue.length() == 0 || unitValue.length() == 0 || bedValue.length() == 0 || patientValue.length() == 0 || doctorSignValue.length() == 0 || nurseSignValue.length() == 0  || auditorValue.length() == 0){
                    MyAlertDialog.setTitle("Message");
                    MyAlertDialog.setMessage("請填寫正確資料");
                    MyAlertDialog.show();
                }else{

                    try{
                        //save to excel
                        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                            private com.google.api.services.sheets.v4.Sheets service = new com.google.api.services.sheets.v4.Sheets.Builder(httpTransport, jsonFactory, MainActivity.credential)
                                    .setApplicationName("Google Sheets API Android Quickstart").build();

                            @Override
                            protected String doInBackground(Void... params) {
                                List<String> results = new ArrayList<String>();
                                Exception mLastError = null;
                                String sheetName = cYear+ MenuActivity.bundleName;
                                range = sheetName+"!A:J";
                                try {
                                    Spreadsheet sheet_metadata = service.spreadsheets().get(spreadsheetId).execute();
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
                                        service.spreadsheets().batchUpdate(spreadsheetId,batchUpdateSpreadsheetRequest).execute();

                                        List<Object> data1 = new ArrayList<Object>();
                                        data1.add("年度");//年度
                                        data1.add("月份");//月份
                                        data1.add("稽核日期");//稽核日期
                                        data1.add("單位");//單位
                                        data1.add("床號");//床號
                                        data1.add("病歷號");//病歷號
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
                                                service.spreadsheets().values().append(spreadsheetId, range, body).setValueInputOption("RAW");

                                        AppendValuesResponse response = requestAddFirstColumnName.setInsertDataOption("INSERT_ROWS").execute();
                                    }

                                    List<Object> data1 = new ArrayList<Object>();
                                    data1.add(cYear);//年度
                                    data1.add(cMonth);//月份
                                    data1.add(dateValue);//稽核日期
                                    data1.add(unitValue);//單位 //下拉式選單
                                    data1.add(bedValue);//床號
                                    data1.add(patientValue);//病歷號
                                    data1.add(doctorSignValue);//醫師/NP
                                    data1.add(nurseSignValue);//護理師
                                    //TODO 項次 commentValue 下拉式選單
                                    data1.add(completeValue);//總完整
                                    data1.add(auditorValue);//稽核者

                                    List<List<Object>> data = new ArrayList<List<Object>>();
                                    data.add (data1);

                                    List<List<Object>> values = data;

                                    body = new ValueRange()
                                            .setValues(values);

                                    Sheets.Spreadsheets.Values.Append request =
                                            service.spreadsheets().values().append(spreadsheetId, range, body).setValueInputOption("RAW");

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
                                if(output.equals("successful")){
                                    Intent intent = new Intent();
                                    intent.setClass(AddBundleActivity.this, BundleActivity.class);
                                    startActivity(intent);
                                    /*MyAlertDialog.setTitle("Message");
                                    MyAlertDialog.setMessage("新增成功");
                                    MyAlertDialog.show();*/
                                }else{
                                    MyAlertDialog.setTitle("Message");
                                    MyAlertDialog.setMessage("新增失敗");
                                    MyAlertDialog.show();
                                }
                            }
                        };

                        task.execute();
                    }
                    catch(Exception e){
                        e.printStackTrace();
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
}
