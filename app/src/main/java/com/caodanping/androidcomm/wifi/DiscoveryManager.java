package com.caodanping.androidcomm.wifi;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by caodanping on 2016/10/27.
 */

public class DiscoveryManager {
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private Context context;

    public DiscoveryManager(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, Context context) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.context = context;
    }

    public void register() {
        Map<String, String> textMap = new HashMap<>();
        textMap.put("ip", "192.168.11.1");
        textMap.put("port", "20001");

        // instanceName：服务的实例名称
        // serviceType：服务的类型，这个好像可以自定义，反正我用的是自己定义的名称
        // txtMap：服务携带的信息，在被发现的时候可以由对方获取，很有用！！！
        WifiP2pDnsSdServiceInfo servInfo = WifiP2pDnsSdServiceInfo.newInstance("sld-3-0001",
                "_sld3._tcp",    // 服务类型:_协议名._传输层协议
                textMap);
        // 4.1以上
        wifiP2pManager.addLocalService(channel, servInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Log.e("P2pServiceDiscovery", "Add Local Service failed.");
            }
        });
    }

    public void discoveryServices() {
        wifiP2pManager.setDnsSdResponseListeners(channel, null, new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
                Map<String, String> values = new HashMap<String, String>();
                String instanceName = fullDomainName.substring(0, fullDomainName.indexOf('.'));
                String deviceName = srcDevice.deviceName;
                String deviceAddress = srcDevice.deviceAddress;
                String deviceIpAddress = txtRecordMap.get("ip");
                String servicePort = txtRecordMap.get("port");
            }
        });
        wifiP2pManager.addServiceRequest(channel, WifiP2pDnsSdServiceRequest.newInstance("_sld3._tcp"), null);
        wifiP2pManager.discoverServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }
}
