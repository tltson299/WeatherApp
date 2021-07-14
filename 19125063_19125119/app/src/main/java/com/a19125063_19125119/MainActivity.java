package com.a19125063_19125119;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.a19125063_19125119.api.ApiService;
import com.a19125063_19125119.weathermodel.WeatherDetail;
import com.google.gson.Gson;


import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat sdfNow = new SimpleDateFormat("E, dd/MM/yyyy");
    private SimpleDateFormat sdf = new SimpleDateFormat("kk:mm dd/MM");
    public static final String TAG = "DEBUG";
    private static final String defaultCity = "Saigon";

    private ImageView imgWeatherIcon, imgWeatherIcon1, imgWeatherIcon2, imgWeatherIcon3, imgWeatherIcon4;
    private TextView tvNow, tvCity, tvDate1, tvDate2, tvDate3, tvDate4, tvDegree, tvDegree1, tvDegree2, tvDegree3, tvDegree4, tvWeatherType;
    private Button btnSearch;

    private static SharedPreferences local;
    private static boolean start = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        local = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        initViews();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchAcitivty.class));
                overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
            }
        });
    }

    private void initViews() {
        btnSearch = findViewById(R.id.btnSearch);

        tvNow = findViewById(R.id.tvNow);
        tvCity = findViewById(R.id.tvCity);
        tvWeatherType = findViewById(R.id.tvWeatherType);
        tvDegree = findViewById(R.id.tvDegree);
        tvDegree1 = findViewById(R.id.tvDegree1);
        tvDegree2 = findViewById(R.id.tvDegree2);
        tvDegree3 = findViewById(R.id.tvDegree3);
        tvDegree4 = findViewById(R.id.tvDegree4);
        tvDate1 = findViewById(R.id.tvDate1);
        tvDate2 = findViewById(R.id.tvDate2);
        tvDate3 = findViewById(R.id.tvDate3);
        tvDate4 = findViewById(R.id.tvDate4);

        imgWeatherIcon = findViewById(R.id.imgWeatherIcon);
        imgWeatherIcon1 = findViewById(R.id.imgWeatherIcon1);
        imgWeatherIcon2 = findViewById(R.id.imgWeatherIcon2);
        imgWeatherIcon3 = findViewById(R.id.imgWeatherIcon3);
        imgWeatherIcon4 = findViewById(R.id.imgWeatherIcon4);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(start)   {
            getSelectedWeatherDetail(defaultCity);
            start = false;
        }else{
            Intent intent = getIntent();
            if (intent != null) {
                if(!(intent.getBooleanExtra("searched", false)))    {
                    setUpViews(new Gson().fromJson(local.getString("data", null), WeatherDetail.class));
                }else{
                    if(intent.getStringExtra("city") != null)  {
                        getSelectedWeatherDetail(intent.getStringExtra("city"));
                    }
                }
            }
        }
    }

    private void setUpViews(WeatherDetail detail)   {
        Date now = new Date();

        tvNow.setText(sdfNow.format(now));

        tvCity.setText(detail.city.name);

        tvWeatherType.setText(detail.list.get(0).weather.get(0).description.substring(0, 1).toUpperCase()+detail.list.get(0).weather.get(0).description.substring(1));

        imgWeatherIcon.setImageResource(getResources().getIdentifier(updateWeatherIcon(detail.list.get(0).weather.get(0).id), "drawable", getPackageName()));
        imgWeatherIcon1.setImageResource(getResources().getIdentifier(updateWeatherIcon(detail.list.get(1).weather.get(0).id), "drawable", getPackageName()));
        imgWeatherIcon2.setImageResource(getResources().getIdentifier(updateWeatherIcon(detail.list.get(2).weather.get(0).id), "drawable", getPackageName()));
        imgWeatherIcon3.setImageResource(getResources().getIdentifier(updateWeatherIcon(detail.list.get(3).weather.get(0).id), "drawable", getPackageName()));
        imgWeatherIcon4.setImageResource(getResources().getIdentifier(updateWeatherIcon(detail.list.get(4).weather.get(0).id), "drawable", getPackageName()));

        tvDegree.setText(String.valueOf(withMathRound(detail.list.get(0).main.feels_like, 1))+"°C, "+String.valueOf(detail.list.get(0).main.humidity)+"%");
        tvDegree1.setText(String.valueOf(withMathRound(detail.list.get(1).main.feels_like, 1))+"°C, "+String.valueOf(detail.list.get(1).main.humidity)+"%");
        tvDegree2.setText(String.valueOf(withMathRound(detail.list.get(2).main.feels_like, 1))+"°C, "+String.valueOf(detail.list.get(2).main.humidity)+"%");
        tvDegree3.setText(String.valueOf(withMathRound(detail.list.get(3).main.feels_like, 1))+"°C, "+String.valueOf(detail.list.get(3).main.humidity)+"%");
        tvDegree4.setText(String.valueOf(withMathRound(detail.list.get(4).main.feels_like, 1))+"°C, "+String.valueOf(detail.list.get(4).main.humidity)+"%");

        tvDate1.setText(sdf.format(new Date(detail.list.get(1).dt*1000L)));
        tvDate2.setText(sdf.format(new Date(detail.list.get(2).dt*1000L)));
        tvDate3.setText(sdf.format(new Date(detail.list.get(3).dt*1000L)));
        tvDate4.setText(sdf.format(new Date(detail.list.get(4).dt*1000L)));
    }

    public static double withMathRound(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    private void getSelectedWeatherDetail(String city) {

        ApiService.apiService.getWeather(city, "metric", 5,"b9c9cb24ed8b56758aa8f6788235bd09").enqueue(new Callback<WeatherDetail>() {
            @Override
            public void onResponse(Call<WeatherDetail> call, Response<WeatherDetail> response) {
                WeatherDetail detail = response.body();

                if(detail == null)  {
                    Log.d(TAG, "onResponse: detail = null");
                    Log.d(TAG, "onResponse: "+new Gson().fromJson(local.getString("data", null), WeatherDetail.class).city.name);
                    detail = new Gson().fromJson(local.getString("data", null), WeatherDetail.class);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Can not find "+city+".")
                            .setPositiveButton("Ok", null)
                            .show();
                }else   {
                    String data = new Gson().toJson(detail);
                    local.edit().putString("data", data).commit();
                }

                setUpViews(detail);
            }

            @Override
            public void onFailure(Call<WeatherDetail> call, Throwable t) {
                Log.d("DEBUG", t.getMessage());
            }
        });


    }

    private static String updateWeatherIcon(int condition)
    {
        if(condition>=0 && condition<=300)
        {
            return "thunderstorm1";
        }
        else if(condition>=300 && condition<=500)
        {
            return "lightrain";
        }
        else if(condition>=500 && condition<=600)
        {
            return "shower";
        }
        else  if(condition>=600 && condition<=700)
        {
            return "snow1";
        }
        else if(condition>=701 && condition<=771)
        {
            return "fog";
        }

        else if(condition>=772 && condition<=800)
        {
            return "overcast";
        }
        else if(condition==800)
        {
            return "sunny";
        }
        else if(condition>=801 && condition<=804)
        {
            return "cloudy";
        }
        else  if(condition>=900 && condition<=902)
        {
            return "thunderstorm1";
        }
        if(condition==903)
        {
            return "snow1";
        }
        if(condition==904)
        {
            return "sunny";
        }
        if(condition>=905 && condition<=1000)
        {
            return "thunderstorm1";
        }

        return "dunno";


    }

    public void intentToWebsite(View view)   {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://openweathermap.org"));
        startActivity(intent);
    }
}


