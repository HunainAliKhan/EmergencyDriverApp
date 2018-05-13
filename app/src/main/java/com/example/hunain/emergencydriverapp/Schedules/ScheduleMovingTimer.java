package com.example.hunain.emergencydriverapp.Schedules;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.hunain.emergencydriverapp.Common.Constants;
import com.example.hunain.emergencydriverapp.DriverMapsActivity;
import com.example.hunain.emergencydriverapp.Recievers.UpdateLocationReciever;

/**
 * Created by hunain on 4/10/2018.
 */

public class ScheduleMovingTimer {

    public void moveDriver(Context context){
        Intent intent = new Intent(context, UpdateLocationReciever.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        long firstTime = System.currentTimeMillis() +  Constants.TRACKING_SCHEDULE;
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstTime, Constants.TRACKING_SCHEDULE,pending);
    }



}

