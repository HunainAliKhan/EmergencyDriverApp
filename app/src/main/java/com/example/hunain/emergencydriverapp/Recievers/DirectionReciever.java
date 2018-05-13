package com.example.hunain.emergencydriverapp.Recievers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.widget.Toast;

import com.example.hunain.emergencydriverapp.Common.ConnectivityHelper;
import com.example.hunain.emergencydriverapp.Common.Constants;
import com.example.hunain.emergencydriverapp.R;
import com.example.hunain.emergencydriverapp.Services.DirectionService;
import com.example.hunain.emergencydriverapp.Services.LocationSyncService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by hunain on 4/26/2018.
 */

public class DirectionReciever extends BroadcastReceiver {
    public static int REQUEST_CODE = 54321;
    private String TAG = this.getClass().getSimpleName();


    private LocationResult mLocationResult;

    @Override
    public void onReceive(Context context, Intent intent) {
      /*  if(LocationResult.hasResult(intent)) {
            this.mLocationResult = LocationResult.extractResult(intent);
            getCurrentLocation(mLocationResult.getLastLocation());

            Log.i(TAG, "Location Received: " + this.mLocationResult.toString());
        }*/
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Intent intnt = new Intent(context,DirectionService.class);
      if(ConnectivityHelper.isInternetConnectionAvailable(context)){
          if(ConnectivityHelper.isGPSEnabled(context)){
              context.startService(intnt);
          }else{
              Constants.isGPSEnabled = true;
              Toast.makeText(context,"GPS is not enabled! Please Enable it",Toast.LENGTH_SHORT).show();
          }
      }else{
          Toast.makeText(context,"Network is not available",Toast.LENGTH_SHORT).show();
      }
    }
/*
    public void getCurrentLocation(Location location){
        if(Constants.driverMarker == null) {
            Constants.driverMarker = Constants.map.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("Current Location"));
        }else{
            Constants.driverMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        }
        moveCamera(new LatLng(location.getLatitude(),location.getLongitude()));
    }
    private void moveCamera(LatLng latLng) {
        *//*mMap.addMarker(new MarkerOptions().position(new LatLng(24.920716, 67.032184)).title("Fire Brigade"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.920716, 67.032184),DEFAULT_ZOOM));*//*
        Constants.map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM));
    }*/
  /*  private void showGPSDisabledAlertToUser(final Context context,final Intent intnt) {
        @SuppressLint("RestrictedApi") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(DirectionReciever.this, R.style.AppDialogBox));
        alertDialogBuilder.setTitle(R.string.GPSalertDialog);
        alertDialogBuilder.setMessage("GPS is disabled in your device !! please enable it?")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent callGPSSettingIntent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(callGPSSettingIntent);

                            if(ConnectivityHelper.isGPSEnabled(context)){
                                context.startService(intnt);
                            }
                        }
                    });
    alertDialogBuilder.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
    AlertDialog alert = alertDialogBuilder.create();
    alert.show();
}*/
}
