package com.example.echo.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.SharedPreferences;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.echo.hospital.bundle.VAPBundleActivity;
import com.example.echo.hospital.bundle.CVPBundleActivity;
import com.example.echo.hospital.bundle.FoleyBundleActivity;
import com.example.echo.hospital.model.User;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init view
        listView = (ListView) findViewById(R.id.list);

        //set header
        /*headerView = (View)getLayoutInflater().inflate(R.layout.menu_header_view, null);
        listView.addHeaderView(headerView);
        headerTextView = (TextView)findViewById(R.id.menuHeader);
        headerTextView.setText("感染控制稽核系統主選單");*/

        //set menu
        adapter = new ArrayAdapter(this, R.layout.menu_adapter);
        //0- 洗手稽核表
        //1- MDRO稽核表
        //2- Bundle CVP稽核表
        //3- Bundle Foley稽核表
        //4- Bundle VAP稽核表
        adapter.add("手部衛生稽核列表");
        adapter.add("MDRO稽核列表");
        adapter.add("Bundle CVP稽核列表");
        adapter.add("Bundle VAP稽核列表");
        adapter.add("Bundle Foley稽核列表");
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

    //set tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        MenuItem item = menu.add(0, 0, 0, "home"); //your desired title here
        item.setIcon(R.drawable.ic_action_name); //your desired icon here
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 1, 1, "手部衛生稽核列表");
        menu.add(0, 2, 2, "MDRO稽核列表");
        menu.add(0, 3, 3, "Bundle CVP稽核列表");
        menu.add(0, 4, 4, "Bundle VAP稽核列表");
        menu.add(0, 5, 5, "Bundle Foley稽核列表");
        menu.add(0, 6, 6, "登出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //依據itemId來判斷使用者點選哪一個item
        switch(item.getItemId()) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent();
                intent.setClass(MenuActivity.this, WashActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent();
                intent.setClass(MenuActivity.this, MdroActivity.class);
                startActivity(intent);
                break;
            case 3:
                bundleName = "CVP";
                intent = new Intent();
                intent.setClass(MenuActivity.this, CVPBundleActivity.class);
                startActivity(intent);
                break;
            case 4:
                bundleName = "VAP";
                intent = new Intent();
                intent.setClass(MenuActivity.this, VAPBundleActivity.class);
                startActivity(intent);
                break;
            case 5:
                bundleName = "Foley";
                intent = new Intent();
                intent.setClass(MenuActivity.this, FoleyBundleActivity.class);
                startActivity(intent);
                break;
            case 6:
                SharedPreferences preferences = getSharedPreferences(User.PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                intent = new Intent();
                intent.setClass(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
