package com.caodanping.androidcomm.wifi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.caodanping.androidcomm.Constants;
import com.caodanping.androidcomm.collector.Collector;
import com.caodanping.androidcomm.collector.CollectorDataCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by caodanping on 2016/10/27.
 */

public class NetConnectThread extends Thread {
    private Socket socket;
    private CollectorDataCallback callback;

    public NetConnectThread(CollectorDataCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            socket = new Socket("192.168.0.1", 20000);
        } catch (IOException e) {
            System.out.println("Error in create bluetooth socket.");
            return;
        }

        manageConnectedSocket(socket);
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e1) {
        }
    }

    private void manageConnectedSocket(Socket socket) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                Collector.CollectorData data = Collector.CollectorData.parse(line);
                callback.call(data);
            }
        } catch (IOException e) {

        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cancel();
        }
    }
}
