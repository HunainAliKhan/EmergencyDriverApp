package com.example.hunain.emergencydriverapp.Common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hunain on 4/1/2018.
 */

public class StoreToken {

    private static final String SHARED_PREF_NAME = "sharedPrefrence";
    private static final String KEY_ACCESS_TOKEN = "token";
    private static Context context;
    private static StoreToken mInstance;
    private  SharedPreferences.Editor editor;

    public StoreToken(Context context){
        this.context = context;
    }



    public static synchronized StoreToken getInstance(Context context){
        if(mInstance == null){
            mInstance = new StoreToken(context);

        }
        return  mInstance;
    }



    public  boolean storeToken(String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString(KEY_ACCESS_TOKEN,token);
        editor.apply();
        return true;
    }



    public String getToken(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return  sharedPreferences.getString(KEY_ACCESS_TOKEN,null);
    }

    public void remove(String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor.remove(token);
    }
}
