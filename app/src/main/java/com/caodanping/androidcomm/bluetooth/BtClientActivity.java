package com.caodanping.androidcomm.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.caodanping.androidcomm.R;
import com.caodanping.androidcomm.collector.Collector;
import com.caodanping.androidcomm.collector.CollectorDataCallback;

import java.util.ArrayList;
import java.util.List;

public class BtClientActivity extends AppCompatActivity {
    // 常量
    public static final int REQUEST_ENABLE_BT = 1;

    // 发现设备
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(BtClientActivity.this, "发现设备", Toast.LENGTH_SHORT).show();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
                listAdapter.add("name:" + device.getName() + ",address" + device.getAddress());
                devices.add(device);
                listAdapter.notifyDataSetChanged();
                Log.d("BtClientActivity", device.getName() + " is found.");
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

    private List<BluetoothDevice> devices = new ArrayList<>();

    private ListView listBt;
    private ArrayAdapter<String> listAdapter;
    private TextView txtXyz;

    private BluetoothAdapter bluetoothAdapter;
    private BtConnectThread btConnectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_client);


        listBt = (ListView) findViewById(R.id.listBt);
        listAdapter = new ArrayAdapter<String>(this, R.layout.activity_bt_client);
        listBt.setAdapter(listAdapter);

        txtXyz = (TextView) findViewById(R.id.txtXyz);

        listBt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = devices.get(position);
                btConnectThread = new BtConnectThread(bluetoothAdapter, device, new CollectorDataCallback() {
                    @Override
                    public void call(Collector.CollectorData data) {
                        txtXyz.setText("X:" + data.getX() + ", Y:" + data.getY() + ", Z:" + data.getZ());
                    }
                });
            }
        });

        registerBluetoothDeviceFoundReceiver();
        setupBluetooth();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(receiver);
        bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.disable();
    }

    private void registerBluetoothDeviceFoundReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    private void setupBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // 不支持蓝牙
            Toast.makeText(this, "不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        // 开启蓝牙
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙开启成功", Toast.LENGTH_SHORT).show();
                if(bluetoothAdapter.startDiscovery()) {
                    Toast.makeText(this, "开始蓝牙发现", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }
}
