package com.dev.eivs.openweathermapapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.eivs.openweathermapapp.adapter.ForecastAdapter;
import com.dev.eivs.openweathermapapp.model.WeatherForecastDB;
import com.dev.eivs.openweathermapapp.model.WeatherForecastResult;
import com.dev.eivs.openweathermapapp.retrofit.ApiServise;
import com.dev.eivs.openweathermapapp.retrofit.RetrofitClient;
import com.dev.eivs.openweathermapapp.storage.Storage;
import java.util.ArrayList;
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
public class ForecastFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    ApiServise mService;
    TextView txt_city_name, txt_geo_coord;
    RecyclerView recycler_forecast;


    static ArrayList<WeatherForecastDB> listBank;
    static ForecastFragment instance;

    public static ForecastFragment getInstance() {
        if (instance == null)
            instance = new ForecastFragment();
        return instance;
    }

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(ApiServise.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_forecast, container, false);

        txt_city_name = (TextView) itemView.findViewById(R.id.txt_city_name);
        txt_geo_coord = (TextView) itemView.findViewById(R.id.txt_geo_coord);

        recycler_forecast = (RecyclerView) itemView.findViewById(R.id.recycler_forecast);
        recycler_forecast.setHasFixedSize(true);
        recycler_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        listBank = new ArrayList<>();
        if (InternetConnection.checkConnection(getContext()))
            getForecastInformation();
        else ;
        return itemView;
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

    private void getForecastInformation() {
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
        compositeDisposable.add(mService.getForecastWeatherByLatLng(
                String.valueOf(Storage.current_location.getLatitude()),
                String.valueOf(Storage.current_location.getLongitude()),
                Storage.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {

                        displayForecastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("ERROR", "" + throwable.getMessage());
                    }
                })
        );
    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult) {

        txt_city_name.setText(new StringBuilder(weatherForecastResult.city.name));
        txt_geo_coord.setText(new StringBuilder(weatherForecastResult.city.coord.toString()));

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);
        ForecastAdapter adapter = new ForecastAdapter(getContext(), weatherForecastResult);
        recycler_forecast.setAdapter(adapter);
        recycler_forecast.setLayoutAnimation(controller);
        recycler_forecast.getAdapter().notifyDataSetChanged();
        recycler_forecast.scheduleLayoutAnimation();
    }

}
