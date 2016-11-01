package com.caodanping.androidcomm.collector;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.EventListener;
import java.util.EventListenerProxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by caodanping on 2016/10/30.
 */

public class Collector implements Serializable {
    public static class CollectorData {
        private float x;
        private float y;
        private float z;

        public CollectorData(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @NonNull
        public static CollectorData parse(String line) {
            String[] xyz = line.split(",");
            return new CollectorData(Float.parseFloat(xyz[0]),
                    Float.parseFloat(xyz[1]),
                    Float.parseFloat(xyz[2]));
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }

        @Override
        public String toString() {
            return "CollectorData{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }

        public String toDisplay() {
            return    "X:" + x +
                    ", Y:" + y +
                    ", Z:" + z;
        }
    }

    public static interface CollectorEventListener extends EventListener {
        void onDataCollected(CollectorData data);
    }

    private CollectorData data;

    private List<CollectorEventListener> listeners = new CopyOnWriteArrayList<CollectorEventListener>();

    public CollectorData getData() {
        return data;
    }

    public void setData(CollectorData data) {
        this.data = data;
        fireDataCollected(data);
    }

    public void setXyz(float x, float y, float z) {
        setData(new CollectorData(x, y, z));
    }

    public void addCollectorEventListener(CollectorEventListener e) {
        listeners.add(e);
    }

    public void removeCollectorEventListener(CollectorEventListener e) {
        listeners.remove(e);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public CollectorEventListener[] getListeners() {
        return listeners.toArray(new CollectorEventListener[0]);
    }

    void fireDataCollected(CollectorData data) {
        for(CollectorEventListener listener : getListeners()) {
            listener.onDataCollected(data);
        }
    }
}
