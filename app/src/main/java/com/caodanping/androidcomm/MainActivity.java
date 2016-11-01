package com.caodanping.androidcomm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.caodanping.androidcomm.bluetooth.BtClientActivity;
import com.caodanping.androidcomm.collector.CollectorActivity;
import com.caodanping.androidcomm.wifi.WifiClientActivity;
import com.caodanping.androidcomm.wifi.WifiP2pDiscoveryActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnToCollector;
    private Button btnBtClientTest;
    private Button btnWifiP2pNsdTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToCollector = (Button) findViewById(R.id.btnToCollector);
        btnBtClientTest = (Button) findViewById(R.id.btnBtClientTest);
        btnWifiP2pNsdTest = (Button) findViewById(R.id.btnWifiP2pServiceTest);
        Button btnWifiClientTest = (Button) findViewById(R.id.btnWifiClientTest);

        btnToCollector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CollectorActivity.class);
                startActivity(intent);
            }
        });

        btnBtClientTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BtClientActivity.class);
                startActivity(intent);
            }
        });

        btnWifiP2pNsdTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WifiP2pDiscoveryActivity.class);
                startActivity(intent);
            }
        });

        btnWifiClientTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WifiClientActivity.class);
                startActivity(intent);
            }
        });
    }


}
