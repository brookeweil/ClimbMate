package hu.ait.weatherinfo.network;


public interface WeatherResultListener {

    public void weatherResultArrived(String rawJson);

}
