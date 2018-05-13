package com.example.hunain.emergencydriverapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.hunain.emergencydriverapp.Common.ConnectivityHelper;
import com.example.hunain.emergencydriverapp.Common.Constants;
import com.example.hunain.emergencydriverapp.Recievers.DirectionReciever;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GetRequestFragment extends Fragment{ //implements RoutingListener {
    Driver driver;
    TextView name;
    TextView problem;
    Button accept,reject;
    GoogleMap mMap;
   Book book;
    FragmentManager fm;
   Context mContext;
    LatLng userPosition;
    private static final float DEFAULT_ZOOM = 15f;
    Marker marker;
    RestService rs;
    List<Polyline> polylines;
    LatLngBounds.Builder boundsBuilder;

    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_get_request, container, false);
        name = (TextView) view.findViewById(R.id.userName);
        problem = (TextView) view.findViewById(R.id.userProblem);
        accept = (Button) view.findViewById(R.id.accept);
        reject = (Button) view.findViewById(R.id.reject);
        book = (Book) getArguments().getSerializable("requests");
        polylines = new ArrayList<Polyline>();
       boundsBuilder = new LatLngBounds.Builder();
       problem.setText(book.problem);
      name.setText(book.customerName);
      Constants.endUserToken = book.customerDeviceToken;
        mContext = getActivity().getApplicationContext();

        fm = getActivity().getSupportFragmentManager();
   //     routeToTheMarker();
        scheduleDestinationRoute();
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Driver driver = new Driver();
               // Log.i("Driver id",String.valueOf(Driver.dId));
                //rs = new RestService();
                String serialNumber = ConnectivityHelper.getMacAddress(getContext());
                 Call<String> sendDriverData = new RestService().getService().sendDriverCredentialsToUser(serialNumber, Constants.driverCurrentLocation);
                 sendDriverData.enqueue(new Callback<String>() {
                     @Override
                     public void onResponse(Call<String> call, Response<String> response) {
                         Toast.makeText(mContext,"send succefullly",Toast.LENGTH_SHORT);
                     }

                     @Override
                     public void onFailure(Call<String> call, Throwable t) {

                     }
                 });





            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Call<String> cancelRequest = new RestService().getService().cancelUserRequest(book.CID);
               cancelRequest.enqueue(new Callback<String>() {
                   @Override
                   public void onResponse(Call<String> call, Response<String> response) {

                    //   erasePolyLine();
                       marker.remove();
                       //moveCamera();
                       if(fm.getBackStackEntryCount() > 0) {
                           fm.popBackStack();
                       }
                       Toast.makeText(mContext,"Request has been cancelled",Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void onFailure(Call<String> call, Throwable t) {

                   }
               });
             // erasePolyLine();


            }
        });

        return  view;


    }

    private void moveCamera() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(24.920716, 67.032184)).title("Fire Brigade"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.920716, 67.032184),DEFAULT_ZOOM));
    }

    private void scheduleDestinationRoute(){

        DirectionReciever directionReciever = new DirectionReciever();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver((BroadcastReceiver) directionReciever, intentFilter);



    }



  /*  public  void routeToTheMarker(){
        LatLng userPosition = new LatLng(book.latitude,book.longitude);
        marker = mMap.addMarker(new MarkerOptions().position(userPosition).title("user location"));
       Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(24.920716,67.032184), new LatLng(book.latitude,book.longitude))
                .build();
        routing.execute();

        boundsBuilder.include(new LatLng(24.920716,67.032184));
        boundsBuilder.include( new LatLng(book.latitude,book.longitude));
    }


    @Override
  public void onRoutingFailure(RouteException e) {
      if(e != null) {
          Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
      }else {
          Toast.makeText(mContext, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
      }


  }

  @Override
  public void onRoutingStart() {

  }

  @Override
  public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
      if(polylines.size()>0) {
          for (Polyline poly : polylines) {
              poly.remove();
          }
      }

      polylines = new ArrayList<>();
      //add route(s) to the map.
      for (int i = 0; i <route.size(); i++) {

          //In case of more than 5 alternative routes
          int colorIndex = i % COLORS.length;

         PolylineOptions polyOptions = new PolylineOptions();
          polyOptions.color(getResources().getColor(COLORS[colorIndex]));
          polyOptions.width(10 + i * 3);
          polyOptions.addAll(route.get(i).getPoints());
          Polyline polyline = mMap.addPolyline(polyOptions);
          polylines.add(polyline);
          int routePadding = 100;
          LatLngBounds latLngBounds = boundsBuilder.build();

          mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200));


          Toast.makeText(mContext,"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
      }
  }

  @Override
  public void onRoutingCancelled() {

  }

    public  void erasePolyLine(){
        if(!polylines.isEmpty()){
        for(Polyline  line : polylines){
            line.remove();
        }}
        polylines.clear();
        mMap.clear();
    }*/
}
