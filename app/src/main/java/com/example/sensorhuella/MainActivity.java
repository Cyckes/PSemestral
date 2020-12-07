package com.example.sensorhuella;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    TextView msg_txt;
    Button login_btn;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msg_txt = (TextView) findViewById(R.id.txt_msg);
        login_btn = (Button) findViewById(R.id.login_btn);

        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate())
        {
            case BiometricManager.BIOMETRIC_SUCCESS:
                msg_txt.setText("Puede usar el sensor de huellas para iniciar sesión");
                msg_txt.setTextColor(Color.parseColor("#FAFAFA"));
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                msg_txt.setText("El dispositivo no dispone de un sensor de huellas");
                login_btn.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                msg_txt.setText("El sensor de huellas no esta disponible");
                login_btn.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                msg_txt.setText("No se ha configurado nonguna huella, verificar las opciones del dispositivo");
                login_btn.setVisibility(View.GONE);
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


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }
}