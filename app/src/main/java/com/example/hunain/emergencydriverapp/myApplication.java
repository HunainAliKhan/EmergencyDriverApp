package com.example.hunain.emergencydriverapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by hunain on 12/11/2017.
 */

public class myApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static boolean isActive;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }
    public static boolean isActivityVisible(){
        return isActive;
    }

    public static void isResume(){
        isActive = true;
    }
    public  static  void isPause(){
        isActive = false;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        isActive = true;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
        isActive = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
