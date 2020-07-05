package com.amilcar.laura.childrentrack;

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

public class MovimientoServicio extends Service implements SensorEventListener {

    public static final boolean SERVERTRACE = false;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private FallDetectAlgo fallDetectAlgo;
    private boolean flag_fall = false;
    private boolean flag_buffer_ready = false;
    private boolean f_send_msg = true;

    public MovimientoServicio() {
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void buildNotification() {
        String stop = "stop2";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 2, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

// Create the persistent notification//
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Parar el servicio Movimiento")

//Make this notification ongoing so it can’t be dismissed by the user//

                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
        startForeground(2, builder.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (SERVERTRACE) enableStrictMode(); // for socket handling in mainloop

        // start sample and analyze
        fallDetectAlgo = new FallDetectAlgo();
        fallDetectAlgo.setDaemon(true);
        fallDetectAlgo.start();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            Toast.makeText(this, "No Accelerometer Found!!",
                    Toast.LENGTH_LONG).show();
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

        // display if buffer is ready
        flag_buffer_ready = fallDetectAlgo.get_buffer_ready();
        if (flag_buffer_ready) {
            //button.setText("BUFFER OK");
        } else {
            //button.setText("NO BUFFER");
        }
        // store values in buffer and visualize fall
        flag_fall = fallDetectAlgo.set_data(event); // event has values minus gravity
        if (flag_fall) {
            if (f_send_msg) {
                f_send_msg = false;
                playTone();
                //send_sms();
            }
        } else {
            f_send_msg = true;
        }

    }



    public void enableStrictMode() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    public void playTone() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }
}
