package com.get.current.location.in.java.utilities;

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GoogleUtils {

    private static final String TAG = GoogleUtils.class.getSimpleName();

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (googleApiAvailability.isUserResolvableError(resultCode))
            {
                googleApiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.e(TAG, "Google Play Services is user resolvable error.");
            }
            else
            {
                Log.e(TAG, "Google Play Services not supported.");
            }
            return false;
        }
        else
        {
            Log.e(TAG, "Google Play Services available : "+ googleApiAvailability.getErrorString(resultCode));
            return true;
        }
    }
}
