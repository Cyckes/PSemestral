package com.example.sensorhuella;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import static android.hardware.Sensor.TYPE_LIGHT;

public class Menu extends AppCompatActivity {

    Button btnBoton3, btnexit;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEvtListener;
    private View root;
    private float maxVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(TYPE_LIGHT);

        if (lightSensor == null) {
            Toast.makeText(this, ("El dispositivo no tiene sensor de iluminacion"), Toast.LENGTH_LONG).show();
            finish();
        }
        maxVal = lightSensor.getMaximumRange();

        lightEvtListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float val = event.values[0];
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                if (0.0 <=val && val < 30.0){
                    lp.screenBrightness = (int) (255f * val / (maxVal/7.5));
                    getWindow().setAttributes(lp);
                }
                if (val > 29.0 && val < 50.0){
                    lp.screenBrightness = (int) (255f * val / (maxVal/6.5));
                    getWindow().setAttributes(lp);
                }
                if (val > 49.0 && val < 100.0){
                    lp.screenBrightness = (int) (255f * val / (maxVal/6));
                    getWindow().setAttributes(lp);
                }
                if (val > 99.0 && val < 1000.0){
                    lp.screenBrightness = (int) (255f * val / (maxVal/5));
                    getWindow().setAttributes(lp);
                }
                if (val > 999.0 && val < 5000.0){
                    lp.screenBrightness = (int) (255f * val / (maxVal/3));
                    getWindow().setAttributes(lp);
                }
                if (val > 4999.0){
                    lp.screenBrightness = (int) (255f * val / (maxVal));
                    getWindow().setAttributes(lp);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

                btnBoton3 = findViewById(R.id.btnSesion3);
        btnexit = findViewById(R.id.btnSesion2);

        btnBoton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, SensorGPS.class);
                startActivity(i);
            }
        });

        btnexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, MainActivity.class);
                startActivity(i);
                Toast.makeText( getApplicationContext(),"Sesion cerrada correctamente", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEvtListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightEvtListener);
    }
}

