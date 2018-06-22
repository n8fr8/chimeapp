package info.guardianproject.chime.model;

import com.orm.SugarRecord;

import java.util.Date;

public class Chime extends SugarRecord {

    public Chime () {}

    public String name;
    public String chimeId;

    public String ssid;
    public String bssid;
    public String key;

    public double latitude;
    public double longitude;
    public float radius;

    public String serviceUri;
    public String servicePackage;
    public String serviceType;

    public Date begin;
    public Date end;

    public Date lastSeen;
}
