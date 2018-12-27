package com.example.r0nin.eventer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AutoCompleteTextView searchText; //wyszukiwanie
    private ImageView markersDelete, goBack;
    private LocationManager locationManager;
    private Boolean gpsEnabled = false; // gps włączony
    private Boolean internetEnabled = false; // internety som
    private Boolean permissionsGranted = false; //ustawienie permisji na ture
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Location currentLocation;
    private static final int ERROR_DIALOG_REQUEST = 9001; //if Services are turned off
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSIONS_REQUEST = 4321; //if locations permissions are not enabled
    protected static ArrayList<Marker> markers = new ArrayList<>();




    /*
    Jak bedziesz chcial dodac zmiane polozenia na mapie twojej lokacji to odkomentuj to i w metodzie onMapReady
    private static final int PROXIMITY_RADIUS = 10000; //10 km
    private static final int INTERVAL = 120000;//for every 2 minutes
    private static final int FASTEST_INTERVAL = 5000;  //for every 30 sec if it's sooner
    private static final int SMALLEST_DISPLACEMENT = 100; //for every x meters the locationCallback will go

     */

    // do lokalizacji klienta
    // do wyszukiwania punktu po nazwie
    protected GeoDataClient geoDataClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    //ustawienie tak granic wyszukania miejsc, żeby najpierw polske wyszukiwało
    //bez tego wyszukiwałoby w stanach bodajże domyślnie
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(49.299181, 19.949562), new LatLng(52.229676, 21.012229));
    private PlacesInfo placesInfo;
    private Marker marker;
    private static final float DEFAULT_ZOOM = 15; //Default zoom for camera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchText = findViewById(R.id.input_search);
        markersDelete = findViewById(R.id.markers_delete);
        goBack = findViewById(R.id.back);
        isServiceOk();
        enableRuntimePermission();
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, this.getText(R.string.gpsEnabled), Toast.LENGTH_SHORT).show();
            gpsEnabled = true;
        }else{
            showGPSDisabledAlertToUser();
        }
        if(haveNetworkConnection()){
            Toast.makeText(this, this.getText(R.string.internetEnabled), Toast.LENGTH_SHORT).show();
            internetEnabled = true;
        }else {
            showInternetDisabledAlertToUser();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if(gpsEnabled && internetEnabled) {
            Toast.makeText(this, this.getText(R.string.mapRead), Toast.LENGTH_SHORT).show();
        }
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        /*

        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL); //for every 2 minutes
        locationRequest.setFastestInterval(FASTEST_INTERVAL); //for every 1 minute if it's sooner
        locationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT); //for every 100 meters the locationCallback will go
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        */
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        init();
    }

    //sprawdza czy ma dostep do google
    private boolean isServiceOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);
        if(available == ConnectionResult.SUCCESS){
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, this.getString(R.string.errorGoogleServices), Toast.LENGTH_SHORT).show();

        }
        return false;
    }
    //sprawdza czy sa odpalone permisje (od arciucha wzialem)
    private void enableRuntimePermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = true;
                initializeMap();
            }
        }else{
            ActivityCompat.requestPermissions(this, new String[]{FINE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);
            ActivityCompat.requestPermissions(this, new String[]{COARSE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);
        }
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }



    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = new NetworkInfo[0];
        if (cm != null) {
            netInfo = cm.getAllNetworkInfo();
        }
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }



    private void showInternetDisabledAlertToUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getText(R.string.internetDisabled))
                .setTitle("INTERNET")
                .setCancelable(false)
                .setPositiveButton(this.getText(R.string.tryToInternetEnabled),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                startActivity(i);
                            }
                        }
                )
                .setNegativeButton(this.getText(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MapsActivity.this.finish();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Check if GPS is disabled
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(this.getText(R.string.gpsDisabled))
                .setTitle("GPS")
                .setCancelable(false)
                .setPositiveButton(this.getText(R.string.tryToGpsEnabled),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(this.getText(R.string.cancel),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    //usuwanie markerów
    private void deleteMarkersFromMap(GoogleMap mMap){
        mMap.clear();
        Toast.makeText(MapsActivity.this, MapsActivity.this.getText(R.string.markersDelete).toString(), Toast.LENGTH_SHORT).show();
    }

    //pobranie lokacji urządzenia
    private void getDeviceLocation() {
        try {
            if (permissionsGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            currentLocation = (Location) task.getResult();
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            //niżej masz przemieszczanie kamery do punktu tam gdzie jesteś
                            //moveCamera(latLng, DEFAULT_ZOOM,MapsActivity.this.getText(R.string.currentPosition).toString());
                        } else {
                            Toast.makeText(MapsActivity.this, MapsActivity.this.getText(R.string.errorCurrentLocation), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException ex) {
        }
    }

    //chowa klawiature
    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
            }
        }catch (NullPointerException ex){
        }
    }

    //inicjalizacja widgetow
    private void init() {
        geoDataClient = Places.getGeoDataClient(this);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, geoDataClient, LAT_LNG_BOUNDS, null);
        searchText.setAdapter(placeAutocompleteAdapter);
        searchText.setOnItemClickListener(searchTextClickListener);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }

                return false;
            }
        });
        markersDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMarkersFromMap(mMap);
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, EnterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void geoLocate(){
        String searchString = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
        }

        if(list.size() > 0){
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(),
                    address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }
    //przesuwanie kamery
    private void moveCamera(LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(!title.equals(MapsActivity.this.getText(R.string.currentPosition).toString())){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            marker = mMap.addMarker(options);
        }
        hideKeyboard();
    }

    //przesuwanie kamery
    private void moveCamera(LatLng latLng, float zoom, PlacesInfo placesInfo) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.clear();
        mMap.setInfoWindowAdapter(new PlacesInfoAdapter(MapsActivity.this));
        if(placesInfo != null){
            try{
                String snippet = getNotNullData(placesInfo);
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placesInfo.getName())
                        .snippet(snippet);
                marker = mMap.addMarker(options);
            }catch (NullPointerException ex){

            }
        }else{
            marker = mMap.addMarker(new MarkerOptions().position(latLng));
        }
        hideKeyboard();
    }

    private String getNotNullData(PlacesInfo placesInfo){
        String notNullString;
        String address, coordinates,  rating, distance;
        coordinates = ("Lat: " + placesInfo.getLatLng().latitude + " Lng: " + placesInfo.getLatLng().longitude + " " + "\n");
        notNullString =coordinates;
        return notNullString;
    }


    //metody do wyszukiwania miejsc
    private AdapterView.OnItemClickListener searchTextClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard();
            final AutocompletePrediction itemFromPlaceAutocompleteAdapter = placeAutocompleteAdapter.getItem(position);
            final String placeId = itemFromPlaceAutocompleteAdapter != null ? itemFromPlaceAutocompleteAdapter.getPlaceId() : null;
            final CharSequence primaryText = itemFromPlaceAutocompleteAdapter != null ? itemFromPlaceAutocompleteAdapter.getPrimaryText(null) : null;
            Task<PlaceBufferResponse> placeResult = geoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(placeBufferResponseOnCompleteListener);
            hideKeyboard();
        }
    };

    private OnCompleteListener <PlaceBufferResponse> placeBufferResponseOnCompleteListener = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
            hideKeyboard();
            PlaceBufferResponse places = task.getResult();
            final Place place = places.get(0);
            try {
                placesInfo = new PlacesInfo();
                placesInfo.setName(place.getName().toString());
                placesInfo.setAddress(Objects.requireNonNull(place.getAddress()).toString());
                placesInfo.setId(place.getId());
                placesInfo.setLatLng(place.getLatLng());
                placesInfo.setRating(place.getRating());
                float[] distance = new float[1];
                Location.distanceBetween(currentLocation.getLatitude(),currentLocation.getLongitude(),place.getLatLng().latitude, place.getLatLng().longitude,
                        distance);
                String string_temp = Float.valueOf(distance[0]).toString();
                String string_form = string_temp.substring(0,string_temp.indexOf('.'));
                float distanceB = Float.parseFloat(string_form);
                placesInfo.setDistance(distanceB);
            }catch(NullPointerException ex){
            }
            moveCamera(new LatLng(Objects.requireNonNull(place.getViewport()).getCenter().latitude,place.getViewport().getCenter().longitude),DEFAULT_ZOOM, placesInfo);
            places.release();
            searchText.setText("");
        }
    };


}
