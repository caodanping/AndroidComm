package com.caodanping.androidcomm.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.caodanping.androidcomm.Constants;
import com.caodanping.androidcomm.collector.Collector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by caodanping on 2016/10/27.
 */

public class BtCollectorConnectedThread extends Thread implements Collector.CollectorEventListener {
    public static final int BUFFER_SIZE = 1024;
    private final BluetoothSocket socket;
    private final InputStream in;
    private final OutputStream out;
    private final Handler handler;

    private Collector collector;

    public BtCollectorConnectedThread(Collector collector, BluetoothSocket socket, Handler handler) {
        this.collector = collector;
        this.socket = socket;
        this.handler = handler;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        in = tmpIn;
        out = tmpOut;

        collector.addCollectorEventListener(this);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                len = in.read(buffer);
                // Send the obtained bytes to the UI activity
//                handler.obtainMessage(Constants.MESSAGE_READ, len, -1, buffer).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            out.write(bytes);
        } catch (IOException e) { }
    }

    public void cancel() {
        collector.removeCollectorEventListener(this);
        try {
            socket.close();
        } catch (IOException e) { }
    }

    @Override
    public void onDataCollected(Collector.CollectorData data) {
        String text = data.getX() + "," + data.getY() + "," + data.getZ() + "\n";
        write(text.getBytes());
    }
}
