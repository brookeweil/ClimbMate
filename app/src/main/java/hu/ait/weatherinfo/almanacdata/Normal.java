
package hu.ait.weatherinfo.almanacdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Normal {

    @SerializedName("F")
    @Expose
    private String f;
    @SerializedName("C")
    @Expose
    private String c;

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

}
