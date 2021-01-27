package com.example.projektsklep;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AppCompatDelegate;

import java.io.Serializable;

public class LightSensor implements SensorEventListener, Serializable {
    private SensorManager sensorManager;
    private Sensor sensorLight;

    public LightSensor(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        sensorLight = this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float currentValue = sensorEvent.values[0];

        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                if(currentValue < 100) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int value) {
    }

    public void onStart() {
        if (sensorLight != null) {
            sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onPause() {
        sensorManager.unregisterListener(this);
    }
}
