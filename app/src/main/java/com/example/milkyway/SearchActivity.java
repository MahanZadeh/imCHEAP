package com.example.milkyway;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

    String countryName;
    Bundle bundle = new Bundle();
    ArrayList<String> citiesList = new ArrayList<>();
    HashMap<Double, List<String>> cityInfo = new HashMap<>();

    private final String citiesUrl = "https://cost-of-living-and-prices.p.rapidapi.com/cities";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        setNationalitySpinner();

        Button toResults = findViewById(R.id.btnSearch);
        toResults.setOnClickListener(view -> {
            // Grab cities
            AsyncTaskRunnerCities runnerCities = new AsyncTaskRunnerCities();
            runnerCities.execute(citiesUrl);
        });

//        Button toSampleAPI = findViewById(R.id.sampleAPIbutton);
//        toSampleAPI.setOnClickListener(view -> {
//            Intent intent = new Intent(this, SampleAPIconnection.class);
//            startActivity(intent);
//        });

    }

    public void setNationalitySpinner() {
        Spinner spinner = findViewById(R.id.nationality_spinner);
        ArrayList<String> countries = new ArrayList<>();

        InputStream inputStream = getBaseContext().getResources().openRawResource(R.raw.countries);
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        try {
            line = bufferedReader.readLine();
            countries.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null) {
            // make use of the line read
            try {
                line = bufferedReader.readLine();
                countries.add(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This piece of code sets the spinner to display info from countries.txt
        ArrayAdapter<String> countriesData = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, countries);
        spinner.setAdapter(countriesData);
    }

    private class AsyncTaskRunnerCities extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray cities = response.getJSONArray("cities");
                        Spinner spinner = findViewById(R.id.nationality_spinner);
                        countryName = spinner.getSelectedItem().toString()
                                .toLowerCase(Locale.ROOT);
                        String userInputCountry = countryName;
                        String city;
                        String itemName;

                        for (int i = 0; i < cities.length(); i++) {
                            JSONObject jsonObjectPrice = cities.getJSONObject(i);
                            itemName = jsonObjectPrice.getString("country_name");
                            itemName = itemName.toLowerCase(Locale.ROOT);
                            if (itemName.contains(userInputCountry)) {
                                city = jsonObjectPrice.getString("city_name");
                                citiesList.add(city);
                            }
                        }
                        onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SearchActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            })

            {
                /** Passing some request headers* */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-RapidAPI-Key", "a7a771dd33mshd86919ea5896511p1a2b01jsnd7f94fc97590");
                    headers.put("X-RapidAPI-Host", "cost-of-living-and-prices.p.rapidapi.com");
                    return headers;
                }
            };
            queue.add(request);
            return null;
        }

        protected void onSuccess() {
            if (citiesList.size() != 0) {
                for (int i = 0; i < 10; i++) {
                    String pricesUrl = "https://cost-of-living-and-prices.p.rapidapi.com/prices";
                    String tempUrl = pricesUrl + "?country_name=" + countryName + "&city_name=" + citiesList.get(i);
                    AsyncTaskRunnerPrices runner = new AsyncTaskRunnerPrices();
                    runner.execute(tempUrl);
                }
            }
        }
    }

    private class AsyncTaskRunnerPrices extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        String cityName = response.getString("city_name");
                        JSONArray prices = response.getJSONArray("prices");


                        String userInputTextTest = "beer";
                        double tempPrice = 0.00;
                        String itemName = "";
                        String currencyCode = "";
                        for (int i=0; i < prices.length(); i++) {
                            JSONObject jsonObjectPrice = prices.getJSONObject(i);
                            itemName = jsonObjectPrice.getString("item_name");
                            itemName = itemName.toLowerCase(Locale.ROOT);
                            if (itemName.contains(userInputTextTest)) {
                                tempPrice = jsonObjectPrice.getDouble("avg");
                                currencyCode = jsonObjectPrice.getString("currency_code");
                                break;
                            }
                        }
//                        Toast.makeText(SampleAPIconnection.this, String.valueOf(prices.length()), Toast.LENGTH_SHORT).show();
                        List<String> cityItemCode = new ArrayList<>();
                        cityItemCode.add(cityName);
                        cityItemCode.add(itemName);
                        cityItemCode.add(currencyCode);
                        cityInfo.put(tempPrice, cityItemCode);
                        onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SearchActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }

            })

            {

                /** Passing some request headers* */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-RapidAPI-Key", "a7a771dd33mshd86919ea5896511p1a2b01jsnd7f94fc97590");
                    headers.put("X-RapidAPI-Host", "cost-of-living-and-prices.p.rapidapi.com");
                    return headers;
                }
            };
            queue.add(request);
            return null;
        }

        protected void onSuccess() {
            if (cityInfo.size() <= 10) {
                Double[] itemsByPrice = cityInfo.keySet().toArray(new Double[0]);
                Arrays.sort(itemsByPrice);
                // We may need to implement more robust sorting in another class?
                ArrayList<String> sortedCities = new ArrayList<>();
                ArrayList<String> costsDescription = new ArrayList<>();
                for (Double price : itemsByPrice) {
                    String cityName = Objects.requireNonNull(cityInfo.get(price)).get(0);
                    sortedCities.add(cityName);
                    String itemName = Objects.requireNonNull(cityInfo.get(price)).get(1);
                    String cCode = Objects.requireNonNull(cityInfo.get(price)).get(2);
                    String fullDescription = itemName + ", Price: " + price + " " + cCode;
                    costsDescription.add(fullDescription);
                }
                bundle.putStringArrayList("Sorted Cities", sortedCities);
                bundle.putStringArrayList("Sorted Price Descriptions", costsDescription);
                bundle.putString("Country Name", countryName);

                Intent intent = new Intent(SearchActivity.this, ResultsPage.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }
}
