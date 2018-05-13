package com.example.hunain.emergencydriverapp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import com.example.hunain.emergencydriverapp.Common.Constants;
import com.example.hunain.emergencydriverapp.Common.StoreToken;
import com.example.hunain.emergencydriverapp.RestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hunain on 3/25/2018.
 */

public class LocationSyncService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateLocation();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }




    public void updateLocation(){
        String token = StoreToken.getInstance(getApplicationContext()).getToken();
        Call<String> mCall = new RestService().getService().DriverLocation(Constants.driverCurrentLocation,token);
        mCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("Update Location",response.message());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getBaseContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}
