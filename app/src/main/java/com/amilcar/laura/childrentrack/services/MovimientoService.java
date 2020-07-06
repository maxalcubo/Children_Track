package com.amilcar.laura.childrentrack.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.StrictMode;
import android.widget.Toast;

import com.amilcar.laura.childrentrack.utils.fallDetect.FallDetectAlgorithm;
import com.amilcar.laura.childrentrack.R;

public class MovimientoService extends Service implements SensorEventListener {
    public static final int ID_NOTIFICATION = 2;
    private static final String TAG = "MovimientoService";
    private SensorManager sensorManager;
    private FallDetectAlgorithm fallDetectAlgorithm;
    private boolean sendNotificationFall = true;

    public MovimientoService() {
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void buildNotification() {
        //this notification will stop the service
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name) )
                .setContentText("Parar el servicio Movimiento")
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
        startForeground(ID_NOTIFICATION, builder.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // start sample and analyze
        fallDetectAlgorithm = new FallDetectAlgorithm();
        fallDetectAlgorithm.setDaemon(true);
        fallDetectAlgorithm.start();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "No Accelerometer Found!!", Toast.LENGTH_LONG).show();
        }

        buildNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // store values in buffer and visualize fall
        boolean hasFallenFlag = fallDetectAlgorithm.set_data(event);
        if (hasFallenFlag) {
            if (sendNotificationFall) {
                sendNotificationFall = false;
                playTone(); //send notification about posible accident (to the father)
            }
        } else {
            sendNotificationFall = true;
        }
    }


    public void playTone() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }
}
