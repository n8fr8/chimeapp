package info.guardianproject.pinwheel.service;


import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

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

    @Override
    public void onReceive(final Context context, final Intent intent) {
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

                    createNotification(ssid, mac);
                    stopSelf();
                }
            }, 5000);
            return START_NOT_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        /**
         * Creates a notification displaying the SSID & MAC addr
         */
        private void createNotification(String ssid, String mac) {


        }
    }

    public static final int MY_BACKGROUND_JOB = 0;

    public static void scheduleJob(Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            JobScheduler js = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            JobInfo job = new JobInfo.Builder(
                    MY_BACKGROUND_JOB,
                    new ComponentName(context, WifiJobService.class))
                    .setPersisted(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setRequiresCharging(false)
                    .build();
            js.schedule(job);
        }
    }
}