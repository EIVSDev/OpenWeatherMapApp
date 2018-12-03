package com.dev.eivs.openweathermapapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.eivs.openweathermapapp.R;
import com.dev.eivs.openweathermapapp.model.WeatherForecastResult;
import com.dev.eivs.openweathermapapp.storage.Storage;
import com.squareup.picasso.Picasso;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.MyViewHolder> {
    Context context;
    WeatherForecastResult weatherForecastResult;

    public ForecastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_weathr_forecast, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherForecastResult.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.img_weather1);

        holder.txt_date_time1.setText(new StringBuilder(Storage.convertUnixToDate(weatherForecastResult
                .list.get(position).dt)));

        holder.txt_description1.setText(new StringBuilder(weatherForecastResult.list.get(position)
                .weather.get(0).getDescription()));

        holder.txt_temperature1.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position)
                .main.getTemp())).append("°C"));
    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_date_time1, txt_description1, txt_temperature1;
        ImageView img_weather1;

        public MyViewHolder(View itemView) {
            super(itemView);

            img_weather1 = (ImageView) itemView.findViewById(R.id.img_weather11);
            txt_date_time1 = (TextView) itemView.findViewById(R.id.txt_date);
            txt_description1 = (TextView) itemView.findViewById(R.id.txt_description);
            txt_temperature1 = (TextView) itemView.findViewById(R.id.txt_temperature);
        }
    }
}
