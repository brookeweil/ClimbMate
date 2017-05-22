
package hu.ait.weatherinfo.almanacdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TempHigh {

    @SerializedName("normal")
    @Expose
    private Normal normal;
    @SerializedName("record")
    @Expose
    private Record record;
    @SerializedName("recordyear")
    @Expose
    private String recordyear;

    public Normal getNormal() {
        return normal;
    }

    public void setNormal(Normal normal) {
        this.normal = normal;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public String getRecordyear() {
        return recordyear;
    }

    public void setRecordyear(String recordyear) {
        this.recordyear = recordyear;
    }

}
