package hu.ait.weatherinfo;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;

public class ShowWeatherDetailsActivity extends FragmentActivity
                                        implements OnMapReadyCallback,
                                                   MapLocationManager.OnNewLocationAvailable {

    private static final String imageURL     = "http://openweathermap.org/img/w/";
    private static final String URLextension = ".png";

    private MapLocationManager mapLocationManager;
    private WeatherPlace placeToShow;
    private GoogleMap mMap;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_details);

        findPlaceAndSetName();
        setDetailsText();
        setGlideImage();
        setupMap();
    }

    private void setupMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void findPlaceAndSetName() {
        String placeName = getIntent().getSerializableExtra(WeatherRowAdapter.KEYNAME).toString();
        ((TextView)findViewById(R.id.tvPlaceNameDetail)).setText(placeName);
        placeToShow = getRealm().where(WeatherPlace.class)
                .equalTo("placeName", placeName)
                .findFirst();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng thisPlace = new LatLng(placeToShow.getLatitude(), placeToShow.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(thisPlace)
                .title(placeToShow.getPlaceName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(thisPlace));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        requestLocationPermission();
    }

    private void setGlideImage() {
        ImageView icon = (ImageView) findViewById(R.id.ivWeatherIcon);
        Glide.with(this).load(imageURL + placeToShow.getIconID() + URLextension).into(icon);
    }

    public void setDetailsText() {
        ((TextView)findViewById(R.id.tvDescription)).setText(placeToShow.getDescription());
        ((TextView)findViewById(R.id.tvCurrentTemp)).setText(Integer.toString(placeToShow.getCurrent_temp()) + "°");
        ((TextView)findViewById(R.id.tvMaxTemp)).setText(Integer.toString(placeToShow.getMax_temp()) + "°");
        ((TextView)findViewById(R.id.tvMinTemp)).setText(Integer.toString(placeToShow.getMin_temp()) + "°");
        ((TextView)findViewById(R.id.tvHumidity)).setText(getString(R.string.humidity)
                                                          + Integer.toString(placeToShow.getHumidity())
                                                          + "%");
        ((TextView)findViewById(R.id.tvSunrise)).setText(getString(R.string.sunrise) + getSunriseTime());
        ((TextView)findViewById(R.id.tvSunset)).setText( getString(R.string.sunset) + getSunsetTime());
        if (placeToShow.getHist_max_temp() > 0) {
            ((TextView) findViewById(R.id.tvHistMax)).setText(getString(R.string.hist_max_txt)
                    + Integer.toString(placeToShow.getHist_max_temp()) + "°");
        } else {
            ((TextView) findViewById(R.id.tvHistMax)).setText(getString(R.string.hist_max_txt)
                    + " -- " + "°");
        }
    }

    @NonNull
    private String getSunriseTime () {
        String dateSunRise = new java.text.SimpleDateFormat("HH:mm").format(
                new java.util.Date(placeToShow.getSunrise() * 1000));
        return dateSunRise.toString();
    }

    @NonNull
    private String getSunsetTime () {
        String dateSunRise = new java.text.SimpleDateFormat("HH:mm").format(
                new java.util.Date(placeToShow.getSunset() * 1000));
        return dateSunRise.toString();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // no permissions, do nothing
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
        } else {
            // got permissions
            mapLocationManager = new MapLocationManager(this);
            mapLocationManager.startLocationMonitoring(this);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // start our job
                mapLocationManager = new MapLocationManager(this);
                mapLocationManager.startLocationMonitoring(this);
                mMap.setMyLocationEnabled(true);
            } else {
                //no permission, do nothing
            }
        }
    }

    @Override
    public void onNewLocation(Location location) {

    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmItems();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapLocationManager.stopLocationMonitorinig();
    }


}
