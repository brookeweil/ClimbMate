package hu.ait.weatherinfo;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

public class AddFromMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double usaCenterLat = 37.75;
    private double usaCenterLon = -97.13;
    Button btnSave;
    Button btnCancel;
    LatLng newPlace;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_from_map);

        setupButtons();
        setupMap();
    }

    private void setupMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mapAdd);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(usaCenterLat, usaCenterLon)));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                newPlace = latLng;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(newPlace));
                Log.i("ADD", "onMapClick: location is " + latLng);
            }
        });
    }

    private void setupButtons() {
        btnSave = (Button) findViewById(R.id.btnMapOk);
        btnCancel = (Button) findViewById(R.id.btnMapCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlace();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void cancel() {
        Intent intentResult = new Intent();
        setResult(CreatePlaceActivity.RESULT_CANCEL, intentResult);
        finish();
    }

    private void savePlace() {

        Intent intentResult = new Intent();
        DecimalFormat newFormat = new DecimalFormat("#.###");
        double lat =  Double.valueOf(newFormat.format(newPlace.latitude));
        double lon =  Double.valueOf(newFormat.format(newPlace.longitude));


        intentResult.putExtra(CreatePlaceActivity.NEWLAT, Double.toString(lat));
        intentResult.putExtra(CreatePlaceActivity.NEWLON, Double.toString(lon));
        setResult(CreatePlaceActivity.RESULT_OK, intentResult);

        finish();
    }
}
