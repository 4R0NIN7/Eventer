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
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    protected ArrayList<Wydarzenie> myEvents = new ArrayList<>();
    protected ArrayList<Wydarzenie> recommendedEvents = new ArrayList<>();
    ArrayList<Wydarzenie> allEvents = new ArrayList<>();



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

    private String login="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchText = findViewById(R.id.input_search);
        markersDelete = findViewById(R.id.markers_delete);
        goBack = findViewById(R.id.back);
        isServiceOk();
        enableRuntimePermission();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); //lokacja
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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

        Bundle loginBundle = getIntent().getExtras();
        if(loginBundle != null){
            login = loginBundle.getString("login");


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

        /*

         */
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        /*
        Zapis do wydarzenia
         */
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });
        /*
        Rezygnacja z uczestnictwa
         */
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {

            }
        });


        /*
        Na klikniecie i przytrzymanie
         */
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Intent intent = new Intent(MapsActivity.this, WydarzenieActivity.class);
                Bundle bundle = new Bundle();
                double lat, lng;
                lat = latLng.latitude;
                lng = latLng.longitude;
                bundle.putDouble("lat", lat);
                bundle.putDouble("lng", lng);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        new Mapa_WydarzeniaTimer(10000, 1000).callTimer();
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
    //Odświeżanie danych


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
                //deleteMarkersFromMap(mMap);
                mMap.clear();
                setMarkersFromEvent(allEvents);
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MapsActivity.this, EnterActivity.class);
                //startActivity(intent);
                finish();
            }
        });
        //Tutaj lista markerów


        //setMarkersFromEvent();

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
            markers.add(marker);
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
    /*
    Dodaje markery z wydarzeniami
     */
    private void setMarkersFromEvent(ArrayList<Wydarzenie> wydarzenia){
        if(wydarzenia.size() >0){
            for (Wydarzenie w: wydarzenia) {
                MarkerOptions options = new MarkerOptions()
                        .position(w.coordinates)
                        .title(w.EventName);
                marker = mMap.addMarker(options);
                markers.add(marker);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wydarzenia.get(0).coordinates, DEFAULT_ZOOM));
        }

    }





    private class Wydarzenie{
        int id;
        LatLng coordinates;
        String adminLogin;
        Date beginDate;
        Date endDate;
        String EventName;

        public Wydarzenie(String id, String Lat, String Lon, String Admin, String begin, String end, String name){
            this.id = Integer.parseInt(id);
            coordinates = new LatLng(Double.parseDouble(Lat), Double.parseDouble(Lon));
            adminLogin = Admin;
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            beginDate = new Date();
            endDate = new Date();
            try {
                beginDate = format.parse(begin);
                endDate = format.parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        };

    }
    private class GetMyEventsTask extends AsyncTask<String, Void, Object> {

        String mlogin;


        public GetMyEventsTask(String me){
            mlogin = me;
            //Log.d("godzilla", "new task "+me);

        }


        @Override
        protected Object doInBackground(String... strings) {
            //Log.d("godzilla", "new background");
            OkHttpClient client = new OkHttpClient();
            Request req;

            req = new Request.Builder().url(getResources().getString(R.string.apiUrl)+"/api/Wydarzenie/moje-wydarzenia/"+mlogin).build();
            //Log.d("godzila", "asking "+getResources().getString(R.string.apiUrl)+"/api/Wydarzenie/moje-wydarzenia/"+mlogin);
            Response res = null;
            try{
                res = client.newCall(req).execute();
                return res.body().string();
            }catch(IOException exception){
                exception.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            try {
                if(o != null){
                    //Log.d("godzilla", "getting response");
                    JSONArray json = new JSONArray(o.toString());
                    //Log.d("godzilla", "response: "+json);
                    myEvents.clear();
                    for(int i = 0; i<json.length(); i++){
                        JSONObject userJson = json.getJSONObject(i);
                        String lat = userJson.getString("szerokosc");
                        String lon = userJson.getString("wysokosc");

                        String login_organizatora = userJson.getString("login_organizatora");
                        String data_poczatku = userJson.getString("data_poczatku");
                        String data_konca = userJson.getString("data_konca");
                        String nazwa_wydarzenia = userJson.getString("nazwa_wydarzenia");
                        String id = userJson.getString("id");
                        //Log.d("godzilla", nazwa_wydarzenia+": "+lat+","+lon+": "+login_organizatora+": "+data_poczatku+","+data_konca);
                        //listItemsTwojeWydarzenia.add(id+": "+nazwa_wydarzenia+": "+lat+","+lon+": "+login_organizatora+": "+data_poczatku+": "+data_konca);
                        try{
                            Wydarzenie w = new Wydarzenie(id, lat, lon, login_organizatora, data_poczatku, data_konca, nazwa_wydarzenia);
                            myEvents.add(w);
                        }catch(Exception e){

                        }

                    }




                }
            } catch (JSONException e) {

            }
        }
    }
    private class GetRecommendedEventsTask extends AsyncTask<String, Void, Object> {

        String mlogin;


        public GetRecommendedEventsTask(String me){
            mlogin = me;

        }


        @Override
        protected Object doInBackground(String... strings) {


            OkHttpClient client = new OkHttpClient();
            Request req;

            req = new Request.Builder().url(getResources().getString(R.string.apiUrl)+"/api/Wydarzenie/proponowane-wydarzenia/"+mlogin).build();
            Response res = null;
            try{
                res = client.newCall(req).execute();
                return res.body().string();
            }catch(IOException exception){
                exception.printStackTrace();
            }

            return null;
        }



        @Override
        protected void onPostExecute(Object o) {
            try {
                if(o != null){
                    //("godzilla", "getting response");
                    JSONArray json = new JSONArray(o.toString());
                    recommendedEvents.clear();
                    for(int i = 0; i<json.length(); i++){
                        JSONObject userJson = json.getJSONObject(i);
                        String lat = userJson.getString("szerokosc");
                        String lon = userJson.getString("wysokosc");

                        String login_organizatora = userJson.getString("login_organizatora");
                        String data_poczatku = userJson.getString("data_poczatku");
                        String data_konca = userJson.getString("data_konca");
                        String nazwa_wydarzenia = userJson.getString("nazwa_wydarzenia");
                        String id = userJson.getString("id");

                        //listItemsWydarzeniaWPoblizu.add(id+": "+nazwa_wydarzenia+": "+lat+","+lon+": "+login_organizatora+": "+data_poczatku+": "+data_konca);
                        try{
                            Wydarzenie w = new Wydarzenie(id, lat, lon, login_organizatora, data_poczatku, data_konca, nazwa_wydarzenia);
                            recommendedEvents.add(w);
                        }catch(Exception e){

                        }

                    }




                }
            } catch (JSONException e) {

            }
        }
    }
    public class Mapa_WydarzeniaTimer {
        protected long period; //mili sekundy
        protected int delay;
        protected final Timer myTimer = new Timer();

        public Mapa_WydarzeniaTimer(long period, int delay){
            this.period = period;
            this.delay = delay;
        }
        private void callTimer()
        {

            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new GetMyEventsTask(login).execute("");
                    new GetRecommendedEventsTask(login).execute();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    allEvents.clear();
                    allEvents.addAll(myEvents);
                    allEvents.addAll(recommendedEvents);
                    //Log.d("godzilla", "all events: "+allEvents.size());
                    //setMarkersFromEvent(allEvents);

                }
            }, delay,period);

        }
    }
}
