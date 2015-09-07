package com.googlemap;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, View.OnClickListener {

    public static final String TAG = "MP_MapsActivity";
    private static final String PREFS_NAME = "preferences";
    private static final String KEY_MAP_MARKERS = "markers";
    private static final int LAT = 0;
    private static final int LNG = 1;
    private static final int TITLE = 2;
    private static final int SNIPPET = 3;
    private static final int ICON_PATH = 4;
    private static final int IS_DRAGGABLE = 5;
    private static final int IS_VISIBLE = 6;

    private SharedPreferences mPreferences;
    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private MyLocationListener mMyLocationListener;
    private LatLng mMyLocation;
    private Button mBtnLocInfo;
    private Set<String> mMarkers = new ArraySet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG, TAG + "#onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        readPreferences();
        viewInit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mMyLocationListener);
    }

    private void viewInit() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMyLocationListener = new MyLocationListener(this);

        mBtnLocInfo = (Button) findViewById(R.id.btnLocInf);
        mBtnLocInfo.setOnClickListener(this);
    }


    private void readPreferences() {
        Log.d(TAG, TAG + "#readPreferences");
        try {
            mPreferences = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        } catch (RuntimeException e) {
            mPreferences = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLocInf:
                Log.d(TAG, TAG + "#onBtnLocInfClik");
                showDialog(getAddressForLocation(mMyLocation));
                break;
            default:
                break;
        }
    }

    private String getAddressForLocation(LatLng _myLocation) {
        if (_myLocation != null){
            String myLocInfo = "";
            List<Address> addresses = null;
            try {
                Geocoder mGcd = new Geocoder(this, Locale.getDefault());
                addresses = mGcd.getFromLocation(_myLocation.latitude, _myLocation.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                myLocInfo = "Address: " + addresses.get(0).getAddressLine(0) + "\n" +
                        "City: " + addresses.get(0).getAddressLine(1) + "\n" +
                        "Region: " + addresses.get(0).getAddressLine(2) + "\n" +
                        "Country: " + addresses.get(0).getAddressLine(3);
                if (addresses.get(0).getPostalCode() != null) {
                    myLocInfo = myLocInfo + "\n" + "PostalCode: " + addresses.get(0).getPostalCode();
                } else myLocInfo = myLocInfo + "\n" + "PostalCode: Unknown";
                if (addresses.get(0).getPhone() != null) {
                    myLocInfo = myLocInfo + "\n" + "PhoneCode: " + addresses.get(0).getPhone();
                } else myLocInfo = myLocInfo + "\n" + "PhoneCode: Unknown";
            } else return "No internet connection!";
            return myLocInfo;
        } else return "Navigation Error!";
    }

    private void showDialog(String _myLocInfo) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newDialFrag = LocationDialog.newInstance(_myLocInfo);
        newDialFrag.show(ft, "dialog");
    }

    public void setMyLocation(Location location) {
        if (location != null) {
            Log.d(TAG, "Lat: " + location.getLatitude() + " - " +
                    "Lng: " + location.getLongitude());

            mMyLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;

        this.mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        this.mGoogleMap.setMyLocationEnabled(true);
        this.mGoogleMap.setOnMapLongClickListener(this);

        LatLng thinkMob = new LatLng(48.618416, 22.298971);
        this.mGoogleMap.addMarker(new MarkerOptions().position(thinkMob).title("ThinkMobiles"));
        this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(thinkMob, 15));

        readMarkersFromPref();
    }

    private void readMarkersFromPref() {
        Object[] arr = mPreferences.getStringSet(KEY_MAP_MARKERS, mMarkers).toArray();
        for (int i = 0; i < arr.length; i++) {
            markerFromString((String) arr[i]);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, TAG + "#onMapLongClick");

        String iconPath = "iconPath";
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marker | " + String.format("%1$s | %2$s",
                        String.valueOf(latLng.latitude), String.valueOf(latLng.longitude)))
                .snippet("My first marker")
                .icon(BitmapDescriptorFactory.defaultMarker()));

        mMarkers.add(markerToString(marker, iconPath));
    }

    private String markerToString(Marker _marker, String _iconPath) {
        return String.valueOf(_marker.getPosition().latitude) + ", " +
                String.valueOf(_marker.getPosition().longitude) + ", " +
                _marker.getTitle() + ", " +
                _marker.getSnippet() + ", " +
                _iconPath + ", " +
                String.valueOf(_marker.isDraggable()) + ", " +
                String.valueOf(_marker.isVisible());
    }

    private void markerFromString(String _markerStr) {

        String[] arr = _markerStr.split(", ");

        String iconPath = "iconPath";
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(arr[LAT]), Double.parseDouble(arr[LNG])))
                .title(arr[TITLE])
                .snippet(arr[SNIPPET])
                .icon(BitmapDescriptorFactory.defaultMarker()));

        mMarkers.add(markerToString(marker, iconPath));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(mMyLocationListener);
    }

    @Override
    protected void onDestroy() {
//        Log.d(TAG, TAG + "#onDestroy");
        super.onDestroy();
        writeSharedPref();
    }

    private void writeSharedPref() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putStringSet(KEY_MAP_MARKERS, (Set) mMarkers);
        editor.apply();
        Log.d(TAG, TAG + "#writeSharedPref" + mMarkers.size());
    }
}
