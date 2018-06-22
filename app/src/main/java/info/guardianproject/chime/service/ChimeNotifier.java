package info.guardianproject.chime.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import info.guardianproject.chime.MainActivity;
import info.guardianproject.chime.R;
import info.guardianproject.chime.model.Chime;
import info.guardianproject.chime.model.ChimeEvent;

public class ChimeNotifier {

    private final static String CHANNEL_ID = "chime-default";

    private int notificationId = 1;

    private Context context;

    public ChimeNotifier (Context context)
    {
        this.context = context;
        createNotificationChannel(context);
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void notify (ChimeEvent chimeEvent, Chime chime)
    {
        String desc = "";

        if (chimeEvent.type == ChimeEvent.TYPE_HEARD_KNOWN_CHIME)
        {
           desc = context.getString(R.string.action_heard_known_chime);
        }
        else if (chimeEvent.type == ChimeEvent.TYPE_FOUND_NEW_CHIME)
        {
            desc = context.getString(R.string.action_heard_new_chime);
        }
        else if (chimeEvent.type == ChimeEvent.TYPE_ADDED_CHIME)
        {
            desc = context.getString(R.string.action_added_chime);
        }


        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_windchime)
                .setContentTitle(chime.name)
                .setContentText(desc)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId++, mBuilder.build());


    }

    public void clearAll ()
    {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancelAll();
    }

}
