package com.example.hunain.emergencydriverapp;

import com.example.hunain.emergencydriverapp.Entity.Token;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by hunain on 11/17/2017.
 */

public interface InstituteService {


    //i.e. http://localhost/api/institute/Students/1
    //Get student record base on ID



    @POST("Driver/driverRegistration/")
    Call<String> driverRegistration(@Body Driver driver);



    @POST("Request/sendDriverCredentialsToUser/")
    Call<String> sendDriverCredentialsToUser(@Query("serialNumber") String serialNumber,@Query("driverCurrentLocation") LatLng driverLocation);




    @POST("Request/cancelUserRequest/")
    Call<String> cancelUserRequest(@Query("id") int id);


    @GET ("Driver/login/")
    Call<String> login(@Query("phoneNumber") String phoneNumber);

    @POST ("Tracking/DriverLocation/")
    Call<String> DriverLocation(@Query("driverCurrentLocation")LatLng driverLocation,@Query("token")String token);




       @PUT("Token/UpdateToken/{token}")
    Call<String> UpdateToken(@Body Token token);















   /* @GET("Department/getRegionalDepartment")
    Call<List<Department>> getRegionalDepartment();



    @GET("GetRequest/getRequests/")
    Call<GetRequest> getRequests(@Query("id") int id);*/




    //i.e. http://localhost/api/institute/Students/1
    //Delete student record base on ID

}
