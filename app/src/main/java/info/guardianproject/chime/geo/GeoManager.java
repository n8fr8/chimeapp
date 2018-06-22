package info.guardianproject.chime.geo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.mapzen.android.lost.api.Geofence;
import com.mapzen.android.lost.api.GeofencingRequest;
import com.mapzen.android.lost.api.LocationListener;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.guardianproject.chime.model.Chime;

import static com.mapzen.android.lost.api.Geofence.NEVER_EXPIRE;

public class GeoManager {


    private static LostApiClient lostApiClient;
    private static Location lastLocation;

    private static HashMap<String,PendingIntent> pendingIntents = new HashMap<>();

    public static Location getLastLocation ()
    {
        return lastLocation;
    }

    public static Location getLatestLocation (Context context)
    {
        if (lostApiClient == null)
            return null;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return lastLocation;
        }

        lastLocation =   LocationServices.FusedLocationApi.getLastLocation(lostApiClient);
        return getLastLocation();
    }

    public static synchronized void initLocation(final Context context) {

        if (lostApiClient == null) {
            lostApiClient = new LostApiClient.Builder(context).addConnectionCallbacks(new LostApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected() {
                    getCurrentLocation(context);
                }

                @Override
                public void onConnectionSuspended() {

                }
            }).build();

            lostApiClient.connect();
        }

    }


    public static void getCurrentLocation(Context context) {

        if (lostApiClient == null || (!lostApiClient.isConnected()))
        {
            lostApiClient = null;
            initLocation(context);
            return;
        }

        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(5000)
                .setSmallestDisplacement(5);

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Do stuff

                lastLocation = location;
            }
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(lostApiClient, request, listener);
    }

    @SuppressLint("MissingPermission")
    public static void disconnect ()
    {
        if (lostApiClient != null && lostApiClient.isConnected()) {

            for (PendingIntent pendingIntent : pendingIntents.values())
            {

                LocationServices.GeofencingApi.removeGeofences(lostApiClient,pendingIntent);

            }

            lostApiClient.disconnect();
        }
    }

    public static void addGeofence(Context context, String requestId, double lat, double lon, float radius) {

        Geofence geofence = new Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(lat, lon, radius)
                .setExpirationDuration(NEVER_EXPIRE)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();

        Intent serviceIntent = new Intent(context.getApplicationContext(), GeoFenceIntentService.class);
        serviceIntent.putExtra("chimeid",requestId);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (pendingIntents.containsKey(requestId))
            LocationServices.GeofencingApi.removeGeofences(lostApiClient,pendingIntents.get(requestId));

        pendingIntents.put(requestId, pendingIntent);
        LocationServices.GeofencingApi.addGeofences(lostApiClient, request, pendingIntent);
    }

    public static void buildGeoFences (final Context context)
    {
        if (lostApiClient == null || (!lostApiClient.isConnected()))
            initLocation(context);

        // Need to wait a bit for the SSID to get picked up;
        // if done immediately all we'll get is null
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (lostApiClient == null || (!lostApiClient.isConnected()))
                {
                    //try again
                    buildGeoFences(context);
                }
                else {
                    List<Chime> chimes = Chime.listAll(Chime.class);

                    for (Chime chime : chimes) {
                        addGeofence(context, "chime:" + chime.getId(), chime.latitude, chime.longitude, chime.radius);
                    }
                }
            }
        }, 10000);

    }
}
