package co.edu.unipiloto.boundservices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Random;

public class OdometerService extends Service {

    private final IBinder binder = new OdometerBinder();
    private final Random aleatorio = new Random();
    private LocationListener listener;
    private static double distanciaEnMetros;
    private static Location ultimaLocalizacion = null;
    private LocationManager locManager;


    public OdometerService() {
    }

    public class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public double getDistancia() {
        //return aleatorio.nextDouble();
        return distanciaEnMetros/1000;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (ultimaLocalizacion != null) {
                    ultimaLocalizacion = location;
                }
                distanciaEnMetros += location.distanceTo(ultimaLocalizacion);
                ultimaLocalizacion = location;
            }

            @Override
            public void onProviderDisabled(String arg0) {

            }

            @Override
            public void onProviderEnabled(String arg0) {

            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle bundle) {

            }
        };
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String proveedor = locManager.getBestProvider(new Criteria(), true);

        if (proveedor != null &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.v("LOCATION MANAGER", locManager.getAllProviders().toString());
            locManager.requestLocationUpdates(proveedor, 2000, 1, listener);
        }else{
            return;
        }
    }

    @Override
    public void onDestroy(){
        if(locManager != null && listener!=null){
            Log.v("ON DESTROY", locManager.getAllProviders().toString());
            locManager.removeUpdates(listener);
        }
        locManager=null;
        listener = null;
    }
}