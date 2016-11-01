package com.caodanping.androidcomm.wifi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.caodanping.androidcomm.Constants;
import com.caodanping.androidcomm.collector.Collector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by caodanping on 2016/10/27.
 */

public class NetAcceptThread extends Thread {
    private Handler handler;
    private ServerSocket serverSocket;
    private Collector collector;
    private int port;
    private NetConnectedThread netConnectedThread;


    public NetAcceptThread(Collector collector, Handler handler) {
        this.collector = collector;
        this.handler = handler;
        try {
            this.serverSocket = new ServerSocket(20001);
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        try {
            port = 20001;
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("listening error");
            e.printStackTrace();
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
        if (netConnectedThread != null) {
            netConnectedThread.cancel();
            netConnectedThread = null;
        }

        netConnectedThread = new NetConnectedThread(collector, socket, handler);
        netConnectedThread.start();
    }

    public int getPort() {
        return port;
    }
}
