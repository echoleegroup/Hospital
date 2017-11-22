package com.example.echo.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.echo.hospital.bundle.VAPBundleActivity;
import com.example.echo.hospital.bundle.CVPBundleActivity;
import com.example.echo.hospital.bundle.FoleyBundleActivity;
import com.example.echo.hospital.wash.WashActivity;
import com.example.echo.hospital.mdro.MdroActivity;

public class MenuActivity extends AppCompatActivity {

    private ListView listView;
    private View headerView;
    private TextView headerTextView;
    private ArrayAdapter adapter;
    public static String bundleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //init view
        listView = (ListView) findViewById(R.id.list);

        //set header
        headerView = (View)getLayoutInflater().inflate(R.layout.menu_header_view, null);
        listView.addHeaderView(headerView);
        headerTextView = (TextView)findViewById(R.id.menuHeader);
        headerTextView.setText("感染科稽核系統主選單");

        //set menu
        adapter = new ArrayAdapter(this, R.layout.menu_adapter);
        //0- 洗手稽核表
        //1- MDRO稽核表
        //2- Bundle CVP稽核表
        //3- Bundle Foley稽核表
        //4- Bundle VAP稽核表
        adapter.add("洗手稽核表");
        adapter.add("MDRO稽核表");
        adapter.add("Bundle CVP稽核表");
        adapter.add("Bundle VAP稽核表");
        adapter.add("Bundle Foley稽核表");
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {//arg2選單文字
            if(arg3 == 0L){//洗手稽核表
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, WashActivity.class);
                startActivity(intent);
            }else if(arg3 == 1L){//MDRO稽核表
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, MdroActivity.class);
                startActivity(intent);
            }
            else if(arg3 == 2L){//Bundle CVP稽核表
                bundleName = "CVP";
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, CVPBundleActivity.class);
                startActivity(intent);
            }else if(arg3 == 3L){//Bundle VAP稽核表
                bundleName = "VAP";
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, VAPBundleActivity.class);
                startActivity(intent);
            }else if(arg3 == 4L){//Bundle Foley稽核表
                bundleName = "Foley";
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, FoleyBundleActivity.class);
                startActivity(intent);
            }
            }
        });
    }
}
