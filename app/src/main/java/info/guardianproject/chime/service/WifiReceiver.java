package info.guardianproject.chime.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;
import java.util.List;

import info.guardianproject.chime.R;
import info.guardianproject.chime.model.Chime;
import info.guardianproject.chime.model.ChimeEvent;

/**
 * Receives wifi changes and creates a notification when wifi connects to a network,
 * displaying the SSID and MAC address.
 *
 * Put the following in your manifest
 *
 * <receiver android:name=".WifiReceiver" android:exported="false" >
 *   <intent-filter>
 *     <action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
 *   </intent-filter>
 * </receiver>
 * <service android:name=".WifiReceiver$WifiActiveService" android:exported="false" />
 *
 * To activate logging use: adb shell setprop log.tag.WifiReceiver VERBOSE
 */
public class WifiReceiver extends BroadcastReceiver {

    private final static String TAG = WifiReceiver.class.getSimpleName();

    private static final String ANDROID_CHANNEL_ID = "info.guardianproject.chime.service.Channel";
    private static final int NOTIFICATION_ID = 555;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            boolean connected = info.isConnected();

            context.startService(new Intent(context, WifiActiveService.class));
            //call your method
        }

        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())
                && WifiManager.WIFI_STATE_ENABLED == wifiState) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Wifi is now enabled");
            }
            context.startService(new Intent(context, WifiActiveService.class));
        }
    }

    /**
     * Getting the network info and displaying the notification is handled in a service
     * as we need to delay fetching the SSID name. If this is done when the receiver is
     * called, the name isn't yet available and you'll get null.
     *
     * As the broadcast receiver is flagged for termination as soon as onReceive() completes,
     * there's a chance that it will be killed before the handler has had time to finish. Placing
     * it in a service lets us control the lifetime.
     */
    public static class WifiActiveService extends Service {

        @Override
        public void onCreate() {
            super.onCreate();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            CharSequence channelName = "Some Channel";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(ANDROID_CHANNEL_ID, channelName, importance);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            // Need to wait a bit for the SSID to get picked up;
            // if done immediately all we'll get is null
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WifiInfo info = wifiManager.getConnectionInfo();

                    String mac = info.getMacAddress();
                    String ssid = info.getSSID();
                    String bssid = info.getBSSID();

                    createNotification(ssid, mac,bssid);
                    stopSelf();
                }
            }, 5000);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("SmartTracker Running")
                        .setAutoCancel(true);
                Notification notification = builder.build();
                startForeground(NOTIFICATION_ID, notification);
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("SmartTracker is Running...")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
                Notification notification = builder.build();
                startForeground(NOTIFICATION_ID, notification);
            }

            return START_NOT_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        /**
         * Creates a notification displaying the SSID & MAC addr
         */
        private void createNotification(String ssid, String mac, String bssid) {

            String[] args = {ssid};
            List<Chime> chimes = Chime.find(Chime.class,"ssid = ?",args);
            for (Chime chime : chimes)
            {
                if (!TextUtils.isEmpty(chime.bssid))
                {
                    if (!bssid.equals(chime.bssid))
                        continue;
                }

                chime.isNearby = true;
                chime.lastSeen = new Date();
                chime.save();

                Date now = new Date();

                //clear previous chime events for this chime
                String[] eventArgs = {chime.getId()+""};
                List<ChimeEvent> events = ChimeEvent.find(ChimeEvent.class, "chime_id = ?",eventArgs);
                for (ChimeEvent cevent : events) {

                    if (now.getTime() - cevent.happened.getTime() < EVENT_MIN_INTERVAL)
                        return;

                    cevent.delete();
                }

                ChimeEvent event = new ChimeEvent();
                event.chimeId = chime.getId()+"";
                event.type = ChimeEvent.TYPE_HEARD_KNOWN_CHIME;
                event.happened = now;
                event.description = chime.name;
                event.save();

                new ChimeNotifier(this).notify(event, chime);
            }


        }
    }

    private final static long EVENT_MIN_INTERVAL = 60 * 60000; //1 hours

}