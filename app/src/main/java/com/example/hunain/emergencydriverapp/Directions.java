package com.example.hunain.emergencydriverapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hunain.emergencydriverapp.Common.ConnectivityHelper;
import com.example.hunain.emergencydriverapp.Common.Constants;
import com.example.hunain.emergencydriverapp.Common.MarkerAnimation;
import com.example.hunain.emergencydriverapp.Recievers.DirectionReciever;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.LogRecord;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hunain on 4/14/2018.
 */

public class Directions {

    private PolylineOptions polylineOptions,blackPolyLineOptions;
    private Polyline blackPolyline,greyPolyline;
    private Marker destinationMarker;
    private double lat,lng;
    private Handler handler;
    private LatLng startPositon,endPosition;
    private int index,next;
    private String destination;
    private List<LatLng> polylineList;
    private Context context;
    float v;
    //GoogleMap map;
    final float durationInMs = 3000;

   // final long start = SystemClock.uptimeMillis();



    public Directions(Context context,LatLng startPositon,LatLng endPosition){
        polylineList = new ArrayList<>();

        this.endPosition = endPosition;
        this.startPositon = startPositon;
        this.context = context;
       // this.carMarker = currentMarker;

    }
    public Directions(){}

    public void moveMarker(final LatLng currentLocation, final LatLng toPosition, final boolean hideMarker) {
            handler = new Handler();


        final long start = SystemClock.uptimeMillis();
        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();

        // handler.post(new Runnable() {


        // @Override
        //   public void run() {
        /*if (index < (polylineList.size() - 1)) {
            index++;
            next = index + 1;
            startPositon = polylineList.get(index);
            endPosition = polylineList.get(next);
        }
    */

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * currentLocation.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * currentLocation.latitude;
                LatLng newPosition = new LatLng(lat, lng);
                Constants.driverMarker.setPosition(newPosition);
                //float bearing  = currentLocation.bearingTo(toPosition);
                //carMarker.setRotation(bearing);
                Constants.driverMarker.setAnchor(0.5f, 0.5f);
               // carMarker.setRotation(currentLocation.getBearing());

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        Constants.driverMarker.setVisible(false);
                    } else {
                        Constants.driverMarker.setVisible(true);
                    }
                }
            }
        });


        // }
        //});
        //}
    }






               /* ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(1000); // duration 1 second
                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LatLng newPosition = new LatLng(((v * endPosition.latitude) + ((1 - v) * currentLocation.getLatitude())), ((v * endPosition.longitude) + ((1 - v) * currentLocation.getLatitude())));
                        carMarker.setPosition(newPosition);
                        carMarker.setAnchor(0.5f, 0.5f);
                        carMarker.setRotation(getBearing(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), newPosition));
                        map.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(newPosition)
                                        .zoom(15.5f).build()));
                    }
                });
                valueAnimator.start();*/
                //  handler.postDelayed(this, 3000);




                /*handler.post(new Runnable() {
                    @Override
                    public void run() {*/

                //});






    private float getBearing(LatLng startPos,LatLng endPos){
        double lat = Math.abs(startPos.latitude - endPos.latitude);
        double lng = Math.abs(endPos.longitude - endPos.longitude);


        if(startPos.latitude < endPos.latitude && startPos.longitude < endPosition.longitude)
            return  (float) (Math.toDegrees(Math.atan(lng/lat)));

        else if(startPos.latitude >= endPos.latitude && startPos.longitude < endPos.longitude)
            return  (float) ((90 - (Math.toDegrees(Math.atan(lng/lat)))) + 90);
        else if(startPos.latitude >= endPos.latitude && startPos.longitude >= endPos.longitude)
            return  (float) ((Math.toDegrees(Math.atan(lng/lat))) + 180);
        else if(startPos.latitude < endPos.latitude && startPos.longitude >= endPos.longitude)
            return  (float) ((90 - Math.toDegrees(Math.atan(lng/lat))) + 270);

        return  -1;


    }


    public void getDirections(){
        if(ConnectivityHelper.isInternetConnectionAvailable(context)) {
            String requestApi = null;


            try {
                //requestApi = "https://maps.googleapis.com/maps/api/directions/json?/mode=driving&transit_routing_preference=less_driving&origin=24.9176968,67.0224648&destination=24.923859,67.031765&key=AIzaSyAOraCkFeLVtrcJSeDK4wW6wFq9MEuU3CQ";

                requestApi = "https://maps.googleapis.com/maps/api/directions/json?/mode=driving&transit_routing_preference=less_driving&origin="+ startPositon.latitude +"," + startPositon.longitude + "&destination=" + endPosition.latitude +"," + endPosition.longitude + "&key=" + context.getResources().getString(R.string.google_map_directionApi);
            //requestApi = "https://maps.googleapis.com/maps/api/directions/json?/mode=driving&transit_routing_preference=less_driving&origin=24.9176968,67.0224648&destination=24.923859,67.031765&key=AIzaSyAOraCkFeLVtrcJSeDK4wW6wFq9MEuU3CQ";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestApi, null, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //JSONObject jsonObject = new JSONObject(response.toString());
                            JSONArray jsonArray = response.getJSONArray("routes");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyline = poly.getString("points");
                                polylineList = decodePolyLine(polyline);


                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polylineList) {
                                    builder.include(latLng);
                                }
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 5);
                                Constants.map.animateCamera(mCameraUpdate);


                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polylineList);
                                greyPolyline = Constants.map.addPolyline(polylineOptions);

                                blackPolyLineOptions = new PolylineOptions();
                                blackPolyLineOptions.color(Color.BLACK);
                                blackPolyLineOptions.width(5);
                                blackPolyLineOptions.startCap(new SquareCap());
                                blackPolyLineOptions.endCap(new SquareCap());
                                blackPolyLineOptions.jointType(JointType.ROUND);
                                blackPolyLineOptions.addAll(polylineList);


                                blackPolyline = Constants.map.addPolyline(polylineOptions);
                                Constants.map.addMarker(new MarkerOptions().position(polylineList.get(polylineList.size() - 1)));
                            //    animateMarker();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                RequestQueue quew = Volley.newRequestQueue(context.getApplicationContext());
                quew.add(jsonObjectRequest);


            }catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    private List decodePolyLine(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    public void animateMarker() {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {


                        List<LatLng> points = greyPolyline.getPoints();
                        v = animation.getAnimatedFraction();
                        float percentValue =(float) animation.getAnimatedValue();

                        int size = points.size();
                        int newPoints = (int) (size * (percentValue/100.0));
                        List<LatLng> p = points.subList(0,newPoints);
                        blackPolyline.setPoints(p);


//                        marker.setPosition(newPosition);
//                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));

                }

            });

            valueAnimator.start();
           // carMarker = currentMarker;
            handler = new Handler();
            index =-1;
            next = 1;



        }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }





}
