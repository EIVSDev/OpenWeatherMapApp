package com.dev.eivs.openweathermapapp.retrofit;

import com.dev.eivs.openweathermapapp.model.WeatherForecastResult;
import com.dev.eivs.openweathermapapp.model.WheatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServise {

    @GET("weather")
    Observable<WheatherResult> getWheatherByLatLng(@Query("lat") String lat,
                                                   @Query("lon") String lng,
                                                   @Query("appid")String appid,
                                                   @Query("units")String unit);

    @GET("forecast")
    Observable<WeatherForecastResult> getForecastWeatherByLatLng(@Query("lat") String lat,
                                                                 @Query("lon") String lng,
                                                                 @Query("appid")String appid,
                                                                 @Query("units")String unit);
}
