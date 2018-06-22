package info.guardianproject.pinwheel;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.mapzen.android.lost.api.Geofence;
import com.mapzen.android.lost.api.GeofencingRequest;
import com.mapzen.android.lost.api.LocationListener;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import static com.mapzen.android.lost.api.Geofence.NEVER_EXPIRE;

public class GeoFenceIntentService extends IntentService {

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public GeoFenceIntentService() {
        super("GeoFenceIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    LostApiClient lostApiClient;

    public void initLocation() {
        lostApiClient = new LostApiClient.Builder(this).addConnectionCallbacks(new LostApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected() {
                locationTest();
                geofenceTest();
            }

            @Override
            public void onConnectionSuspended() {

            }
        }).build();
        lostApiClient.connect();

    }

    private void locationTest() {

        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(5000)
                .setSmallestDisplacement(10);

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Do stuff
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(lostApiClient, request, listener);
    }

    private void geofenceTest() {
        String requestId = "test1";
        double lat = 30.0;
        double lon = 31.0;
        float rad = 10;

        Geofence geofence = new Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(lat, lon, rad)
                .setExpirationDuration(NEVER_EXPIRE)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();

        Intent serviceIntent = new Intent(getApplicationContext(), GeoFenceIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.GeofencingApi.addGeofences(lostApiClient, request, pendingIntent);
    }
}
