package hu.ait.weatherinfo;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WeatherPlace extends RealmObject {

    private String placeName;
    private double latitude;
    private double longitude;

    private int current_temp;
    private String description;

    private String main;
    private int min_temp;
    private int max_temp;

    private int hist_max_temp = -123;
    private int humidity;
    private long sunrise;
    private long sunset;
    private String iconID;

    @PrimaryKey
    private String placeID;

    public WeatherPlace () {}

    public WeatherPlace (String placeName) {
        setPlaceName(placeName);
    }


    ////////////////////////////////////////////////////////////////////////////
    ///////////////               GETTERS & SETTERS                  ///////////
    ////////////////////////////////////////////////////////////////////////////

    public int getCurrent_temp() {
        return current_temp;
    }

    public void setCurrent_temp(int current_temp) {
        this.current_temp = current_temp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMin_temp() {
        return min_temp;
    }

    public void setMin_temp(int min_temp) {
        this.min_temp = min_temp;
    }

    public int getMax_temp() {
        return max_temp;
    }

    public void setMax_temp(int max_temp) {
        this.max_temp = max_temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public Long getSunrise() {
        return sunrise;
    }

    public void setSunrise(Long sunrise) {
        this.sunrise = sunrise;
    }

    public Long getSunset() {
        return sunset;
    }

    public void setSunset(Long sunset) {
        this.sunset = sunset;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getIconID() {
        return iconID;
    }

    public void setIconID(String iconID) {
        this.iconID = iconID;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public int getHist_max_temp() {
        return hist_max_temp;
    }

    public void setHist_max_temp(int hist_max_temp) {
        this.hist_max_temp = hist_max_temp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
