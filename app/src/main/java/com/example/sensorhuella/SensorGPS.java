package com.example.sensorhuella;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.hardware.Sensor.TYPE_LIGHT;

public class SensorGPS extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    double latitud=0.0;
    double longitud=0.0;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEvtListener;
    private View root;
    private float maxVal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_g_p_s);

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ubicacionactual();

    }

    private void agregar_marcador(double latitud, double longitud){
        LatLng coordenadas= new LatLng(latitud, longitud);
        //CameraUpdate ubicacionactual= CameraUpdateFactory.newLatLngZoom(coordenadas,16);
        //if(marcador!=null) {
        //    marcador.remove();}
        //marcador= mMap.addMarker(new MarkerOptions()
        //.position(coordenadas)
        //        .title("Ubicación Actual")
        //        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));



        mMap.addMarker(new MarkerOptions().position(coordenadas).title("Ubicación Actual"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
        CameraPosition camaraPosition = new CameraPosition.Builder()
                .target(coordenadas)
                .zoom(14)
                .bearing(90)
                .tilt(45)
                .build();
        mMap.animateCamera((CameraUpdateFactory.newCameraPosition(camaraPosition)));

        // mMap.animateCamera(ubicacionactual);
    }

    private void actualizarUbicacion(Location location){
        if (location != null) {

            latitud=location.getLatitude();
            longitud = location.getLongitude();
            String a = String.valueOf(location.getLatitude());

            agregar_marcador(latitud,longitud);

        }
    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    };

    private void ubicacionactual(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No hay permiso", Toast.LENGTH_LONG).show();
            return;
        }
        LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,15000,0,locationListener);
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