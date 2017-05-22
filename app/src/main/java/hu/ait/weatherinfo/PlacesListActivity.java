package hu.ait.weatherinfo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import hu.ait.weatherinfo.almanacdata.WUndergroundData;
import hu.ait.weatherinfo.network.AlmanacResultListener;
import hu.ait.weatherinfo.network.AsyncTaskAlmanacGet;
import hu.ait.weatherinfo.network.AsyncTaskHttpGet;
import hu.ait.weatherinfo.network.WeatherResultListener;
import hu.ait.weatherinfo.resultdata.WeatherData;
import io.realm.Realm;
import io.realm.RealmResults;



public class PlacesListActivity extends AppCompatActivity implements WeatherResultListener,
                                                                     AlmanacResultListener,
                                                                     MapLocationManager.OnNewLocationAvailable{

    static final String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?units=imperial";
    static final String WuUrl      = "http://api.wunderground.com/api/";
    static final String WuPrefix   = "/almanac/q/";
    static final String WuPostfix  = ".json";
    static final String apiPrefix  = "&appid=";
    static final String latPrefix  = "&lat=";
    static final String lonPrefix  = "&lon=";

    public static final int CREATE        = 111;
    public static final int RESULT_OK     = 222;
    public static final int RESULT_CANCEL = 333;
    public static final int SET_TEMP      = 444;
    public static final int TEMP_OK       = 555;

    public static final String NAME_ITEM  = "NAME_ITEM";
    public static final String LAT_ITEM   = "LAT_ITEM";
    public static final String LON_ITEM   = "LON_ITEM";
    public static final String NEW_TEMP   = "NEW_TEMP";
    public static final String CUR_TEMP   = "CUR_TEMP";
    private static final String TEMP_PREF = "TPREF";

    private FloatingActionButton btnAddPlace;
    private WeatherRowAdapter rowAdapter;
    private List<WeatherPlace> allPlaces;
    private DrawerLayout drawerLayout;
    private MapLocationManager mapLocationManager;
    private Location myLocation;
    private int preferredTemp  = 50;
    private int callsPerMinute = 0;
    private int lastCall;



    /////////////////////////////////////////////////////////////////////////////////////
    ///////////////////              Main Functions                    //////////////////
    /////////////////////////////////////////////////////////////////////////////////////

    private void getAllWeather() {

        for (WeatherPlace place : allPlaces) {

            sendGetWeatherRequest(place.getLatitude(), place.getLongitude());
            sendGetAlmanacRequest(place.getLatitude(), place.getLongitude());
        }
        rowAdapter.notifyDataSetChanged();
    }

    @Override
    public void weatherResultArrived(String rawJson) {

        if (!isJsonValid(rawJson)) return;

        Gson gson = new Gson();
        WeatherData weatherResult = gson.fromJson(rawJson, WeatherData.class);
        WeatherPlace placeToUpdate = getRealm().where(WeatherPlace.class)
                .equalTo("latitude", weatherResult.getCoord().getLat())
                .equalTo("longitude", weatherResult.getCoord().getLon())
                .findFirst();

        importJsonIntoWeatherPlace(weatherResult, placeToUpdate);
    }

    @Override
    public void almanacResultArrived(String rawJson) {

        String delims = "[~]";
        String[] tokens = rawJson.split(delims);
        double lat = Double.parseDouble(tokens[0]);
        double lon = Double.parseDouble(tokens[1]);
        String json = tokens[2];

        if (!isJsonValid(json)) return;

        Gson gson = new Gson();
        getRealm().beginTransaction();
        WUndergroundData weatherResult = gson.fromJson(json, WUndergroundData.class);
        WeatherPlace placeToUpdate = getRealm().where(WeatherPlace.class)
                .equalTo("latitude", lat)
                .equalTo("longitude", lon)
                .findFirst();
        if (!checkForNullPlace(placeToUpdate) && weatherResult != null &&
                weatherResult.getAlmanac().getTempHigh().getNormal().getF() != null) {
            placeToUpdate.setHist_max_temp(Integer.parseInt(weatherResult.getAlmanac().getTempHigh().getNormal().getF()));
        }
        getRealm().commitTransaction();
    }

    @Override
    public void onNewLocation(Location location) {
        myLocation = location;
//        Toast.makeText(this, "My location is: " + location, Toast.LENGTH_SHORT).show();
    }



    /////////////////////////////////////////////////////////////////////////////////////
    ///////////////////             Helper Functions                   //////////////////
    /////////////////////////////////////////////////////////////////////////////////////

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmItems();
    }

    private void sendGetWeatherRequest(double lat, double lon) {

        new AsyncTaskHttpGet(PlacesListActivity.this).execute(weatherUrl + apiPrefix  +
                getString(R.string.secret_open_weather_key) + latPrefix + lat + lonPrefix + lon );
    }

    private void sendGetAlmanacRequest(double lat, double lon) {

        if (!checkApiCallsOk()) return;

        new AsyncTaskAlmanacGet(PlacesListActivity.this).execute(WuUrl + getString(R.string.secret_wunderground_api_key)
                                                        + WuPrefix + lat + "," + lon + WuPostfix );
    }

    private boolean checkApiCallsOk() {

        callsPerMinute += 1;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-4:00"));
        int currentMinutes = cal.get(Calendar.MINUTE);

        if ((currentMinutes - lastCall) <= 1) {
            if (callsPerMinute > 8) {
                Toast.makeText(this, R.string.wait_api, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                callsPerMinute = 1;
            }
        }
        lastCall = currentMinutes;
        return true;
    }

    private boolean isJsonValid(String rawJson) {
        if (rawJson == "") {
            Toast.makeText(this, R.string.connection_error_text, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addPlace() {
        Intent intentStart = new Intent(PlacesListActivity.this, CreatePlaceActivity.class);
        startActivityForResult(intentStart, CREATE);
    }

    private void setPrefdTemp() {
        Intent intentStart = new Intent(PlacesListActivity.this, SetPrefsActivity.class);
        intentStart.putExtra(CUR_TEMP, Integer.toString(preferredTemp));
        startActivityForResult(intentStart, SET_TEMP);
    }

    public void deleteItem(WeatherPlace item) {
        getRealm().beginTransaction();;
        item.deleteFromRealm();
        getRealm().commitTransaction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {

            case RESULT_OK:
                String placeName = data.getStringExtra(NAME_ITEM);
                double placeLat  = Double.parseDouble(data.getStringExtra(LAT_ITEM));
                double placeLon  = Double.parseDouble(data.getStringExtra(LON_ITEM));
                createNewPlace(placeName, placeLat, placeLon);
                sendGetWeatherRequest(placeLat, placeLon);
                sendGetAlmanacRequest(placeLat, placeLon);
                rowAdapter.notifyDataSetChanged();
                break;

            case TEMP_OK:
                int newTemp = data.getIntExtra(NEW_TEMP, preferredTemp);
                preferredTemp = newTemp;
                updatePrefdTemp();
                rowAdapter.notifyDataSetChanged();
                break;

            case RESULT_CANCELED:
                break;
        }

    }

    private void updatePrefdTemp() {
        SharedPreferences settings = getSharedPreferences(TEMP_PREF, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("temp", preferredTemp);
        editor.commit();
    }

    private void createNewPlace(String placeName, double placeLat, double placeLon) {
        DecimalFormat newFormat = new DecimalFormat("#.##");
        placeLat =  Double.valueOf(newFormat.format(placeLat));
        placeLon =  Double.valueOf(newFormat.format(placeLon));

        getRealm().beginTransaction();
        WeatherPlace newPlace = getRealm().createObject(WeatherPlace.class,
                                                        UUID.randomUUID().toString());
        newPlace.setPlaceName(placeName);
        newPlace.setLatitude(placeLat);
        newPlace.setLongitude(placeLon);
        getRealm().commitTransaction();

        rowAdapter.addItem(newPlace);
    }

    private void importJsonIntoWeatherPlace(WeatherData weatherResult, WeatherPlace placeToUpdate) {

        if (checkForNullPlace(placeToUpdate)) return;

        getRealm().beginTransaction();
        placeToUpdate.setCurrent_temp((weatherResult.getMain().getTemp()).intValue());
        placeToUpdate.setMax_temp((weatherResult.getMain().getTempMax()).intValue());
        placeToUpdate.setMin_temp((weatherResult.getMain().getTempMin()).intValue());
        placeToUpdate.setDescription(weatherResult.getWeather().get(0).getDescription());
        placeToUpdate.setMain(weatherResult.getWeather().get(0).getMain());
        placeToUpdate.setHumidity(weatherResult.getMain().getHumidity());
        placeToUpdate.setSunrise(weatherResult.getSys().getSunrise());
        placeToUpdate.setSunset(weatherResult.getSys().getSunset());
        placeToUpdate.setIconID(weatherResult.getWeather().get(0).getIcon());
        getRealm().commitTransaction();
    }

    private boolean checkForNullPlace(WeatherPlace placeToUpdate) {
        if (placeToUpdate == null) {
            Log.e("ERROR", getString(R.string.error_find_text));
            return true;
        }
        return false;
    }

    public int getPreferredTemp() {
        return preferredTemp;
    }

    public Location getMyLocation() {
        return myLocation;
    }


    /////////////////////////////////////////////////////////////////////////////////////
    ///////////////////                   Setup                        //////////////////
    /////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);

        allPlaces = setupList();
        RecyclerView recyclerViewPlaces = setupRecycler(allPlaces);
        setupTouch(recyclerViewPlaces);
        getAllWeather();

        showWelcomeMessage();
        requestLocationPermission();
        restorePrefs();

        setupLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllWeather();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MainApplication)getApplication()).closeRealm();
        if (mapLocationManager != null)
            mapLocationManager.stopLocationMonitorinig();
    }

    @NonNull
    private List<WeatherPlace> setupList() {
        ((MainApplication)getApplication()).openRealm();
        RealmResults<WeatherPlace> allPlaces = getRealm().where(WeatherPlace.class).findAll();
        WeatherPlace itemsArray[] = new WeatherPlace[allPlaces.size()];
        return new ArrayList<WeatherPlace>
                (Arrays.asList(allPlaces.toArray(itemsArray)));
    }

    @NonNull
    private RecyclerView setupRecycler(List<WeatherPlace> placesResult) {
        rowAdapter = new WeatherRowAdapter(placesResult, this);
        RecyclerView recyclerViewPlaces = (RecyclerView) findViewById(
                R.id.RecyclerViewPlaces);
        recyclerViewPlaces.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPlaces.setAdapter(rowAdapter);
        return recyclerViewPlaces;
    }

    private void setupTouch(RecyclerView recyclerViewPlaces) {
        ItemListTouchHelperCallback touchHelperCallback = new ItemListTouchHelperCallback(
                rowAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);
        touchHelper.attachToRecyclerView(recyclerViewPlaces);
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.sun);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    private void showWelcomeMessage() {
        Toast toast = Toast.makeText(this, R.string.menu_instr_text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void setupLayout() {
        btnAddPlace = (FloatingActionButton) findViewById(R.id.btnAdd);
        btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlace();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_add:
                                addPlace();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_about:
                                Toast.makeText(PlacesListActivity.this,
                                               R.string.about_text, Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                            case R.id.action_prefs:
                                setPrefdTemp();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_refresh:
                                getAllWeather();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                        }
                        return false;
                    }
                });
        setUpToolBar();
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // start our job
                mapLocationManager = new MapLocationManager(this);
                mapLocationManager.startLocationMonitoring(this);
            } else {
                //got permissions
            }
        }
    }

    private void restorePrefs() {

        SharedPreferences settings = getSharedPreferences(TEMP_PREF, 0);

        int prefTemp = settings.getInt("temp", -1);
        if (prefTemp > 0)
            preferredTemp = prefTemp;
    }
}
