package com.example.hunain.emergencydriverapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.hunain.emergencydriverapp.Common.Constants;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by hunain on 12/10/2017.
 */

public class NotificationHelper extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       sendNotification(remoteMessage);

    }
    public  void sendNotification(RemoteMessage message) {

        Map<String, String> data = message.getData();
        String body = data.get("body");
        Gson gson = new Gson();
        RequestStatus.title = data.get("title");
        if (RequestStatus.title.equals("cancel")) {

            RequestStatus.isRequestAccept = false;
            Intent intnt = new Intent("myFunction");
            intnt.putExtra("request", "cancel");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intnt);
        } else {

            if (RequestStatus.title.equals("Track")) {
                Constants.userLocation = gson.fromJson(body, LatLng.class);

            } else {
                Book customerNotificationRequest = gson.fromJson(body, Book.class);
                Constants.userLocation = new LatLng(customerNotificationRequest.latitude,customerNotificationRequest.longitude);
                notifyUser(customerNotificationRequest);

            }


        }
    }
    public  void notifyUser(Book request){
        RequestStatus.isRequestAccept = true;
        if (myApplication.isActivityVisible()) {

            Intent intnt = new Intent("myFunction");
            intnt.putExtra("request", request);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intnt);


        } else {

            Intent intent = new Intent(this, DriverMapsActivity.class);
            intent.putExtra("request",request);
            //intent.putExtra("request",customerNotificationRequest);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            long[] pattern = {500, 500, 500, 500, 500};
            Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this, "default");
            mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Emergency")
                    .setContentText(request.problem)
                    .setVibrate(pattern)
                    .setAutoCancel(true)
                    .setLights(Color.RED, 1, 1)
                    .setSound(notificationSoundURI)
                    .setContentIntent(resultIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, mNotificationBuilder.build());
        }
    }

    public void sendUserLocation(LatLng userLocation){
        Intent intnt = new Intent("myFunction");
        intnt.putExtra("request", userLocation);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intnt);
    }





    }
        /*Intent intent1 = new Intent("unique_name");

        //put whatever data you want to send, if any
        intent1.putExtra("request", customerNotificationRequest);

        //send broadcast
        this.sendBroadcast(intent);*/




