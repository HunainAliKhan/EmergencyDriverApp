package com.example.hunain.emergencydriverapp.Common;

import android.location.Location;
import android.location.LocationManager;

import com.example.hunain.emergencydriverapp.InstituteService;
import com.example.hunain.emergencydriverapp.RestService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by hunain on 3/31/2018.
 */

public class Constants {

   // public static LatLng driverCurrentLocation;

    public static LatLng userLocation;
    public static LatLng driverCurrentLocation;
    public static long TRACKING_SCHEDULE = (5 * 1000);
    public static GoogleMap map;
    public static String endUserToken = null;
    public static Marker driverMarker;
    public static final float DEFAULT_ZOOM = 15f;
    public static boolean isGPSEnabled = false;







    // public static final String mapUrl =  "https://maps.googleapis.com/";



}
