package com.a19125063_19125119.api;

import com.a19125063_19125119.weathermodel.WeatherDetail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    //Weather API: api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("data/2.5/forecast")
    Call<WeatherDetail> getWeather(@Query("q") String q,
                                   @Query("units") String units,
                                   @Query("cnt") int cnt,
                                   @Query("appid") String appid);
}
