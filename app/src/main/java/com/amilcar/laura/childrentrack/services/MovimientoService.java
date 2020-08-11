package com.amilcar.laura.childrentrack.services;

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
import android.widget.Toast;

import com.amilcar.laura.childrentrack.utils.account.CuentaManager;
import com.amilcar.laura.childrentrack.models.firebase.Acontecimiento;
import com.amilcar.laura.childrentrack.utils.notifications.NotificationOfServices;
import com.amilcar.laura.childrentrack.utils.fallDetect.FallDetectAlgorithm;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MovimientoService extends Service implements SensorEventListener {
    public static final int ID_NOTIFICATION = 2;
    private static final String TAG = "MovimientoService";
    private SensorManager sensorManager;
    private FallDetectAlgorithm fallDetectAlgorithm;
    private boolean sendNotificationFall = true;

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    public MovimientoService() {
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(stopReceiver);
            NotificationOfServices.quitarNotificacion(MovimientoService.this);
            stopSelf();
        }
    };

    private void buildNotification() {
        registerReceiver(stopReceiver, new IntentFilter(NotificationOfServices.STOP));
        NotificationOfServices.mostrarNotificacion(this);
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
        boolean hasFallenFlag = fallDetectAlgorithm.set_data(obtenerAceleracionLinear(event));
        if (hasFallenFlag) {
            if (sendNotificationFall) {
                sendNotificationFall = false;
                playTone(); //send notification about posible accident (to the father)
                mandarDatosAlServidor();
            }
        } else {
            sendNotificationFall = true;
        }
    }
    private float[] obtenerAceleracionLinear(SensorEvent event){
        final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        return linear_acceleration;
    }

    private void mandarDatosAlServidor(){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference(CuentaManager.getInstance().obtenerIdHijo(this) + "/acontecimientos").push();
        df.setValue(new Acontecimiento("Posible caida del niño", "Puede ser que su hijo haya sufrido una caida (o un choque)", System.currentTimeMillis(), null));

    }

    public void playTone() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }
}
