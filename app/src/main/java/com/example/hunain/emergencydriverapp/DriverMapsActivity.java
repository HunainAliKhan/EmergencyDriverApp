package com.example.hunain.emergencydriverapp;

import android.Manifest;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.hunain.emergencydriverapp.Common.Constants;
import com.example.hunain.emergencydriverapp.Recievers.DirectionReciever;
import com.example.hunain.emergencydriverapp.Schedules.ScheduleMovingTimer;
import com.example.hunain.emergencydriverapp.Services.DirectionService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import android.support.v7.app.AppCompatActivity;

public class DriverMapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener
       {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    String mLastUpdateTime;
    Location mLastLocation;
    Button reject;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    private GoogleApiClient mGoogleApiClient;

    Book book;
    GetRequestFragment getRequest;
    String action = "com.example.hunain.emergencyapplication";
    int INTERVAL = 10000, FASTEST_INTERVAL = 5000;

    private DrawerLayout mDrawerLayout;

    FirebaseAuth auth;
    // FusedLocationProviderClient mFusedLocationClient;
    String provider;
    private static String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private Location currentLocation;
    private Marker currentPositionMarker;

    Directions direction;



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    //MyBroadcastReceiver broadcastReceiver = null;
    boolean mIsReceiverRegistered = false;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            //Book book =(Book) intent.getSerializableExtra("request");
            // Book book  = (Book) intent.getSerializableExtra("request");
            //requestPanel();
            if (RequestStatus.isRequestAccept && "Emergency".equals(RequestStatus.title)) {
                book = (Book) intent.getSerializableExtra("request");
                //ScheduleMovingTimer scheduleDriverTracking = new ScheduleMovingTimer();
                //scheduleDriverTracking.moveDriver(context);

                requestPanel();
                startDirectionService();
            //    stateChanged(false);
            } else if (!(RequestStatus.isRequestAccept) && ("cancel".equals(RequestStatus.title))) {
                //Fragment f = (Fragment) fragmentManager.findFragmentById(R.id.fragment_container);
                reset();
                stateChanged(true);
                //fragmentTransaction.commit();
                //fragmentTransaction.commit();
            }
            // requestPanel(polylines);

            //do other stuff here

        }
    };

    private IntentFilter intentFilter = new IntentFilter("myFunction");

    @Override
    protected void onResume() {
        // this.registerReceiver(mMessageReceiver,intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, intentFilter);
        super.onResume();
        myApplication.isResume();

        if (RequestStatus.isRequestAccept && "Emergency".equals(RequestStatus.title)) {
            book = (Book) getIntent().getSerializableExtra("request");
            requestPanel();
            startDirectionService();
           // stateChanged(false);


        } else if (!(RequestStatus.isRequestAccept) && ("cancel".equals(RequestStatus.title))) {

            reset();
            stateChanged(true);
        }

        if(Constants.isGPSEnabled){
           // startDirectionService();
        }
       /* // This will start Location update on resume
        if (mGoogleApiClient.isConnected()) {

            Log.d("Location Update Resume", "Location update resumed .....................");
        }*/

//        direction.animateMarker(currentPositionMarker);



    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void stateChanged(boolean state) {
        RequestStatus.isRequestAccept = state;
    }

    public void reset() {
     //   getRequest.erasePolyLine();
        fragmentManager.popBackStack();
        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

    }

    protected void onPause() {
        //unregisterReceiver(mMessageReceiver);
        super.onPause();
        myApplication.isPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
       // stopLocationUpdates();

    }

    private static final int SECOND_ACTIVITY_RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
       //googleApiCientConnection();
       // mGoogleApiClient.connect();
        //createLocationRequest();




        // googleApiCientConnection();

        setContentView(R.layout.activity_driver_maps);

        getLocationPermission();

        Log.i("dirver id", String.valueOf(Driver.dId));


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, intentFilter);
        //Navigation Drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
        updateUI();

    }

    @Override
    protected void onStop() {
        super.onStop();
       // Log.d("Stops", "onStop fired ..............");
      //  mGoogleApiClient.disconnect();
       // Log.d("Connection", "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
       /* mMap = googleMap;
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        Constants.map = mMap;

       *//* myLocation = new LatLng(24.920716, 67.032184);
        mMap.addMarker(new MarkerOptions().position(new LatLng(24.920716, 67.032184)).title("Fire Brigade"));
       // mMap.addMarker(new MarkerOptions().position(new LatLng(24.920716, 67.032184)).title("Fire Brigade"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.920716, 67.032184),DEFAULT_ZOOM));*//*

        if (mLocationGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

          //  mMap.setMyLocationEnabled(true);

*/
        mMap = googleMap;
        Constants.map = mMap;

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (mLocationGranted) {
         //getDeviceLocation();

            startDirectionService();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this,"Permission hasnt been granted",Toast.LENGTH_LONG).show();
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

      //  mMap.setMyLocationEnabled(true);

        }
     //   startService();
           /* if (!isGooglePlayServicesAvailable()) {
                finish();
            }
            //createLocationRequest();

        }*/

        // Add a marker in Sydney and move the camera

        //mMap.addMarker(new MarkerOptions().position(myLocation).title("Driver Location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

    public void startDirectionService(){
        this.sendBroadcast(new Intent(this, DirectionReciever.class));
     /*   Intent intent = new Intent(this, DirectionService.class);


       // final PendingIntent pIntent = PendingIntent.getBroadcast(this, DirectionReciever.REQUEST_CODE, intentFilesSynchronizer, PendingIntent.FLAG_UPDATE_CURRENT);
      startService(intent);

*/

    }





    public synchronized  void googleApiCientConnection() {
       mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            mLocationGranted = false;
                            return;
                        }
                    }
                    mLocationGranted = true;

                }
            }
        }
    }
           public void getDeviceLocation() {
               mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
               try {
                   if (mLocationGranted) {
                       final Task location = mFusedLocationProviderClient.getLastLocation();
                       location.addOnCompleteListener(new OnCompleteListener() {
                           @Override
                           public void onComplete(@NonNull Task task) {
                               if (task.isSuccessful()) {
                                   // Complete location found

                                   Location currentLocation = (Location) task.getResult();
                                   // we take the result and move the camera to get result
                                   //im gonna create method to move a method
                                   moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                               } else {
                                   Toast.makeText(DriverMapsActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
                               }

                           }
                       });
                   }
               } catch (SecurityException e) {
                   Log.e("getDeviceLocation: ", e.getMessage());

               }
           }

  /*  public void getDeviceLocation() {

            *//*mMap.addMarker(new MarkerOptions().position(new LatLng(24.920716, 67.032184)).title("Fire Brigade"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.920716, 67.032184),DEFAULT_ZOOM));*//*
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "found Location", Toast.LENGTH_LONG).show();
                           // if (task.getResult() != null) {

                                currentLocation = (Location) task.getResult();

                                Constants.driverCurrentLocation = new LatLng(currentLocation.getLongitude(), currentLocation.getLatitude());
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                        .title("Current Location"));
                                // Constants.driverCurrentLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);


                            } else {
                                Toast.makeText(getApplicationContext(), "Unable to find location ", Toast.LENGTH_LONG).show();
                            }
                        }

                });
            }
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }


    }*/

    private void moveCamera(LatLng latLng, float zoom) {
        /*mMap.addMarker(new MarkerOptions().position(new LatLng(24.920716, 67.032184)).title("Fire Brigade"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.920716, 67.032184),DEFAULT_ZOOM));*/
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void requestPanel() {
        getRequest = new GetRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("requests", book);
        getRequest.setArguments(bundle);
        getRequest.mMap = mMap;
        // getRequest.reject(polylines);
        //fragmentManager = getSupportFragmentManager();
        //fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, getRequest);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public void logout(View view) {
           auth = FirebaseAuth.getInstance();


           auth.signOut();
           startActivity(new Intent(DriverMapsActivity.this, MainActivity.class));

       }

    //Update Driver location
    private boolean isGooglePlayServicesAvailable() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(this, status, 0).show();
            return false;
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d("Location Updates", "Location update started ..............: ");
    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        Log.d("Location Updates", "Location update stopped .......................");
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Connected", "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());

       /* if(Constants.driverMarker == null) {
            Constants.driverMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .title("Current Location"));
        }
        startLocationUpdates();

        direction = new Directions(this,mMap,new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),new LatLng(24.923859, 67.031765),currentPositionMarker);
        direction.getDirections();
        System.out.println(Toast.makeText(getApplicationContext(), "On Connected runs", Toast.LENGTH_SHORT));*/


        // createLocationRequest();

       // MarkerAnimation.animateMarker(currentLocation,mMap.addMarker(new MarkerOptions().position(new LatLng(24.9190650, 67.0226848))));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Connection Failed", "Connection failed: " + connectionResult.toString());
    }

    final long start = SystemClock.uptimeMillis();

    @Override
    public void onLocationChanged(Location location) {
       // Log.d("Location Changed", "Firing onLocationChanged..............................................");

      /*  Constants.driverCurrentLocation = new LatLng(location.getLatitude(),location.getLongitude());
        Toast.makeText(getApplicationContext(),"send location",Toast.LENGTH_SHORT).show();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());*/
       // currentLocation = location;
       // direction.moveMarker(currentLocation,location,false);

      //  currentLocation = location;
       // direction.moveMarker(currentLocation,false);
        /*final Interpolator interpolator = new LinearInterpolator();
        long elapsed = SystemClock.uptimeMillis() - start;
        float t = Math.max(
                1 - interpolator.getInterpolation((float) elapsed), 0);*/
        /*currentPositionMarker.setAnchor(0.5f, 1.0f);
        currentPositionMarker.setPosition(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));*/
       // mLastUpdateTime =  DateFormat.getTimeInstance().format(new Date());
       // direction.moveMarker();
        //currentPositionMarker.setPosition(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
       // direction.getDirections();
       Toast.makeText(getApplicationContext(),"OnChangeLocationCalled" , Toast.LENGTH_SHORT).show();
       /* LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));*/


    }
    private void updateUI() {
        Log.d("UI Updates", "UI update initiated .............");
        if (null != currentLocation) {
            String lat = String.valueOf(currentLocation.getLatitude());
            String lng = String.valueOf(currentLocation.getLongitude());
           Toast.makeText(getApplicationContext(),lat + "," + lng,Toast.LENGTH_SHORT).show();
        } else {
            Log.d("LocationUpdates", "location is null ...............");
        }
    }






}
