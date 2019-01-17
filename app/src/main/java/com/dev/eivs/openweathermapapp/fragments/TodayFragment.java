package com.dev.eivs.openweathermapapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.dev.eivs.openweathermapapp.model.WheatherResult;
import com.dev.eivs.openweathermapapp.retrofit.ApiServise;
import com.dev.eivs.openweathermapapp.retrofit.RetrofitClient;
import com.dev.eivs.openweathermapapp.storage.Storage;
import com.squareup.picasso.Picasso;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import static com.dev.eivs.openweathermapapp.InternetConnection.checkUpdate;
import static com.dev.eivs.openweathermapapp.InternetConnection.checkWIFI;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment {

    ImageView img_weather;
    TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_temperature, txt_description,
             txt_date_time, txt_wind, txt_geo_coord;
    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    ApiServise mService;

    static TodayFragment instance;

    public static TodayFragment getInstance() {
        if (instance == null)
            instance = new TodayFragment();
        return instance;
    }

    public TodayFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(ApiServise.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemView = inflater.inflate(R.layout.fragment_today, container, false);

        img_weather = (ImageView) itemView.findViewById(R.id.img_weather);

        txt_city_name = (TextView) itemView.findViewById(R.id.txt_city_name);
        txt_humidity = (TextView) itemView.findViewById(R.id.txt_humidity);
        txt_sunrise = (TextView) itemView.findViewById(R.id.txt_sunrise);
        txt_sunset = (TextView) itemView.findViewById(R.id.txt_sunset);
        txt_pressure = (TextView) itemView.findViewById(R.id.txt_pressure);
        txt_temperature = (TextView) itemView.findViewById(R.id.txt_temperature);
        txt_description = (TextView) itemView.findViewById(R.id.txt_description);
        txt_date_time = (TextView) itemView.findViewById(R.id.txt_date_time);
        txt_wind = (TextView) itemView.findViewById(R.id.txt_wind);
        txt_geo_coord = (TextView) itemView.findViewById(R.id.txt_geo_coord);

        weather_panel = (LinearLayout) itemView.findViewById(R.id.weather_panel);
        loading = (ProgressBar) itemView.findViewById(R.id.loading);

        getWeatherInformation();

        return itemView;
    }

    private void getWeatherInformation() {
        if (checkUpdate&&InternetConnection.checkConnection(getContext())) {
            if(checkWIFI && InternetConnection.checkWifiOnAndConnected(getContext())){
                loadInfo();
              }
            else if(checkWIFI && !InternetConnection.checkWifiOnAndConnected(getContext())){
                Toast.makeText(getContext(),"disabled wifi",Toast.LENGTH_LONG).show();
            }
            else if(!checkWIFI){
                loadInfo();
            }
        }
        else {
            Toast.makeText(getContext(),"internet disconnected",Toast.LENGTH_LONG).show();
        }
    }

    private void loadInfo() {
        compositeDisposable.add(mService.getWheatherByLatLng(String.valueOf(Storage.current_location.getLatitude()),
                String.valueOf(Storage.current_location.getLongitude()),
                Storage.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WheatherResult>() {
                               @Override
                               public void accept(WheatherResult wheatherResult) throws Exception {

                                   Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                           .append(wheatherResult.getWeather().get(0).getIcon())
                                           .append(".png").toString()).into(img_weather);

                                   txt_city_name.setText(wheatherResult.getName());
                                   txt_description.setText(new StringBuilder("Weather in ")
                                           .append(wheatherResult.getName()).toString());
                                   txt_temperature.setText(new StringBuilder(String.valueOf(wheatherResult.getMain().getTemp()))
                                           .append("°C").toString());
                                   txt_date_time.setText(Storage.convertUnixToDate(wheatherResult.getDt()));
                                   txt_pressure.setText(new StringBuilder(String.valueOf(wheatherResult.getMain().getPressure())).append(" hpa").toString());
                                   txt_humidity.setText(new StringBuilder(String.valueOf(wheatherResult.getMain().getHumidity())).append(" %").toString());
                                   txt_sunrise.setText(Storage.convertUnixToHour(wheatherResult.getSys().getSunrise()));
                                   txt_sunset.setText(Storage.convertUnixToHour(wheatherResult.getSys().getSunset()));
                                   txt_geo_coord.setText(new StringBuilder(wheatherResult.getCoord().toString()).toString());

                                   weather_panel.setVisibility(View.VISIBLE);
                                   loading.setVisibility(View.GONE);

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                )

        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

}
