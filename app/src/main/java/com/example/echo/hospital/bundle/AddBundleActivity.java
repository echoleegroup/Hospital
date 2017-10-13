package com.example.echo.hospital.bundle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Button;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import java.util.List;
import java.util.Arrays;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;


import com.example.echo.hospital.R;
import com.example.echo.hospital.model.User;

public class AddBundleActivity extends AppCompatActivity {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    //save to excel

                    //TODO
                    /*
                    List<List<Object>> values = Arrays.asList(
                            Arrays.asList(
                                    // Cell values ...
                            )
                            // Additional rows ...
                    );
                    ValueRange body = new ValueRange()
                            .setValues(values);
                    UpdateValuesResponse result =
                            service.spreadsheets().values().update(spreadsheetId, range, body)
                                    .setValueInputOption(valueInputOption)
                                    .execute();
                    */
                }

            }
        });

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
