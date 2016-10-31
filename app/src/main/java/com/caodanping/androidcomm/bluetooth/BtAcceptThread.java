package com.caodanping.androidcomm.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.caodanping.androidcomm.Constants;
import com.caodanping.androidcomm.collector.Collector;

import java.io.IOException;

/**
 * Created by caodanping on 2016/10/27.
 */
public class BtAcceptThread extends Thread {
    private Collector collector;
    private Handler handler;
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket bluetoothServerSocket;

    private BtCollectorConnectedThread btCollectorConnectedThread;

    public BtAcceptThread(Collector collector, BluetoothAdapter bluetoothAdapter, Handler handler) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.collector = collector;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(Constants.DEVICE_NO, Constants.BLUETOOTH_APP_ID);
        } catch (IOException e) {
            System.out.println("listening error");
            return;
        }

        BluetoothSocket socket = null;
        while (true) {
            try {
                socket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                break;
            }

            if (socket != null) {
                manageConnectedSocket(socket);
            }
        }
    }

    public void cancel() {
        if (bluetoothServerSocket == null) {
            return;
        }
        btCollectorConnectedThread.cancel();
        try {
            bluetoothServerSocket.close();
        } catch (IOException e) {
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        btCollectorConnectedThread = new BtCollectorConnectedThread(collector, socket, handler);
    }
}
