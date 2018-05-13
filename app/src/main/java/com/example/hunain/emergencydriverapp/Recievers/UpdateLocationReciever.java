package com.example.hunain.emergencydriverapp.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.example.hunain.emergencydriverapp.Common.ConnectivityHelper;
import com.example.hunain.emergencydriverapp.Services.LocationSyncService;

/**
 * Created by hunain on 3/25/2018.
 */

public class UpdateLocationReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mIntent = new Intent(context, LocationSyncService.class);

        if(ConnectivityHelper.isInternetConnectionAvailable(context)){
            context.startService(mIntent);
        }
    }
}
