package com.googlemap;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Asus_Dev on 9/4/2015.
 */
public class MyLocationListener implements LocationListener{

    private MapsActivity mapsActivity;

    public MyLocationListener(MapsActivity _mapsActivity) {
        this.mapsActivity = _mapsActivity;
    }

    @Override
    public void onLocationChanged(Location location) {
        mapsActivity.setMyLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
