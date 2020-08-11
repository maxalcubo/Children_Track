package com.amilcar.laura.childrentrack.utils.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.core.app.NotificationCompat;

import com.amilcar.laura.childrentrack.R;

public class NotificationOfServices {
    public static final String STOP = "stop";
    public static final int ID_NOTIFICATION = 100;

    public static void mostrarNotificacion(Context context){
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(STOP), PendingIntent.FLAG_UPDATE_CURRENT);

        //this notification will stop the services
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name) )
                .setContentText("Parar el servicio")
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        mNotificationManager.notify(ID_NOTIFICATION, notification);
    }
    public static void quitarNotificacion(Context context){
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(ID_NOTIFICATION);
    }
}
