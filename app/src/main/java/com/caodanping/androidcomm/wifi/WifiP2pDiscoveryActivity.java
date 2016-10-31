package com.caodanping.androidcomm.wifi;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.caodanping.androidcomm.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WifiP2pDiscoveryActivity extends AppCompatActivity {

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private ArrayAdapter adapter;
    private ListView listDiscoveryResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_p2p_discovery);

        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
        listDiscoveryResults = (ListView) findViewById(R.id.listDiscoveryResults);

        adapter = new ArrayAdapter(this, R.layout.activity_wifi_p2p_discovery);
        listDiscoveryResults.setAdapter(adapter);

        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("sld-3-" + new Random().nextInt(100), "_presence._tcp", new HashMap<String, String>());

        wifiP2pManager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(WifiP2pDiscoveryActivity.this, "添加服务成功.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {

            }
        });

        wifiP2pManager.setDnsSdResponseListeners(channel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                        Toast.makeText(WifiP2pDiscoveryActivity.this, "发现设备:" + instanceName, Toast.LENGTH_SHORT).show();
                    }
                },
                new WifiP2pManager.DnsSdTxtRecordListener() {
                    @Override
                    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
                        Map<String, String> values = new HashMap<String, String>();
                        String instanceName = fullDomainName.substring(0, fullDomainName.indexOf('.'));
                        String deviceName = srcDevice.deviceName;
                        String deviceAddress = srcDevice.deviceAddress;
                        String deviceIpAddress = txtRecordMap.get("ip");
                        String servicePort = txtRecordMap.get("port");

                        String text = "设备名称:" + deviceName + "\n";
                        text += "设备MAC地址:" + deviceAddress + "\n";
                        text += "设备IP地址:" + deviceIpAddress + "\n";
                        text += "服务端口:" + servicePort + "\n";

                        adapter.add(text);
                        adapter.notifyDataSetChanged();
                    }
                }
        );

        wifiP2pManager.clearServiceRequests(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                wifiP2pManager.addServiceRequest(channel, WifiP2pDnsSdServiceRequest.newInstance("_sld3._tcp"), new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        wifiP2pManager.discoverServices(channel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(WifiP2pDiscoveryActivity.this, "开始发现服务", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int reason) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(int reason) {

                    }
                });
            }

            @Override
            public void onFailure(int reason) {

            }
        });

    }
}
