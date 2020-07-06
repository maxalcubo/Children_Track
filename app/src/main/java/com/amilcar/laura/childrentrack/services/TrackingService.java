package com.amilcar.laura.childrentrack.services;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.amilcar.laura.childrentrack.R;
import com.amilcar.laura.childrentrack.interfaces.OnRecordCompletedListener;
import com.amilcar.laura.childrentrack.models.firebase.Posicion;
import com.amilcar.laura.childrentrack.utils.firebase.FileSender;
import com.amilcar.laura.childrentrack.utils.geo.GeoDistance;
import com.amilcar.laura.childrentrack.utils.geo.GeoPoint;
import com.amilcar.laura.childrentrack.utils.sound.SoundRecord;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrackingService extends Service implements LocationListener, OnRecordCompletedListener {
    public static final int ID_NOTIFICATION = 1;
    private static final String TAG = "TrackingService";
    SoundRecord soundRecord;
    GeoPoint lastGeoPoint;


    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        soundRecord = new SoundRecord(this);
        soundRecord.setOnRecordCompletedListener(this);

        buildNotification();
        requestLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void buildNotification() {
        //this notification will stop the service
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Parar el servicio de Localizacion")
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


    private void requestLocationUpdates() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,
                    60 * 1000, //updates every minute
                    0,
                    this);
            Log.d(TAG, "location started");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.getLatitude() + ", " + location.getLongitude());
        final String path = "posicionHijo";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.setValue(new Posicion(location.getLatitude(), location.getLongitude()));


        GeoPoint currentGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        if(lastGeoPoint != null){
            //find the distance between the las position and the current
            double km = GeoDistance.geoDistance(lastGeoPoint, currentGeoPoint);
            if(km < 0.01){// the boy did not move a lot, in a minute
                //record audio to find out why the child has not moved
                soundRecord.record();
            }
        }
        //current position is now the last position
        lastGeoPoint = currentGeoPoint;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onDestroy() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
        super.onDestroy();
    }

    @Override
    public void OnRecordCompleted() {
        //when the sound is recorded, it's send to the server
        new FileSender(soundRecord.getFileName()).send();
    }
}
