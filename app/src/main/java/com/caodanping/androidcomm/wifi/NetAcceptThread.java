package com.caodanping.androidcomm.wifi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.caodanping.androidcomm.Constants;
import com.caodanping.androidcomm.collector.Collector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by caodanping on 2016/10/27.
 */

public class NetAcceptThread extends Thread {
    private ServerSocket serverSocket;
    private Collector collector;

    public NetAcceptThread(Collector collector) {
        this.collector = collector;
        try {
            this.serverSocket = new ServerSocket(20001);
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(20001);
        } catch (IOException e) {
            System.out.println("listening error");
            return;
        }

        Socket socket = null;
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                break;
            }

            if (socket != null) {
                manageConnectedSocket(socket);
                // 只接收一次连接
                cancel();
                break;
            }
        }
    }

    public void cancel() {
        if (serverSocket == null) {
            return;
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }

    private void manageConnectedSocket(Socket socket) {

    }
}
