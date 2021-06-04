package com.get.current.location.in.java;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;
import androidx.annotation.NonNull;

public class BaseApplication extends Application {

    public static final String TAG = BaseApplication.class.getSimpleName();

    private static BaseApplication INSTANCE;

    public static BaseApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "LANDSCAPE");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i(TAG, "PORTRAIT");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
