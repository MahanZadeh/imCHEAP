package com.example.milkyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.matteobattilana.weather.PrecipType;
import com.github.matteobattilana.weather.WeatherView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherApi extends AppCompatActivity {

    Animation sunAnim,cloud1Anim,cloud2Anim,titleAnim;
    ImageView sun,cloud1,cloud2;
    Map<String, ArrayList<String>> fiveDaysWeather = new HashMap<String, ArrayList<String>>();


    EditText etCity;
    TextView tvResult;
    Button btnGetData;
    private final String url = "https://api.openweathermap.org/data/2.5/forecast";
    private final String appid = "fa211ad253385ab5e5f303af6dfebb44";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_api);

//        WeatherView weatherView = findViewById(R.id.weather_view);
//        weatherView.setWeatherData(PrecipType.CUSTOM);

        etCity = findViewById(R.id.city);
        tvResult = findViewById(R.id.result);
        btnGetData = findViewById(R.id.btnGetData);
        btnGetData.setOnClickListener(view -> {
            tvResult.setText("");
            String tempUrl = "";
            String cityName = etCity.getText().toString().trim();
            if (cityName.equals("")) {
                tvResult.setText("City field can not be empty!");
            } else {
                tempUrl = url + "?q=" + cityName + "&appid=" + appid;
            }
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute(tempUrl);
        });

        sunAnim = AnimationUtils.loadAnimation(this,R.anim.sun);
        cloud1Anim = AnimationUtils.loadAnimation(this,R.anim.cloud1);
        cloud1Anim.setRepeatCount(Animation.INFINITE);
        cloud1Anim.setRepeatMode(Animation.INFINITE);
        cloud2Anim = AnimationUtils.loadAnimation(this,R.anim.cloud2);
        sun = findViewById(R.id.sun);
        cloud1 = findViewById(R.id.cloud1);
        cloud2 = findViewById(R.id.cloud2);

        cloud1.startAnimation(cloud1Anim);
        cloud2.startAnimation(cloud2Anim);
//        cloud1.startAnimation(sunAnim);
//        sun.startAnimation(sunAnim);


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                sun.startAnimation(sunAnim);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
////                        startActivity(new Intent(WeatherApi.this,MainActivity2.class));
//                        finish();
//                    }
//                },1000);
//            }
//        },1500);

    }


    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(WeatherApi.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        JSONArray fiveDayHourlyForecast = response.getJSONArray("list");


                        for(int i=0; i< fiveDayHourlyForecast.length(); i++) {
                            String tempDate = fiveDayHourlyForecast.getJSONObject(i).getString("dt_txt").substring(0,11);
                            ArrayList<String> oneDayWeatherInfo = new ArrayList<>();

                            if (!fiveDaysWeather.containsKey(tempDate)) {
                                oneDayWeatherInfo.add(fiveDayHourlyForecast.getJSONObject(i).getJSONObject("main").getString("temp"));
                                oneDayWeatherInfo.add(fiveDayHourlyForecast.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main"));

                                fiveDaysWeather.put(tempDate, oneDayWeatherInfo);

                            }

                        }

                        // Clouds (few, scattered, overcast, broken), rain (light, moderate)   clear sky
                        etCity = findViewById(R.id.city);

                        String cityName = etCity.getText().toString().trim();
                        tvResult.setText("Current weather of " + cityName + fiveDaysWeather+ " (" + fiveDaysWeather.values().toString());
                        onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(WeatherApi.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(request);
            return null;
        }

        protected void onSuccess() {
            String testCon = "Clouds";
            if (testCon.equals("Clouds")) {
                sun.setVisibility(View.VISIBLE);
                sun.startAnimation(sunAnim);
            }
        }

    }
}

///Copyright 2019 Matteo Battilana
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.