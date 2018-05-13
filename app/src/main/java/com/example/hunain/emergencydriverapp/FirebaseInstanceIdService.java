package com.example.hunain.emergencydriverapp;

import android.util.Log;

import com.example.hunain.emergencydriverapp.Business_Object.ITokenBAO;
import com.example.hunain.emergencydriverapp.Business_Object.TokenBAO;
import com.example.hunain.emergencydriverapp.Common.StoreToken;
import com.example.hunain.emergencydriverapp.Data_Access.TokenDAO;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hunain on 12/10/2017.
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {
    private static final String REG_TOKEN = "REG_TOKEN";
    @Override
    public void onTokenRefresh() {
         String recent_token = FirebaseInstanceId.getInstance().getToken();
       // Log.i(REG_TOKEN,recent_token);

        ITokenBAO token = new TokenBAO(new TokenDAO(getApplicationContext()),getApplicationContext());
        token.UpdateToken(recent_token);



        /*
       if your app in background your application will recieve a token  on
        device notification claim other if the application in foreground in order to recieve notification
        and data from firebase you need to create another java class that extend firebase messaging service
        */
    }

    public void saveToken(String token){
        boolean tokenHelper = StoreToken.getInstance(getBaseContext()).storeToken(token);
    }
}
