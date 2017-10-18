package com.example.echo.hospital;

import android.content.Context;
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
import java.util.Arrays;
import java.util.Calendar;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.SheetsScopes;

import com.example.echo.hospital.model.User;

public class AddBundleActivity extends Activity {
    private EditText date, bed, patient, comment, auditor;
    private RadioButton unitOne, unitTwo, doctorSignTrue, doctorSignFalse, nurseSignTrue, nurseSignFalse;
    private String dateValue, unitValue, bedValue, patientValue, doctorSignValue, nurseSignValue, commentValue, auditorValue;
    private Button saveBtn;
    private int mYear, mMonth, mDay; //西元年
    private int cYear, cMonth; //民國
    private Builder MyAlertDialog;

    //store account and password -----start
    private final String DefaultNameValue = "";
    private String NameValue;
    //store account and password -----end

    //google sheet api -----start
    // The ID of the spreadsheet to update.
    private GoogleAccountCredential credential;
    final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    String spreadsheetId = "1ythc41RFh9JmO0hXyZYNghXfXNPn7-NcPbRHosof_sE";
    // The A1 notation of a range to search for a logical table of data.
    // Values will be appended after the last row of the table.
    String range = "106!A1:J";
    ValueRange body = new ValueRange();
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };
    //google sheet api -----end
    String value;

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
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
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
                doctorSignValue = doctorSignTrue.isChecked() ? "是": doctorSignFalse.isChecked() ? "否" : "";
                nurseSignValue = nurseSignTrue.isChecked() ? "是": nurseSignFalse.isChecked() ? "否" : "";
                commentValue = comment.getText().toString();
                auditorValue = NameValue.toString();

                //validate
                if(dateValue.length() == 0 || unitValue.length() == 0 || bedValue.length() == 0 || patientValue.length() == 0 || doctorSignValue.length() == 0 || nurseSignValue.length() == 0  || auditorValue.length() == 0){
                    MyAlertDialog.setTitle("Message");
                    MyAlertDialog.setMessage("請填寫正確資料");
                    MyAlertDialog.show();
                }else{

                    //年度, 月份, 稽核日期, 單位, 床號, 病歷號, 醫師/NP, 護理師, 總完整, 稽核者
                    value = cYear + "," + cMonth +","+ dateValue +","+ unitValue +","+ bedValue +","+ patientValue +","+ doctorSignValue +","+ nurseSignValue +","+ commentValue +","+ auditorValue;


                    try{
                        //save to excel
                        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                            private com.google.api.services.sheets.v4.Sheets service = new com.google.api.services.sheets.v4.Sheets.Builder(httpTransport, jsonFactory, credential)
                                    .setApplicationName("Google Sheets API Android Quickstart").build();

                            @Override
                            protected String doInBackground(Void... params) {
                                List<String> results = new ArrayList<String>();
                                Exception mLastError = null;
                                try {

                                    List<Object> data1 = new ArrayList<Object>();
                                    data1.add(value);

                                    List<List<Object>> data = new ArrayList<List<Object>>();
                                    data.add (data1);

                                    List<List<Object>> values = data;

                                    body = new ValueRange()
                                            .setValues(values);

                                    Sheets.Spreadsheets.Values.Append request =
                                            service.spreadsheets().values().append(spreadsheetId, range, body);

                                    AppendValuesResponse response = request.setInsertDataOption("INSERT_ROWS").execute();
                                    // TODO: Change code below to process the `response` object:
                                    System.out.println(response);
                                    return "successful";
                                } catch (Exception e) {
                                    mLastError = e;
                                    cancel(true);
                                    return "failure";
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

        // Initialize credentials and service object.
        // Google Accounts
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        SharedPreferences allSettings = getPreferences(Context.MODE_PRIVATE);
        credential.setSelectedAccountName(allSettings.getString(PREF_ACCOUNT_NAME, null));
    }


    private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
        cYear = year-1911;
        cMonth = monthOfYear;
        String adjMonthOfYear = String.valueOf(cMonth + 1).length() == 1 ? "0" + String.valueOf(cMonth + 1) : String.valueOf(cMonth + 1);

        return String.valueOf(cYear)
                + adjMonthOfYear
                + String.valueOf(dayOfMonth);
    }

}
