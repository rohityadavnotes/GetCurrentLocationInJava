package com.get.current.location.in.java.utilities;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapUtils {

    public static Marker setMarkerOnMyCurrentLocation(GoogleMap googleMap, LatLng point, String titleString, String snippetString, BitmapDescriptor bitmapDescriptor) {
        googleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.snippet(snippetString);
        markerOptions.title(titleString);
        markerOptions.visible(true);
        markerOptions.icon(bitmapDescriptor);
        Marker marker = googleMap.addMarker(markerOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        return marker;
    }
}
