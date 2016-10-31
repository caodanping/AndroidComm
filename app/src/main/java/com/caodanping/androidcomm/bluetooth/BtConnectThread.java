package com.caodanping.androidcomm.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.caodanping.androidcomm.Constants;
import com.caodanping.androidcomm.collector.Collector;
import com.caodanping.androidcomm.collector.CollectorDataCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by caodanping on 2016/10/27.
 */

public class BtConnectThread extends Thread {

    private final BluetoothAdapter bluetoothAdapter;
    private final BluetoothDevice bluetoothDevice;
    private final CollectorDataCallback callback;

    private BluetoothSocket socket;

    public BtConnectThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice, CollectorDataCallback callback) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.bluetoothDevice = bluetoothDevice;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(Constants.BLUETOOTH_APP_ID);
        } catch (IOException e) {
            System.out.println("Error in create bluetooth socket.");
            return;
        }

        // Cancel discovery because it will slow down the connection
        bluetoothAdapter.cancelDiscovery();

        try {
            socket.connect();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e1) {
            }
        }

        manageConnectedSocket(socket);
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e1) {
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        InputStream in;
        OutputStream out;
        BufferedReader rd;

        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            rd = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = rd.readLine()) != null) {
                Collector.CollectorData data = Collector.CollectorData.parse(line);
                callback.call(data);
            }
        } catch (IOException e) {

        }
    }

}
