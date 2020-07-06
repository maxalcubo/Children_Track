package com.amilcar.laura.childrentrack.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.amilcar.laura.childrentrack.listeners.OrientationSensorEventListener;
import com.amilcar.laura.childrentrack.R;
import com.amilcar.laura.childrentrack.interfaces.OnOrientationChangeListener;
import com.amilcar.laura.childrentrack.utils.orientation.Orientation;

public class OrientationService extends Service implements OnOrientationChangeListener {

    public static String TAG = "OrientationService";
    public static final int ID_NOTIFICATION = 3;


    private SensorManager sensorManager;
    private Sensor sensor;
    OrientationSensorEventListener mySensorEventListener;

    public OrientationService() {
    }


    private void buildNotification() {
        //this notification will stop the service
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Parar el servicio de Orientacion")
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
        startForeground(ID_NOTIFICATION, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensorEventListener=new OrientationSensorEventListener();
        mySensorEventListener.setOnOrientationChangeListener(this);

        Sensor sensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(
                mySensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        Sensor sensor1=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(
                mySensorEventListener,
                sensor1,
                SensorManager.SENSOR_DELAY_NORMAL);

        buildNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(mySensorEventListener);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        switch (orientation){
            case Orientation.TOP:
                Log.i(TAG, "screen is top now");
                break;
            case Orientation.DOWN:
                Log.i(TAG, "screen is down now");
                break;
        }
    }
}
