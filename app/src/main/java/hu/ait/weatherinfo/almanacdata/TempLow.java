
package hu.ait.weatherinfo.almanacdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TempLow {

    @SerializedName("normal")
    @Expose
    private Normal_ normal;
    @SerializedName("record")
    @Expose
    private Record_ record;
    @SerializedName("recordyear")
    @Expose
    private String recordyear;

    public Normal_ getNormal() {
        return normal;
    }

    public void setNormal(Normal_ normal) {
        this.normal = normal;
    }

    public Record_ getRecord() {
        return record;
    }

    public void setRecord(Record_ record) {
        this.record = record;
    }

    public String getRecordyear() {
        return recordyear;
    }

    public void setRecordyear(String recordyear) {
        this.recordyear = recordyear;
    }

}
