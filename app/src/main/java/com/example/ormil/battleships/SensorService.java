package com.example.ormil.battleships;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


/**
 * Created by ormil on 10/01/2018.
 */

public class SensorService extends Service implements SensorEventListener {

    private final int SENSOR_SAMPLE_DELAY = 1000000;
    private final float THRESHOLD = 3f;
    private final int UPDATE_DELAY_IN_MS = 800;

    private long lastSampleTime;
    private int alertTicks = 0;
    private float[] initAxis;

    private final IBinder mBinder = new SensorBinder();
    private MovementAlertListener mListener;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        mSensorManager.registerListener(this, mAccelerometer, SENSOR_SAMPLE_DELAY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Sensor", "Bind!");
        return mBinder;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        boolean toPlayTurn = false;
        if(initAxis == null)
            initAxis = sensorEvent.values.clone();
        long time  = System.currentTimeMillis();
        if (time > lastSampleTime + UPDATE_DELAY_IN_MS) {
            lastSampleTime = time;
            if (isOverExtended(sensorEvent.values[2], initAxis[2])) {
                alertTicks++;
                if(alertTicks == 2){
                    alertTicks = 0;
                    toPlayTurn = true;
                }
                mListener.movementAlert(toPlayTurn);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    boolean isOverExtended(float sensorValue, float initValue){
        float difference = Math.abs(sensorValue - initValue);
        if(difference > THRESHOLD)
            return true;
        return false;
    }

    public class SensorBinder extends Binder {
        void registerListener(MovementAlertListener listener){
            mListener = listener;
        }
    }

    public interface MovementAlertListener {
        void movementAlert(boolean toPlayTurn);
    }
}
