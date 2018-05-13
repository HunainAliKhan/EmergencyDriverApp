package com.example.hunain.emergencydriverapp.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.hunain.emergencydriverapp.Common.Constants;
import com.example.hunain.emergencydriverapp.Directions;
import com.example.hunain.emergencydriverapp.Recievers.DirectionReciever;
import com.example.hunain.emergencydriverapp.RequestStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import static android.location.LocationManager.NETWORK_PROVIDER;

/**
 * Created by hunain on 4/21/2018.
 */

public class DirectionService extends Service {


    private static final int TODO = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public DriverLocationListener listener;
    public Location previousBestLocation = null;
    public LatLng currentPostion;
    public LatLng toPostion;
    public Directions direction;

    Intent intent;
    int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
       // intent = new Intent(BROADCAST_ACTION);


    }

    @Override
    public void onStart(Intent intent, int startId) {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new DriverLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
           // return TODO;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);
        if(RequestStatus.isRequestAccept){
            drawPolyLine();
        }
        return Service.START_STICKY;
    }

    public void drawPolyLine(){

            direction = new Directions(getApplicationContext(),Constants.driverCurrentLocation,Constants.userLocation);
            direction.getDirections();

    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }

   /* public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }*/
    public class DriverLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
           // Log.i("**************************************", "Location changed");
           if(Constants.driverMarker == null){
               direction = new Directions();
               currentPostion = new LatLng(loc.getLatitude(),loc.getLongitude());
               Constants.driverMarker = Constants.map.addMarker(new MarkerOptions().position(currentPostion));       // mMap.addMarker(new MarkerOptions().position(new LatLng(24.920716, 67.032184)).title("Fire Brigade"));
               moveCamera(currentPostion,Constants.DEFAULT_ZOOM);

           }else {
               toPostion = new LatLng(loc.getLatitude(),loc.getLongitude());
               Constants.driverCurrentLocation = currentPostion;
               direction.moveMarker(currentPostion,toPostion,false);
               currentPostion = toPostion;
           }

            Toast.makeText(getApplicationContext(),"onChangeLocation triggered",Toast.LENGTH_SHORT).show();

        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }
        private void moveCamera(LatLng latLng, float zoom) {
        /*mMap.addMarker(new MarkerOptions().position(new LatLng(24.920716, 67.032184)).title("Fire Brigade"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.920716, 67.032184),DEFAULT_ZOOM));*/
            Constants.map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }


    }

}


