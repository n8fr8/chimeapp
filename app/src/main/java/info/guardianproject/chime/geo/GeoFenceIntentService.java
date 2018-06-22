package info.guardianproject.chime.geo;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.mapzen.android.lost.api.Geofence;
import com.mapzen.android.lost.api.GeofencingRequest;
import com.mapzen.android.lost.api.LocationListener;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import java.util.Date;

import info.guardianproject.chime.model.Chime;
import info.guardianproject.chime.model.ChimeEvent;
import info.guardianproject.chime.service.ChimeNotifier;

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

        String chimeId = intent.getStringExtra("chimeid");

        if (!TextUtils.isEmpty(chimeId))
        {
            String[] parts = chimeId.split(":");
            long cid = Long.parseLong(parts[1]);

            Chime chime = Chime.findById(Chime.class,cid);

            if (chime != null)
            {
                chime.isNearby = true;
                chime.lastSeen = new Date();

                ChimeEvent event = new ChimeEvent();
                event.chimeId = chime.getId()+"";
                event.type = ChimeEvent.TYPE_HEARD_KNOWN_CHIME;
                event.happened = new Date();
                event.description = chime.name;
                event.save();

                new ChimeNotifier(this).notify(event, chime);

            }
        }
    }



}
