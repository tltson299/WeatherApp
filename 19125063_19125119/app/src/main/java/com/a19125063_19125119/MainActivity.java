package com.a19125063_19125119;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.a19125063_19125119.api.ApiService;
import com.a19125063_19125119.weathermodel.WeatherDetail;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String defaultCity = "Saigon";
    private String selectedCity = "";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSelectedWeatherDetail("");
    }

    private void getSelectedWeatherDetail(String city) {
        String selected;
        if(city.equals("")) {
            selected = defaultCity;
        }else{
            selected = city;
        }
            //
        ApiService.apiService.getWeather(selected, "metric", 5,"b9c9cb24ed8b56758aa8f6788235bd09").enqueue(new Callback<WeatherDetail>() {
            @Override
            public void onResponse(Call<WeatherDetail> call, Response<WeatherDetail> response) {
                WeatherDetail detail = response.body();
                Log.d("DEBUG", detail.city.name+": "+detail.list.get(0).dt_txt);
            }

            @Override
            public void onFailure(Call<WeatherDetail> call, Throwable t) {
                Log.d("DEBUG", t.getMessage());
            }
        });
    }
}