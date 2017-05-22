
package hu.ait.weatherinfo.almanacdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Features {

    @SerializedName("almanac")
    @Expose
    private Integer almanac;

    public Integer getAlmanac() {
        return almanac;
    }

    public void setAlmanac(Integer almanac) {
        this.almanac = almanac;
    }

}
