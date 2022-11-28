package com.example.milkyway;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.core.content.ContextCompat;

import com.github.matteobattilana.weather.PrecipType;
import com.github.matteobattilana.weather.WeatherView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

//The code for part of the weather (rain and snow) animation is from:

//Copyright 2019 Matteo Battilana
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

//The starting code for part of the weather (sun and clouds) transition animation is from:
//
//https://m-ify-education.blogspot.com/2022/02/android-studio-sun-and-clouds-splash.html

public class CitySummaryFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    String pricesUrl;
    Animation sunAnim,cloud1Anim,cloud2Anim, cloud3Anim;
    ImageView sun,cloud1,cloud2, cloud3;
    Map<String, ArrayList<String>> fiveDaysWeather = new LinkedHashMap<>();
    WeatherView weatherView;
    String tempUrl;
    TextView temp;
    ArrayList<String> descriptionList = new ArrayList<>();
    int position = 0;
    TableLayout tableLayout1;
    int count = 0;
    int maxPriceCalls = 10;

    RelativeLayout factsLoadingLayout;
    RelativeLayout factsFailureLayout;

    public CitySummaryFragment() {
        // Required empty public constructor
    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_city_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        temp = requireView().findViewById(R.id.temp);
        String tempText = "Temp: ";
        temp.setText(tempText);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String countryName = bundle.getString("countryName");
            String cityName = bundle.getString("cityName");

            pricesUrl = "https://cost-of-living-and-prices.p.rapidapi.com/prices?city_name="
                    + cityName + "&country_name=" + countryName;

            factsLoadingLayout = requireView().findViewById(R.id.loadingFunFacts);
            tableLayout1 = requireView().findViewById(R.id.city_summary_table);
            factsFailureLayout = requireView().findViewById(R.id.factsLoadFailure);

            TextView cityNameView = requireView().findViewById(R.id.summary_city_name);
            StringBuilder cityNameSb = new StringBuilder("City Highlight of " + cityName);
            cityNameView.setText(cityNameSb);
            String lowerCaseCityName = cityName.toLowerCase(Locale.ROOT);
            String url = "https://api.openweathermap.org/data/2.5/forecast";
            String appid = "fa211ad253385ab5e5f303af6dfebb44";
            tempUrl = url + "?q=" + lowerCaseCityName + "&appid=" + appid;
            AsyncTaskRunnerWeather runnerWeather = new AsyncTaskRunnerWeather();
            runnerWeather.execute(tempUrl);

            ImageView image = requireView().findViewById(R.id.cityImage);
            Picasso.get()
                    .load("https://countryflagsapi.com/png/" + countryName)
                    .resize(600, 400) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.image_not_found)
                    .into(image);

            sunAnim = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(),
                    R.anim.sun);
            cloud1Anim = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(),
                    R.anim.cloud1);
            cloud2Anim = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(),
                    R.anim.cloud2);
            cloud3Anim = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(),
                    R.anim.cloud3);
            sun = requireView().findViewById(R.id.sun);
            cloud1 = requireView().findViewById(R.id.cloud1);
            cloud2 = requireView().findViewById(R.id.cloud2);
            cloud3 = requireView().findViewById(R.id.cloud3);

            for (int i = 1; i <= maxPriceCalls; i++) {
                AsyncTaskRunner runner = new AsyncTaskRunner();
                try {
                    runner.execute(pricesUrl).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "There has been an error processing city data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void chooseWeather(String desc, String dayTemp) {

        double tempCelsius = Double.parseDouble(dayTemp) - 273.15;
        DecimalFormat value = new DecimalFormat("#.#");

        String tempText = "Temp: " + value.format(tempCelsius) + "\u2103";
        temp.setText(tempText);
        if (desc.contains("clouds")) {
            sun.setVisibility(View.GONE);
            sun.clearAnimation();
            cloud1.setVisibility(View.VISIBLE);
            cloud1.startAnimation(cloud1Anim);
            cloud2.setVisibility(View.VISIBLE);
            cloud2.startAnimation(cloud2Anim);
            cloud3.setVisibility(View.VISIBLE);
            cloud3.startAnimation(cloud3Anim);
            weatherView.setWeatherData(PrecipType.CLEAR);

        } else if (desc.contains("rain")) {
            sun.clearAnimation();
            sun.setVisibility(View.GONE);
            cloud1.setVisibility(View.VISIBLE);
            cloud1.startAnimation(cloud1Anim);
            cloud2.setVisibility(View.VISIBLE);
            cloud2.startAnimation(cloud2Anim);
            weatherView.setWeatherData(PrecipType.RAIN);

        } else if (desc.contains("clear")) {
            cloud1.setVisibility(View.GONE);
            cloud2.setVisibility(View.GONE);
            cloud3.setVisibility(View.GONE);
            cloud1.clearAnimation();
            cloud2.clearAnimation();
            cloud3.clearAnimation();
            sun.setVisibility(View.VISIBLE);
            sun.startAnimation(sunAnim);
            weatherView.setWeatherData(PrecipType.CLEAR);

        } else if (desc.contains("snow")){
            sun.clearAnimation();
            sun.setVisibility(View.GONE);
            cloud1.setVisibility(View.VISIBLE);
            cloud1.startAnimation(cloud1Anim);
            cloud2.setVisibility(View.VISIBLE);
            cloud2.startAnimation(cloud2Anim);
            weatherView.setWeatherData(PrecipType.SNOW);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class AsyncTaskRunnerWeather extends AsyncTask<String, Void, String> {

        AsyncTaskRunnerWeather() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            factsLoadingLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0],
                    null, response -> {
                try {
                    JSONArray fiveDayHourlyForecast = response.getJSONArray("list");

                    for(int i=0; i< fiveDayHourlyForecast.length(); i++) {
                        String tempDate = fiveDayHourlyForecast.getJSONObject(i)
                                .getString("dt_txt").substring(0,10);
                        ArrayList<String> oneDayWeatherInfo = new ArrayList<>();

                        if (!fiveDaysWeather.containsKey(tempDate)) {
                            oneDayWeatherInfo.add(fiveDayHourlyForecast.getJSONObject(i)
                                    .getJSONObject("main").getString("temp"));
                            oneDayWeatherInfo.add(fiveDayHourlyForecast.getJSONObject(i)
                                    .getJSONArray("weather").getJSONObject(0)
                                    .getString("main"));
                            fiveDaysWeather.put(tempDate, oneDayWeatherInfo);
                        }
                    }
                    onSuccess();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }, error -> onFailure());
            queue.add(request);
            return null;
        }

        protected void onFailure() {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "Could not retrieve weather data for this city",
                    Toast.LENGTH_SHORT).show();
            String tempText = temp.getText().toString();
            if (tempText.equals("Temp: ")) {
                tempText = tempText + "N/A";
                temp.setText(tempText);
            }
        }

        protected void onSuccess() throws ParseException {
            Locale currentLocale = Locale.getDefault();
            weatherView = requireView().findViewById(R.id.weather_view);

            ArrayList<String> spinnerList = new ArrayList<>();
            spinnerList.add("> Select");

            View linearLayout =  requireView().findViewById(R.id.linearSpinner);
            Spinner spinner = new Spinner(requireActivity().getApplicationContext());
            spinner.setDropDownWidth(300);
            spinner.setBackgroundColor(0);
            spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));

            DateFormat formatter = new SimpleDateFormat("EEEE", currentLocale);
            Set<String> keys = fiveDaysWeather.keySet();
            ArrayList<String> tempList = new ArrayList<>();
            for (String key : keys) {
                String description = Objects.requireNonNull(fiveDaysWeather.get(key)).get(1);
                String temperature = Objects.requireNonNull(fiveDaysWeather.get(key)).get(0);
                descriptionList.add(description);
                tempList.add(temperature);
                @SuppressLint("SimpleDateFormat") Date date1 =
                        new SimpleDateFormat("yyyy-MM-dd").parse(key);
                if (date1 != null) {
                    String day = formatter.format(date1);
                    spinnerList.add(day.substring(0, 3));
                } else {
                    Toast.makeText(requireActivity().getApplicationContext(),
                            "There has been an error processing date data",
                            Toast.LENGTH_SHORT).show();
                }


// Clouds (few, scattered, overcast, broken), rain (light, moderate)   clear sky

            }
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int item, long l) {
                    if (item==0) {
                        sun.setVisibility(View.GONE);
                        cloud1.setVisibility(View.GONE);
                        cloud2.setVisibility(View.GONE);
                        cloud3.setVisibility(View.GONE);
                        sun.clearAnimation();
                        cloud1.clearAnimation();
                        cloud2.clearAnimation();
                        cloud3.clearAnimation();
                        weatherView.setWeatherData(PrecipType.CLEAR);
                    }
                    if(item == 1) {
                        String desc = descriptionList.get(0).toLowerCase();
                        String dayTemp = tempList.get(0);
                        chooseWeather(desc, dayTemp);
                    } else if(item == 2) {
                        String desc = descriptionList.get(1).toLowerCase();
                        String dayTemp = tempList.get(1);
                        chooseWeather(desc, dayTemp);
                    }else if(item == 3) {
                        String desc = descriptionList.get(2).toLowerCase();
                        String dayTemp = tempList.get(2);
                        chooseWeather(desc, dayTemp);
                    }else if(item == 4) {
                        String desc = descriptionList.get(3).toLowerCase();
                        String dayTemp = tempList.get(3);
                        chooseWeather(desc, dayTemp);
                    }else if(item == 5) {
                        String desc = descriptionList.get(4).toLowerCase();
                        String dayTemp = tempList.get(4);
                        chooseWeather(desc, dayTemp);
                    }else if(item == 6) {
                        String desc = descriptionList.get(5).toLowerCase();
                        String dayTemp = tempList.get(5);
                        chooseWeather(desc, dayTemp);
                    }else if(item == 7) {
                        String desc = descriptionList.get(6).toLowerCase();
                        String dayTemp = tempList.get(6);
                        chooseWeather(desc, dayTemp);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(requireActivity()
                    .getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                    spinnerList);
            spinner.setAdapter(spinnerArrayAdapter);
            ((LinearLayout) linearLayout).addView(spinner);
        }
    }


    @SuppressLint("StaticFieldLeak")
    class AsyncTaskRunner extends AsyncTask<String, Void, String> {

        AsyncTaskRunner() {
            super();
        }

        String itemPrice;

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0],
                    null, response -> {
                boolean isExists = false;

                try {
                    JSONArray prices = response.getJSONArray("prices");
                    int itemId;
                    double tempPrice;
                    String currencyCode;
                    String itemName;
                    Random random = new Random();
                    int randomItemId = random.nextInt(prices.length());

                    for (int i = 0; i < prices.length(); i++) {
                        JSONObject jsonObjectPrice = prices.getJSONObject(i);
                        itemId = jsonObjectPrice.getInt("good_id");
                        itemName = jsonObjectPrice.getString("item_name");

                        if (itemId == randomItemId && jsonObjectPrice.has("item_name")) {
                            isExists = true;
                            position++;

                            if (itemName.contains(",")) {
                                String[] parts = itemName.split(",");
                                itemName = parts[0];
                            }

                            tempPrice = jsonObjectPrice.getDouble("avg");
                            currencyCode = jsonObjectPrice.getString("currency_code");
                            itemPrice = itemName + " costs " + tempPrice + " " + currencyCode + "!";
                            break;
                        }
                    }
                    if (isExists || (((count + 1) == maxPriceCalls) && maxPriceCalls != 0)) {
                        onSuccess(position);
                    } else {
                        maxPriceCalls--;
                    }
                    if (maxPriceCalls == 0) {
                        factsLoadingLayout.setVisibility(View.INVISIBLE);
                        factsFailureLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(requireActivity().getApplicationContext(), error.toString(),
                    Toast.LENGTH_SHORT).show()) {

                /** Passing some request headers* */
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-RapidAPI-Key", "b81516d2e7msh9653b7faa822afdp115f6fjsn863c3bf66af5");
                    headers.put("X-RapidAPI-Host", "cost-of-living-and-prices.p.rapidapi.com");
                    return headers;
                }
            };
            queue.add(request);
            return null;
        }

        protected void onSuccess(int position) {

            TableRow tableRow = new TableRow(requireActivity().getApplicationContext()); // making a row
            TextView textView = new TextView(requireActivity().getApplicationContext()); // making the text for that row

            textView.setText(itemPrice);
            textView.setWidth(900);
            textView.setHeight(100);
            textView.setGravity(Gravity.START);
            textView.setPadding(0,0,5,5);

            if (position % 2 != 0) {
                textView.setBackgroundColor(ContextCompat.getColor(textView.getContext(),
                        R.color.extra_light_sand));
            }

            tableRow.addView(textView); // adding the text to the row
            tableLayout1.addView(tableRow); // THIS is where we add the row to the table layout

            tableLayout1.setForegroundGravity(Gravity.CENTER);
            count++;
            if (count == maxPriceCalls) {
                factsLoadingLayout.setVisibility(View.INVISIBLE);
                tableLayout1.setVisibility(View.VISIBLE);
            }
        }
    }
}

