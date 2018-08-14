package info.guardianproject.chime.model;

import android.net.Uri;
import android.text.TextUtils;

import com.orm.SugarRecord;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Set;

public class Chime extends SugarRecord {

    public Chime () {}

    public String name;
    public String chimeId;

    public String ssid;
    public String bssid;
    public String key;

    public double latitude;
    public double longitude;
    public float radius = 10;//default

    public String serviceUri;
    public String servicePackage;
    public String serviceType;

    public Date begin;
    public Date end;

    public Date lastSeen;
    public boolean isNearby = false;

    /**
     *
     wcap:lat=135.2&lon=35.4&begin=0000-00-00T03:00:00Z&end=0000-00-00T05:59:59Z&ssid=librarybox.lan&bssid=24:a4:3c:9e:d2:84&serviceType=fdroid&serviceUri=http://192.168.1.1/fdroid/repo?fingerprint=B7C2EEFD8DAC7806AF67DFCD92EB18126BC08312A7F2D6F3862E46013C7A6135
     * @return
     */
    public String getUri ()
    {
        StringBuilder result = new StringBuilder();

        result.append(SCHEME).append("://");

        if (!TextUtils.isEmpty(name)) {
            try {
                result.append(URLEncoder.encode(name, "UTF-8")).append("?");
                result.append("name=").append(URLEncoder.encode(name, "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        result.append("lat=").append(latitude).append("&");
        result.append("lon=").append(longitude).append("&");

        if (!TextUtils.isEmpty(ssid)) {
            try {
                result.append("ssid=").append(URLEncoder.encode(ssid,"UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(bssid))
            result.append("bssid=").append(bssid).append("&");

        if (!TextUtils.isEmpty(serviceType))
            result.append("type=").append(serviceType).append("&");

        if (!TextUtils.isEmpty(serviceUri)) {
            try {
                result.append("service=").append(URLEncoder.encode(serviceUri,"UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(servicePackage))
            result.append("pkg=").append(servicePackage).append("&");


        return result.toString();
    }

    public static Chime parseUri (String uriString)
    {
        Chime chime = null;

        Uri uri = Uri.parse(uriString);
        String protocol = uri.getScheme();

        if (protocol != null && protocol.contains(SCHEME)) {

            chime = new Chime();

            chime.name = uri.getQueryParameter("name");
            chime.ssid = uri.getQueryParameter("ssid");
            chime.bssid = uri.getQueryParameter("bssid");

            String latitude = uri.getQueryParameter("lat");
            if (latitude != null)
                chime.latitude = Double.parseDouble(latitude);

            String longitude = uri.getQueryParameter("lon");
            if (longitude != null)
                chime.longitude = Double.parseDouble(longitude);

            chime.serviceType = uri.getQueryParameter("type");
            chime.serviceUri = uri.getQueryParameter("service");
            chime.servicePackage = uri.getQueryParameter("pkg");


        }

        return chime;
    }

    public final static String SCHEME = "wcap";
}
