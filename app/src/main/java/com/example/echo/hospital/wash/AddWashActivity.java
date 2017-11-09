package com.example.echo.hospital.wash;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.RadioButton;

import com.example.echo.hospital.R;
import com.example.echo.hospital.bundle.AddBundleActivity;
import com.example.echo.hospital.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.Calendar;

public class AddWashActivity extends AppCompatActivity {
    private EditText date, unit, title;
    private RadioButton handWashTrue, handWashFalse, equipmentTrue, equipmentFalse, tissueTrue, tissueFalse, washTypeW, washTypeA, contactPatientTrue, contactPatientFalse, executeTrue, executeFalse, bodyFluidTrue, bodyFluidFalse, contactPatientAfterTrue, contactPatientAfterFalse;
    private RadioButton surroundingTrue, surroundingFalse, openFaucetTrue, openFaucetFalse, useWashHandTrue, useWashHandFalse, soupHandKeepDownTrue, soupHandKeepDownFalse;
    private RadioButton heartToHeartTrue, heartToHeartFalse, heartToBackTrue, heartToBackFalse, sewToSewTrue, sewToSewFalse;
    private RadioButton backToHeartTrue, backToHeartFalse, handToThumbTrue, handToThumbFalse, sharpToHeartTrue, sharpToHeartFalse, fifteensecTrue, fifteensecFalse;
    private RadioButton washHandTrue, washHandFalse, wipeTrue, wipeFalse, closeFaucetTrue, closeFaucetFalse, completeTrue, completeFalse;
    private int mYear, mMonth, mDay; //西元年
    private int cYear, cMonth; //民國年月
    private String cDay; //民國日
    private AlertDialog.Builder MyAlertDialog;

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
        unit = (EditText) findViewById(R.id.EditText3);

        //set title
        title = (EditText) findViewById(R.id.EditText4);

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
        washTypeA = (RadioButton) findViewById(R.id.RadioButton8);

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

        //TODO

        //set auditor name
        //get input account and password  ---- start
        SharedPreferences settings = getSharedPreferences(User.PREFS_NAME,
                Context.MODE_PRIVATE);
    }

    private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
        cYear = year - 1911;
        cMonth = monthOfYear + 1;
        String adjMonthOfYear = String.valueOf(cMonth).length() == 1 ? "0" + String.valueOf(cMonth) : String.valueOf(cMonth);
        cDay = String.valueOf(dayOfMonth).length() == 1 ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
        return cYear + adjMonthOfYear + cDay;
    }
}
