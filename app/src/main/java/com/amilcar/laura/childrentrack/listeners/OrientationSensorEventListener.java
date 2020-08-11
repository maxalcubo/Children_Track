package com.amilcar.laura.childrentrack.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.amilcar.laura.childrentrack.interfaces.OnOrientationChangeListener;
import com.amilcar.laura.childrentrack.utils.orientation.Orientation;

public class OrientationSensorEventListener implements SensorEventListener {
    private static final String TAG = "OrientationSensorEventListener";
    private float[] accelValues;
    private float[] magValues;
    private float[] orientationVals = new float[3];
    private boolean notifyTop = true, notifyDown = true;
    private OnOrientationChangeListener onOrientationChangeListener;

    public void setOnOrientationChangeListener(OnOrientationChangeListener onOrientationChangeListener){
        this.onOrientationChangeListener = onOrientationChangeListener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER: {
                accelValues = sensorEvent.values.clone();
                break;
            }
            case Sensor.TYPE_MAGNETIC_FIELD: {
                magValues = sensorEvent.values.clone();
                break;
            }
        }

        float[] rotationMatrix = new float[16];
        if (magValues != null && accelValues != null) {

            SensorManager.getRotationMatrix(rotationMatrix, null, accelValues, magValues);

            SensorManager.getOrientation(rotationMatrix, orientationVals);

            orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
            orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
            orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);

            Log.d(TAG, "z:"+orientationVals[0] + ", x:" + orientationVals[1] + ", y:" + orientationVals[2]);
        }

        orientationVals[2] = accelValues[2]; // in case of there is not magnetic sensor

        if(orientationVals[2] > 9){
            //screen is top
            if(notifyTop){//notify just one time
                onOrientationChangeListener.onOrientationChanged(Orientation.TOP);
                notifyTop = false;
            }
        }else if(orientationVals[2] < -9){
            //screen is down
            if(notifyDown){//notify just one time
                onOrientationChangeListener.onOrientationChanged(Orientation.DOWN);
                notifyDown = false;
            }
        }else{
            //screen is on its side
            notifyTop = true;
            notifyDown = true;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "" + sensor.getName());

    }
}