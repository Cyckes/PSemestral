package com.example.sensorhuella;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

import static android.hardware.Sensor.TYPE_LIGHT;

public class MainActivity extends AppCompatActivity {

    Button btnSesion;
    ImageButton imgbtnHuella;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEvtListener;
    private View root;
    private float maxVal;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //msg_txt = (TextView) findViewById(R.id.txt_msg);
        //login_btn = (Button) findViewById(R.id.login_btn);
        imgbtnHuella = (ImageButton) findViewById(R.id.imgbtnHuella);

        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate())
        {
            case BiometricManager.BIOMETRIC_SUCCESS:
                //msg_txt.setText("Puede usar el sensor de huellas para iniciar sesión");
                //msg_txt.setTextColor(Color.parseColor("#FAFAFA"));
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                //msg_txt.setText("El dispositivo no dispone de un sensor de huellas");
                //login_btn.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"El dispositivo no dispone de un sensor de huellas",Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                //msg_txt.setText("El sensor de huellas no esta disponible");
                //login_btn.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"El sensor de huellas no esta disponible",Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                //msg_txt.setText("No se ha configurado nonguna huella, verificar las opciones del dispositivo");
                //login_btn.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"No se ha configurado nonguna huella, verificar las opciones del dispositivo",Toast.LENGTH_SHORT).show();
                break;
        }


        Executor executor = ContextCompat.getMainExecutor(this);

        final BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),"Inicio de sesión correcto !",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });


        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Iniciar Sesión")
                .setDescription("Utilice su huella dactilar para iniciar sesión")
                .setNegativeButtonText("Cancelar")
                .build();


        //login_btn.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View view) {
              //  biometricPrompt.authenticate(promptInfo);
            //}
        //});

        imgbtnHuella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
                Intent i = new Intent(MainActivity.this, SensorGPS.class);
                startActivity(i);
            }
        });

        root = findViewById(R.id.root);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(TYPE_LIGHT);

        if (lightSensor == null) {
            Toast.makeText(this, ("El dispositivo no tiene sensor de iluminacion"), Toast.LENGTH_LONG);
            finish();
        }

        maxVal = lightSensor.getMaximumRange();

        lightEvtListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float val = event.values[0];
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                if (val < 30){
                    lp.screenBrightness = (int) (255f * val / (maxVal/7));
                    getWindow().setAttributes(lp);
                }
                if (val > 29 && val < 5000){
                    lp.screenBrightness = (int) (255f * val / (maxVal/5));
                    getWindow().setAttributes(lp);
                }
                if (val > 4999){
                    lp.screenBrightness = (int) (255f * val / (maxVal));
                    getWindow().setAttributes(lp);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEvtListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightEvtListener);
    }
}