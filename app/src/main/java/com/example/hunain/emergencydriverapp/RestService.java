package com.example.hunain.emergencydriverapp;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by hunain on 11/17/2017.
 */

public class RestService {
    //You need to change the IP if you testing environment is not local machine
    //or you may have different URL than we have here


    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    Retrofit.Builder builder;
    private InstituteService apiService;

    public RestService() {
         String URL =  "http://192.168.1.101:51444/api/";
         builder = new Retrofit.Builder()
                .baseUrl(URL).addConverterFactory(GsonConverterFactory.create());



        Retrofit retrofit = builder.build();
        apiService = retrofit.create(InstituteService.class);
    }

    public  RestService(String url){

        builder = new Retrofit.Builder()
                .baseUrl(url).addConverterFactory(ScalarsConverterFactory.create());




        Retrofit retrofit = builder.build();
        apiService = retrofit.create(InstituteService.class);
    }

    public InstituteService getService() {
        return apiService;
    }
}