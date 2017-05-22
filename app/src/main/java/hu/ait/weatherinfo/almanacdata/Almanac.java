
package hu.ait.weatherinfo.almanacdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Almanac {

    @SerializedName("airport_code")
    @Expose
    private String airportCode;
    @SerializedName("temp_high")
    @Expose
    private TempHigh tempHigh;
    @SerializedName("temp_low")
    @Expose
    private TempLow tempLow;

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public TempHigh getTempHigh() {
        return tempHigh;
    }

    public void setTempHigh(TempHigh tempHigh) {
        this.tempHigh = tempHigh;
    }

    public TempLow getTempLow() {
        return tempLow;
    }

    public void setTempLow(TempLow tempLow) {
        this.tempLow = tempLow;
    }

}
