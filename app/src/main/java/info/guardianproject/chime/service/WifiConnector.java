package info.guardianproject.chime.service;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import static android.content.Context.WIFI_SERVICE;

public class WifiConnector {

    public static void connect (Context context, String ssid, String bssid, String key)
    {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);

        if (!TextUtils.isEmpty(bssid))
            wifiConfig.BSSID = String.format("\"%s\"", bssid);

        if (!TextUtils.isEmpty(key))
            wifiConfig.preSharedKey = String.format("\"%s\"", key);

        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(WIFI_SERVICE);

        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }
}
