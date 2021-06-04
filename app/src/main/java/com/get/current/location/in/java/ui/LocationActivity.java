package com.get.current.location.in.java.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.get.current.location.in.java.utilities.GoogleUtils;
import com.get.current.location.in.java.utilities.MapUtils;
import com.get.current.location.in.java.R;
import com.get.current.location.in.java.ui.base.BaseActivity;
import com.get.current.location.in.java.utilities.permissionutils.ManagePermission;
import com.get.current.location.in.java.utilities.permissionutils.PermissionDialog;
import com.get.current.location.in.java.utilities.permissionutils.PermissionName;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LocationActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {

    public static final String TAG = LocationActivity.class.getSimpleName();

    private FloatingActionButton floatingActionButton;
    private TextView addressTextView, latitudeTextView, longitudeTextView, lastUpdateTimeTextView;
    private MaterialButton updateOnMaterialButton, updateOffMaterialButton;

    private ManagePermission managePermission;

    private static final int SINGLE_PERMISSION_REQUEST_CODE = 1001;
    private static final int SINGLE_PERMISSIONS_FROM_SETTING_REQUEST_CODE = 2001;

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    private GoogleApiClient googleApiClient;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SettingsClient settingsClient;
    private LocationCallback locationCallback;
    private Location location;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10 * 1000; /* 10 * 1000 = 10 second */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2; /* 10 * 1000 = 10 second */
    public static final long DISPLACEMENT = 10; /* 10 meters distance update call*/
    private LocationRequest locationRequest;

    private LocationSettingsRequest locationSettingsRequest;

    private boolean requestingLocationUpdates = false;
    private String lastUpdateTime = "";

    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);

        if (!GoogleUtils.isGooglePlayServicesAvailable(this)) {
            finish();
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_location;
    }

    @Override
    protected void initializeView() {
        floatingActionButton = findView(R.id.floatingActionButton);

        addressTextView = findView(R.id.addressTextView);
        latitudeTextView = findView(R.id.latitudeTextView);
        longitudeTextView = findView(R.id.longitudeTextView);
        lastUpdateTimeTextView = findView(R.id.lastUpdateTimeTextView);

        updateOnMaterialButton = findView(R.id.updateOnMaterialButton);
        updateOffMaterialButton = findView(R.id.updateOffMaterialButton);
    }

    @Override
    protected void initializeObject() {
        managePermission = new ManagePermission(LocationActivity.this);

        if (Build.VERSION.SDK_INT >= 23) {
            if (managePermission.hasPermission(PermissionName.ACCESS_FINE_LOCATION)) {
                /* Is Granted, Do next code */
                Log.e(TAG, "Is Granted, Do next code");
            } else {
                /* If not granted, Request for Permission */
                ActivityCompat.requestPermissions(LocationActivity.this, new String[]{PermissionName.ACCESS_FINE_LOCATION}, SINGLE_PERMISSION_REQUEST_CODE);
            }
        } else {
            /* Already Granted, Do next code */
            Log.e(TAG, "Already Granted, Do next code");
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        buildGoogleApiClient(this);
    }

    @Override
    protected void initializeToolBar() {
    }

    @Override
    protected void initializeCallbackListener() {
    }

    @Override
    protected void addTextChangedListener() {
    }

    @Override
    protected void setOnClickListener() {
        floatingActionButton.setOnClickListener(this);

        updateOnMaterialButton.setOnClickListener(this);
        updateOffMaterialButton.setOnClickListener(this);
    }

    @Override
    protected void handleClickEvent(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton:
                getLastLocationOne();
                break;
            case R.id.updateOnMaterialButton:
                updateOn();
                break;
            case R.id.updateOffMaterialButton:
                updateOff();
                break;
            default:
                System.out.println("Invalid view id");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapLoadedCallback(this);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (googleApiClient.isConnected()) {

            initFusedLocationProviderClient(this);

            if (requestingLocationUpdates) {
                startLocationUpdates();
            }
            else
            {
                Log.e(TAG, "ENABLE REQUEST UPDATE : "+requestingLocationUpdates);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        connectGoogleApiClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        /* Clearing all the markers */
        googleMap.clear();

        /* Adding a new marker to the current pressed position we are also making the draggable true */
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case SINGLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    String permission = permissions[0];
                    if (permission.equalsIgnoreCase(PermissionName.ACCESS_FINE_LOCATION)) {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            Log.e(TAG, "location permission granted");
                        } else if (managePermission.shouldShowRequestPermissionRationale(permission)) {
                            Log.e(TAG, "location permission denied");
                            ActivityCompat.requestPermissions(LocationActivity.this, new String[]{PermissionName.ACCESS_FINE_LOCATION}, SINGLE_PERMISSION_REQUEST_CODE);
                        } else {
                            Log.e(TAG, "location permission denied and don't ask for it again");
                            PermissionDialog.permissionDeniedWithNeverAskAgain(
                                    LocationActivity.this,
                                    R.drawable.permission_ic_location,
                                    "Location Permission",
                                    "Kindly allow Location Permission from Settings, without this permission the app is unable to provide location feature. Please turn on permissions at [Setting] -> [Permissions]>",
                                    permission,
                                    SINGLE_PERMISSIONS_FROM_SETTING_REQUEST_CODE);
                        }
                    }
                }
                break;
            default:
                throw new RuntimeException("unhandled permissions request code: " + requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SINGLE_PERMISSIONS_FROM_SETTING_REQUEST_CODE) {
            if (managePermission.hasPermission(PermissionName.ACCESS_FINE_LOCATION)) {
                Log.e(TAG, "permission granted from settings");
            } else {
                Log.e(TAG, "permission is not granted, request for permission, from settings");
                ActivityCompat.requestPermissions(LocationActivity.this, new String[]{PermissionName.ACCESS_FINE_LOCATION}, SINGLE_PERMISSION_REQUEST_CODE);
            }
        }

        if (REQUEST_CHECK_SETTINGS == requestCode) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.e(TAG, "User agreed to make required location settings changes.");
                    /* Nothing to do. startLocationUpdates(); gets called in onResume again. */
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e(TAG, "User chose not to make required location settings changes.");
                    requestingLocationUpdates = false;
                    if (location != null) {
                        Log.e(TAG, "Last Update Time : "+lastUpdateTime);
                    }
                    break;
                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart()");
        connectGoogleApiClient();
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e(TAG, "onRestoreInstanceState(Bundle savedInstanceState)");
        super.onRestoreInstanceState(savedInstanceState);
        updateValuesFromBundle(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        Log.e(TAG, "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume()");
        if (googleApiClient.isConnected() && requestingLocationUpdates) {
            startLocationUpdates();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop()");
        disConnectGoogleApiClient();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState(Bundle outState)");
        outState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates);
        outState.putParcelable(KEY_LOCATION, location);
        outState.putString(KEY_LAST_UPDATED_TIME_STRING, lastUpdateTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy()");
        stopLocationUpdates();
        super.onDestroy();
    }
    /**
     ***********************************************************************************************
     ******************************************* Helper Method *************************************
     ***********************************************************************************************
     */
    private synchronized void buildGoogleApiClient(Context context) {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) context)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) context)
                    .build();
        }
    }

    private void disConnectGoogleApiClient() {
        if (googleApiClient != null)
        {
            if (googleApiClient.isConnected())
            {
                googleApiClient.disconnect();
            }
        }
        else
        {
            Log.e(TAG, "googleApiClient is null");
        }
    }

    private void connectGoogleApiClient() {
        if (googleApiClient != null)
        {
            if (!googleApiClient.isConnected())
            {
                googleApiClient.connect();
            }
        }
        else
        {
            Log.e(TAG, "googleApiClient is null");
        }
    }

    private LocationRequest createLocationRequest(boolean isBalancedPowerAccuracy, long interval, long fastestInterval, long displacement) {
        int priority;

        if (isBalancedPowerAccuracy)
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        else
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY;

        return LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(fastestInterval)
                //.setSmallestDisplacement(displacement)
                .setPriority(priority);
    }

    private LocationSettingsRequest buildLocationSettingsRequest(LocationRequest locationRequest) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        return builder.build();
    }

    private String getCompleteAddressString(Context context, double latitude, double longitude) {
        String stringAddress = "";

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try
        {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {

                Address address = addressList.get(0);
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    stringBuilder.append(address.getAddressLine(i)).append("\n");
                }

                stringAddress = stringBuilder.toString();
            }
            else
            {
                Log.e(TAG, "No Address returned!");
            }
        }
        catch (Exception exception) {
            Log.e(TAG, "Can not get Address!");
            exception.printStackTrace();
        }

        return stringAddress;
    }

    private void updateUI(Location newLocation) {
        if (newLocation != null)
        {
            String address      = getCompleteAddressString(this, newLocation.getLatitude(), newLocation.getLongitude());
            String latitude     = String.valueOf(newLocation.getLatitude());
            String longitude    = String.valueOf(newLocation.getLongitude());
            lastUpdateTime      = getCurrentDate();

            if (latitude.equalsIgnoreCase("0.0") && longitude.equalsIgnoreCase("0.0"))
            {
                if (googleApiClient.isConnected() && requestingLocationUpdates)
                {
                    startLocationUpdates();
                }
            }
            else
            {
                addressTextView.setText(address);
                latitudeTextView.setText(latitude);
                longitudeTextView.setText(longitude);
                lastUpdateTimeTextView.setText(lastUpdateTime);

                Log.e(TAG, "Address : "+address);
                Log.e(TAG, "Latitude : "+latitude);
                Log.e(TAG, "Longitude : "+longitude);
                Log.e(TAG, "Last Update Time : "+lastUpdateTime);

                if(googleMap != null){
                    LatLng point = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    Marker startMarker = MapUtils.setMarkerOnMyCurrentLocation(googleMap, point, lastUpdateTime, address, bitmapDescriptor);
                }
            }
        }
        else
        {
            Log.e(TAG, "updateUI(Location newLocation) : newLocation is null");
        }
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        simpleDateFormat.setTimeZone(timeZone);

        Date date = calendar.getTime();

        return simpleDateFormat.format(date);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location    = locationResult.getLastLocation();

                if (location != null) {
                    updateUI(location);
                }
            }
        };
    }

    private void initFusedLocationProviderClient(Activity activity) {
        try
        {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
            settingsClient = LocationServices.getSettingsClient(activity);

            createLocationCallback();
            locationRequest = createLocationRequest(false, UPDATE_INTERVAL_IN_MILLISECONDS, FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS, DISPLACEMENT);
            locationSettingsRequest = buildLocationSettingsRequest(locationRequest);
        }
        catch (SecurityException securityException)
        {
            Log.e(TAG, "catch (SecurityException securityException)");
            securityException.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if (ActivityCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.e(TAG, "Location settings are not satisfied, but this can be fixed by showing the user a dialog.");
                                try {
                                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                    resolvableApiException.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sendIntentException) {
                                    Log.e(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.e(TAG, "Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.");
                                requestingLocationUpdates = false;
                        }
                    }
                });
    }

    private void stopLocationUpdates() {
        if (!requestingLocationUpdates) {
            Log.e(TAG, "updates never requested");
            return;
        }

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(LocationActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        requestingLocationUpdates = false;
                        Log.e(TAG, "DISABLE REQUEST UPDATE : "+requestingLocationUpdates);
                    }
                });
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for applications that
     * do not require a fine-grained location and that do not need location updates. Gets the best
     * and most recent location currently available, which may be null in rare cases when a location
     * is not available.
     *
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private void getLastLocationOne() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            location    = task.getResult();
                            if (location != null) {
                                updateUI(location);
                            }
                        }
                        else
                        {
                            Log.e(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocationTwo() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        /* request the last location and add a listener to get the response. then update the UI. */
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                /* Got last known location. In some rare situations this can be null. */
                if (location != null) {
                    updateUI(location);
                }
                else {
                    Log.e(TAG, "Location is null");
                }
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                    }
                }
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocationThree() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    updateUI(location);
                }
                else {
                    Log.e(TAG, "Location is null");
                }
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception exception) {
                                          exception.printStackTrace();
                                      }
                                  }
        );
    }

    private void updateOn() {
        Toast.makeText(getApplicationContext(), "UpdateOn", Toast.LENGTH_LONG).show();
        if (!requestingLocationUpdates) {
            requestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    private void updateOff() {
        Toast.makeText(getApplicationContext(), "UpdateOff", Toast.LENGTH_LONG).show();
        stopLocationUpdates();
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
        }

        if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
            location = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
            lastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
        }

        updateUI(location);
    }
}