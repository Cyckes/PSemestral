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

import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executor;

import static android.hardware.Sensor.TYPE_LIGHT;

public class MainActivity extends AppCompatActivity {

    Button btnSesion;
    ImageButton imgbtnHuella;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEvtListener;
    private float maxVal;
    private TextInputEditText etusuaario, etcontraseña;
    private String[] nombres = {"Dayan","Manuel","Byron","Alison","Kevin","Roberto","Rolando","Susan","Elena","Massiel"};
    private String[] contra = {"day01","man02","byr03","ali04","kev05","rob06","rol07","sus08","ele09","mas10"};

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etusuaario = (TextInputEditText) findViewById(R.id.etUsuario);
        etcontraseña = (TextInputEditText) findViewById(R.id.etContraseña);
        btnSesion = (Button) findViewById(R.id.btnSesion);
        imgbtnHuella = (ImageButton) findViewById(R.id.imgbtnHuella);

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
                //valor de luz detectado por el sensor
                float val = event.values[0];
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                if (0.0 <=val && val < 30.0){
                    //conversion a valores en base a 255 para el brillo de la pantalla
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


        //Creación del BIOMETRICMANAGER y comprobar si se puede utilizar el sensor de huella dactilar
        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate())
        {
            //Usuario puede utilizar el sensor de huella
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(getApplicationContext(),"Puede usar el sensor de huellas para iniciar sesión",Toast.LENGTH_SHORT).show();
                break;
            //El dispositivo no tiene el sensor de huellas
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getApplicationContext(),"El dispositivo no dispone de un sensor de huellas",Toast.LENGTH_SHORT).show();
                break;
            //EL sensor no esta en servicio
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getApplicationContext(),"El sensor de huellas no esta disponible",Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
            //El usuario no ha registrado ninguna huella
                Toast.makeText(getApplicationContext(),"No se ha configurado nonguna huella, verificar las opciones del dispositivo",Toast.LENGTH_SHORT).show();
                break;
        }


        //Comprobar si se puede utilizar el sensor de huellas
        //Primero se crea el EXECUTOR
        Executor executor = ContextCompat.getMainExecutor(this);


        //Ahora la respuesta de la petición, si se puede utilizar el sensor o no
        final BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            //Método cuando ocurre un error en la autenticación
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            //Método cuando la autenticación fue exitosa
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),"Inicio de sesión correcto !",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, Menu.class);
                startActivity(i);
            }

            @Override
            //Método cuando la autenticación fallo
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });


        //Creando el diálogo para el sensor de huella
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Iniciar Sesión")
                .setDescription("Utilice su huella dactilar para iniciar sesión")
                .setNegativeButtonText("Cancelar")
                .build();


        //Evento de dar CLIC en la imagen de la HUELLA
        imgbtnHuella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);

            }
        });


        //Iniciar sesión
        btnSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = etusuaario.getText().toString();
                String pass = etcontraseña.getText().toString();
                boolean existe = false;
                for(int i = 0; i<10; i++)
                {
                    if(nombres[i].equals(nombre) && contra[i].equals(pass))
                    {
                        existe = true;
                    }
                }
                if(existe)
                {
                    etcontraseña.setText("");
                    etusuaario.setText("");
                    existe = false;
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"ERROR\nNombre de usuario o contraseña incorrectos",Toast.LENGTH_LONG).show();
                }
            }
        });

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