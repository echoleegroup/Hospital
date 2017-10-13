package com.example.echo.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.echo.hospital.bundle.BundleActivity;

public class MenuActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        listView = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1);
        //0-洗手稽核表
        //1-Bundle稽核表
        //2-MDRO稽核表
        adapter.add("洗手稽核表");
        adapter.add("Bundle稽核表");
        adapter.add("MDRO稽核表");
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                ListView listView = (ListView) arg0;
                /*Toast.makeText(
                    MenuActivity.this,
                    "ID：" + arg3 + " 選單文字："+ listView.getItemAtPosition(arg2).toString(),
                    Toast.LENGTH_LONG).show();
                */
                if(arg3 == 0L){//洗手稽核表

                }
                else if(arg3 == 1L){//Bundle稽核表
                    Intent intent = new Intent();
                    intent.setClass(MenuActivity.this, BundleActivity.class);
                    startActivity(intent);
                }else if(arg3 == 2L){//MDRO稽核表

                }
            }
        });



    }

}
