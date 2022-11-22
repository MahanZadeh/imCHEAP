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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeatherApi extends AppCompatActivity {

    Animation sunAnim,cloud1Anim,cloud2Anim,titleAnim;
    ImageView sun,cloud1,cloud2;
    Map<String, ArrayList<String>> fiveDaysWeather = new LinkedHashMap<>();
    WeatherView weatherView;

    EditText etCity;
    TextView tvResult;
    Button btnGetData;
    private final String url = "https://api.openweathermap.org/data/2.5/forecast";
    private final String appid = "fa211ad253385ab5e5f303af6dfebb44";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_api);

        weatherView = findViewById(R.id.weather_view);

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
//        cloud1Anim.setRepeatCount(Animation.INFINITE);
//        cloud1Anim.setRepeatMode(Animation.INFINITE);
        cloud2Anim = AnimationUtils.loadAnimation(this,R.anim.cloud2);
        sun = findViewById(R.id.sun);
        cloud1 = findViewById(R.id.cloud1);
        cloud2 = findViewById(R.id.cloud2);


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
                            String tempDate = fiveDayHourlyForecast.getJSONObject(i).getString("dt_txt").substring(0,10);
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
                    } catch (ParseException e) {
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

        protected void onSuccess() throws ParseException {
            int[] buttonViews = {R.id.date, R.id.button2};
            Locale currentLocale = Locale.getDefault();

            DateFormat formatter = new SimpleDateFormat("EEEE", currentLocale);
//            Map.Entry<String, ArrayList<String>> entry = fiveDaysWeather.entrySet().iterator().next();
            Iterator<Map.Entry<String, ArrayList<String>>> itr = fiveDaysWeather.entrySet().iterator();
            for (int i =0; i < buttonViews.length; i++) {
//                Map.Entry<String, ArrayList<String>> entry = fiveDaysWeather.entrySet().iterator().next();
                Map.Entry<String, ArrayList<String>> entry = itr.next();
                String description = entry.getValue().get(1);

                String key= entry.getKey();
                Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(key);
                String day = formatter.format(date1);
                Button btn = findViewById(buttonViews[i]);
                btn.setText(day);
                btn.setOnClickListener(view -> {
                    if (description.equals("Clouds")) {
                        sun.setVisibility(View.GONE);
                        sun.clearAnimation();
                        cloud1.setVisibility(View.VISIBLE);
                        cloud1.startAnimation(cloud1Anim);
                        cloud2.setVisibility(View.VISIBLE);
                        cloud2.startAnimation(cloud2Anim);
                    } else if (description.equals("Rain")) {
                        cloud1.setVisibility(View.VISIBLE);
                        cloud1.startAnimation(cloud1Anim);
                        cloud2.setVisibility(View.VISIBLE);
                        cloud2.startAnimation(cloud2Anim);
                        weatherView.setWeatherData(PrecipType.RAIN);
                }else if (description.equals("Clear")) {
                        cloud1.setVisibility(View.GONE);
                        cloud1.clearAnimation();
                        sun.setVisibility(View.VISIBLE);
                        sun.startAnimation(sunAnim);

                    }
                });


            }

//            for (String key : fiveDaysWeather.keySet()) {
//                ArrayList<String> value = fiveDaysWeather.get(key);
//
//                System.out.println(key + "=" + value);
//            }

//            Map.Entry<String, ArrayList<String>> entry = fiveDaysWeather.entrySet().iterator().next();
//            String key= entry.getKey();
//            String key2 = entry.getKey();
//            String temperature = entry.getValue().get(0);
//            String description = entry.getValue().get(1);
//            String description2 = entry.getValue().get(1);
//
//
//            Button btnDate = findViewById(R.id.date);
//            Button btnTwo = findViewById(R.id.button2);
//            Calendar c = Calendar.getInstance();
//            Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(key);
//            Date date2=new SimpleDateFormat("yyyy-MM-dd").parse(key2);
//
////            Locale currentLocale = Locale.getDefault();
////            DateFormat formatter = new SimpleDateFormat("EEEE", currentLocale);
//            String day = formatter.format(date1);
//            String day2 = formatter.format(date2);
//
////            c.setTime(date1);
//            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
//            System.out.println(day);
//            btnDate.setText(day);
//            btnTwo.setText(day2);
//            btnDate.setOnClickListener(view -> {
//                if (description.equals("Clouds")) {
//                    cloud1.setVisibility(View.VISIBLE);
//                    cloud1.startAnimation(cloud1Anim);
//                }
//            });
//            btnTwo.setOnClickListener(view -> {
//                if (description2.equals("Rain")) {
//                    cloud1.setVisibility(View.VISIBLE);
//                    cloud1.startAnimation(cloud1Anim);
//                    weatherView.setWeatherData(PrecipType.RAIN);
//                }
//            });
//            String testCon = "Clouds";
//            if (description.equals("Clouds")) {
//                cloud1.setVisibility(View.VISIBLE);
//                cloud1.startAnimation(cloud1Anim);
//                weatherView.setWeatherData(PrecipType.RAIN);
//
//            } else if (description.equals("Clear")) {
//                sun.setVisibility(View.VISIBLE);
//                sun.startAnimation(sunAnim);
//
//            }else if (description.contains("rain")) {
//                cloud1.setVisibility(View.VISIBLE);
//                cloud1.startAnimation(cloud1Anim);
//                weatherView.setWeatherData(PrecipType.RAIN);
//
//            }
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