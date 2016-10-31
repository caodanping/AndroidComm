package com.caodanping.androidcomm.collector;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.caodanping.androidcomm.Constants;
import com.caodanping.androidcomm.R;
import com.caodanping.androidcomm.bluetooth.BtAcceptThread;
import com.caodanping.androidcomm.wifi.DiscoveryManager;
import com.caodanping.androidcomm.wifi.WifiApManager;

import java.util.HashMap;
import java.util.Map;

public class CollectorActivity extends AppCompatActivity implements SensorEventListener {

    private static final int REQUEST_ENABLE_BT = 101;
    private static final int REQUEST_ENABLE_BT_DISCOVERY = 102;

    private Collector collector = null;
    private BtAcceptThread btAcceptThread = null;
    private BluetoothAdapter bluetoothAdapter = null;

    private boolean started = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private WifiApManager wifiApManager;
    private WifiManager wifiManager;


    private Button btn;
    private Button btnEnableBt;
    private Button btnEnableBtDiscovery;
    private Button btnEnableWifiAp;
    private Button btnWifiP2pDiscovery;

    private TextView txtXVal;
    private TextView txtYVal;
    private TextView txtZVal;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private DiscoveryManager discoveryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);

        btn =  (Button) findViewById(R.id.btnStartCollector);
        btnEnableBt = (Button) findViewById(R.id.btnEnableBt);
        btnEnableBtDiscovery = (Button) findViewById(R.id.btnEnableDiscovery);
        btnEnableWifiAp = (Button) findViewById(R.id.btnEnableWifiAp);
        btnWifiP2pDiscovery = (Button) findViewById(R.id.btnEnableWifiP2pNsd);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        txtXVal = (TextView) findViewById(R.id.txtXVal);
        txtYVal = (TextView) findViewById(R.id.txtYVal);
        txtZVal = (TextView) findViewById(R.id.txtZVal);

        btn.setVisibility(View.INVISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    btn.setText(getText(R.string.stopCollector));
                    started = true;

                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter == null) {
                        Toast.makeText(CollectorActivity.this, "不支持蓝牙", Toast.LENGTH_SHORT);
                        return;
                    }

                    // 开启
                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                } else {
                    btn.setText(getText(R.string.startCollector));
                    started = false;
                }
            }
        });

        btnEnableBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null) {
                    Toast.makeText(CollectorActivity.this, "不支持蓝牙", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 开启
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });

        btnEnableWifiAp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiConfiguration config = new WifiConfiguration();
                config.SSID = Constants.DEVICE_NO;
                config.preSharedKey = Constants.DEVICE_NO;
                config.allowedAuthAlgorithms.set(1);

                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiApManager.setWifiApEnabled(config, true);
            }
        });

        btnEnableBtDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivityForResult(discoverableIntent, REQUEST_ENABLE_BT_DISCOVERY);
            }
        });

        btnWifiP2pDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> textMap = new HashMap<>();
                textMap.put("ip", "192.168.11.1");
                textMap.put("port", "20001");

                // instanceName：服务的实例名称
                // serviceType：服务的类型，这个好像可以自定义，反正我用的是自己定义的名称
                // txtMap：服务携带的信息，在被发现的时候可以由对方获取，很有用！！！
                WifiP2pDnsSdServiceInfo servInfo = WifiP2pDnsSdServiceInfo.newInstance(Constants.DEVICE_NO,
                        "_sld3._tcp",    // 服务类型:_协议名._传输层协议
                        textMap);
                // 4.1以上
                wifiP2pManager.addLocalService(channel, servInfo, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(CollectorActivity.this, "添加WifiP2p本地服务成功.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e("P2pServiceDiscovery", "Add Local Service failed.");
                    }
                });
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        wifiApManager = new WifiApManager(this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);

        collector = new Collector();
    }

    @Override
    protected void onResume() {
        super.onResume();

        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
        discoveryManager = new DiscoveryManager(wifiP2pManager, channel, this);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        wifiApManager.setWifiApEnabled(null, false);
        super.onStop();

        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // 蓝牙开启成功
                Toast.makeText(this, "蓝牙开启成功", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_ENABLE_BT_DISCOVERY) {
            if (resultCode > RESULT_CANCELED) {
                // 蓝牙发现开启成功
                Toast.makeText(this, "蓝牙发现开启成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] xyz = event.values;
        collector.setXyz(xyz[0], xyz[1], xyz[2]);
        txtXVal.setText(String.valueOf(xyz[0]));
        txtYVal.setText(String.valueOf(xyz[1]));
        txtZVal.setText(String.valueOf(xyz[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
