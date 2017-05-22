package hu.ait.weatherinfo;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;


public class WeatherRowAdapter extends RecyclerView.Adapter<WeatherRowAdapter.ViewHolder> {

    public static final int UPDATE = 666;
    public static final String KEYNAME = "KEYNAME";
    private static final double METERSperMILE = 1609.34;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvPlaceName;
        public TextView tvDetails;
        public TextView tvDistance;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
            tvDetails = (TextView) itemView.findViewById(R.id.tvDetails);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
        }
    }

    private List<WeatherPlace> placesList;
    private Context context;

    public WeatherRowAdapter(List<WeatherPlace> itemList, Context context) {
        this.placesList = itemList;
        this.context = context;
    }

    @Override
    public WeatherRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.weather_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(WeatherRowAdapter.ViewHolder holder, int position) {

        final WeatherPlace thisPlace = placesList.get(position);

        holder.tvPlaceName.setText(thisPlace.getPlaceName());
        holder.tvDetails.setText(thisPlace.getDescription());
        holder.tvDetails.setTextColor(context.getColor(R.color.colorText));

        setCustomText(holder, thisPlace);
        setupCardviewListener(holder, thisPlace);
        setDistanceText(holder, thisPlace);
        setAnimation(holder.itemView);
    }

    private void setupCardviewListener(ViewHolder holder, final WeatherPlace thisPlace) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStart = new Intent(context, ShowWeatherDetailsActivity.class);
                intentStart.putExtra(KEYNAME, (thisPlace.getPlaceName()));
                ((PlacesListActivity)context).startActivityForResult(intentStart, UPDATE);
            }
        });
    }

    private void setDistanceText(ViewHolder holder, WeatherPlace thisPlace) {
        double distance = getDistanceFromMe(thisPlace.getLatitude(), thisPlace.getLongitude());
        if (distance > 0)
            holder.tvDistance.setText(Double.toString(distance) + context.getString(R.string.miles_away_text));
    }

    private void setCustomText(ViewHolder holder, WeatherPlace thisPlace) {
        if (thisPlace.getMain() != null && thisPlace.getMain().equals(context.getString(R.string.rain))){
            holder.tvDetails.setTextColor(context.getResources().getColor(R.color.colorBad));
            holder.tvDetails.setText(holder.tvDetails.getText().toString() + " :(");
        }
        else if (Math.abs(thisPlace.getMax_temp() -
                          ((PlacesListActivity)context).getPreferredTemp()) < 5) {
            holder.tvDetails.setText(R.string.ideal_txt);
            holder.tvDetails.setTextColor(context.getResources().getColor(R.color.colorGood));
        }
        else if (thisPlace.getMax_temp() < thisPlace.getHist_max_temp()){
            holder.tvDetails.setText(R.string.cooler_txt);
            holder.tvDetails.setTextColor(context.getResources().getColor(R.color.colorGood));
        }
    }

    private double getDistanceFromMe(double lat, double lon) {

        Location myLocation = ((PlacesListActivity)context).getMyLocation();
        Location thisLocation = new Location("this place");
        thisLocation.setLatitude(lat);
        thisLocation.setLongitude(lon);

        double distance = -1;

        if (myLocation != null) {
            distance = myLocation.distanceTo(thisLocation);
            distance = distance / METERSperMILE;
            DecimalFormat newFormat = new DecimalFormat("#.#");
            distance = Double.valueOf(newFormat.format(distance));
        }

        Log.d("DIST", "getDistanceFromMe: distance is: " + distance);

        return distance;
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public void addItem(WeatherPlace place) {
        placesList.add(place);
        notifyDataSetChanged();
    }

    public void removeItem(int index) {

        ((PlacesListActivity)context).deleteItem(placesList.get(index));
        placesList.remove(index);
        notifyItemRemoved(index);
    }

    public void swapItems(int oldPosition, int newPosition) {
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(placesList, i, i + 1);
            }
        } else {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(placesList, i, i - 1);
            }
        }
        notifyItemMoved(oldPosition, newPosition);
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
    }

}
