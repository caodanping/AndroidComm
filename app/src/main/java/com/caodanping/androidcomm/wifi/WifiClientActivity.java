package com.caodanping.androidcomm.wifi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.caodanping.androidcomm.Constants;
import com.caodanping.androidcomm.R;
import com.caodanping.androidcomm.collector.Collector;
import com.caodanping.androidcomm.collector.CollectorDataCallback;

public class WifiClientActivity extends AppCompatActivity {
    private NetConnectThread netConnectThread;
    private Button btnConnectWifi;
    private TextView txtXyz;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constants.MESSAGE_WIFI_CONNECTED) {
                Toast.makeText(WifiClientActivity.this, "连接成功.", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_client);

        btnConnectWifi = (Button) findViewById(R.id.btnConnectWifi);
        txtXyz = (TextView) findViewById(R.id.txtWifiXyz);
        btnConnectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetConnectThread thread = new NetConnectThread(new CollectorDataCallback() {
                    @Override
                    public void call(final Collector.CollectorData data) {
                        btnConnectWifi.post(new Runnable() {
                            @Override
                            public void run() {
                                txtXyz.setText(data.toDisplay());
                            }
                        });
                    }
                }, handler);
                thread.start();
                netConnectThread = thread;
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (netConnectThread != null) {
            netConnectThread.cancel();
        }
        super.onDestroy();
    }
}
