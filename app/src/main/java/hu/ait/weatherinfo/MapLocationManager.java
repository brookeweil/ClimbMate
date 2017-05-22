package hu.ait.weatherinfo;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

    public class MapLocationManager implements LocationListener {

        private android.location.LocationManager locationManager;

        public interface OnNewLocationAvailable {
            public void onNewLocation(Location location);
        }
        private OnNewLocationAvailable onNewLocationAvailable;

        public MapLocationManager(OnNewLocationAvailable onNewLocationAvailable) {
            this.onNewLocationAvailable = onNewLocationAvailable;
        }

        public void startLocationMonitoring(Context ctx) throws SecurityException {
            locationManager = (android.location.LocationManager) ctx.getSystemService(
                    Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER,
                    0, 0, this);
        }

        public void stopLocationMonitorinig() throws SecurityException {
            locationManager.removeUpdates(this);
        }

        @Override
        public void onLocationChanged(Location location) {
            onNewLocationAvailable.onNewLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

