package co.edu.unipiloto.boundservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private OdometerService odometro;
    private boolean enlazado=false;
    private LocationListener listener;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) iBinder;
            odometro= odometerBinder.getOdometer();
            enlazado=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            enlazado=false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

            }

            @Override
            public void onProviderDisabled(String arg0){

            }

            @Override
            public void onProviderEnabled(String arg0){

            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle bundle){

            }
        };
        mostrarDistancia();
    }

    @Override
    public void onStart(){
        super.onStart();
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(enlazado){
            unbindService(connection);
            enlazado=false;
        }
    }

    private void mostrarDistancia(){
        TextView verDistancia = findViewById(R.id.distancia);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distancia = 0.0;
                if(enlazado && odometro!=null){
                    distancia= odometro.getDistancia();
                }

                String distanciaStr = String.format(Locale.getDefault(), "%1$,.2f  Km", distancia);

                verDistancia.setText(distanciaStr);
                handler.postDelayed(this, 2000);
            }
        });
    }
}