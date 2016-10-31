package com.caodanping.androidcomm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.LinkedHashSet;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    // 常量
    public static final int REQUEST_ENABLE_BT = 1;

    // 发现设备
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
                /*
                  Performing device discovery is a heavy procedure for the Bluetooth adapter and will consume a lot of its resources.
                  Once you have found a device to connect, be certain that you always stop discovery with cancelDiscovery()
                  before attempting a connection. Also, if you already hold a connection with a device,
                  then performing discovery can significantly reduce the bandwidth available for the connection,
                   so you should not perform discovery while connected.
                 */
            }
        }
    };


    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // 注册蓝牙设备发现处理
        //registerBluetoothDeviceFoundReceiver();
    }

    private void registerBluetoothDeviceFoundReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    private void setupBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // 不支持蓝牙
            return;
        }

        // 开启蓝牙
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * 查询已配对设备
     */
    private void queryPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // TODO: 2016/10/27  更新到画面
            }
        }
    }

    /**
     * 开始发现设备
     */
    private void startDiscovery() {
        // The discovery process usually involves an inquiry scan of about 12 seconds,
        // followed by a page scan of each found device to retrieve its Bluetooth name.
        // 这是一个异步方法
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 开启可发现性
     */
    private void enableDiscoverability() {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // 在300秒内可发现，最大3600秒，设置为0表示永久可发现
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        // 此时会显示一个对话框，请求用户权限使设备可发现，选择是设备将在300秒内变得可发现，
        // Activity会收到一个onActivityResult回调
    }

    private void connectAsServer() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // 蓝牙开启成功
            }
        }
    }
}
