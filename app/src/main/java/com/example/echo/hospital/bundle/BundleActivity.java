package com.example.echo.hospital.bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.Context;
import android.view.View;
import android.support.design.widget.FloatingActionButton;

import com.example.echo.hospital.R;
import com.example.echo.hospital.model.User;

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
    private FloatingActionButton addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundle);

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
