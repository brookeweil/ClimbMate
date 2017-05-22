
package hu.ait.weatherinfo.almanacdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WUndergroundData {

    @SerializedName("response")
    @Expose
    private Response response;
    @SerializedName("almanac")
    @Expose
    private Almanac almanac;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Almanac getAlmanac() {
        return almanac;
    }

    public void setAlmanac(Almanac almanac) {
        this.almanac = almanac;
    }

}
