package com.example.milkyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class SampleAPIconnection extends AppCompatActivity {
    EditText etCity;
    EditText etCountry;
    TextView tvResult;
    Button btnGetData;
    Bundle bundle = new Bundle();
    String country = "Germany";
    ArrayList<String> cities = new ArrayList<>();
    HashMap<Double, List<String>> cityInfo = new HashMap<>();

    private final String pricesUrl = "https://cost-of-living-and-prices.p.rapidapi.com/prices";
    private final String citiesUrl = "https://cost-of-living-and-prices.p.rapidapi.com/cities";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_apiconnection);

        etCity = findViewById(R.id.city);
        etCountry = findViewById(R.id.country);
        tvResult = findViewById(R.id.result);
        btnGetData = findViewById(R.id.btnGetData);
        btnGetData.setOnClickListener(view -> {
            tvResult.setText("");
            String tempUrl = "";
            String cityName = etCity.getText().toString().trim();
            String countryName = etCountry.getText().toString().trim();
            if (cityName.equals("")) {
                tvResult.setText("City field can not be empty!");
            } else if (countryName.equals("")) {
                tvResult.setText("Country field can not be empty!");
            } else {
                tempUrl = pricesUrl + "?country_name=" + countryName + "&city_name=" + cityName;
            }

            // Grab cities
            AsyncTaskRunner runnerCities = new AsyncTaskRunner();
            try {
                ArrayList<String> citiesList = runnerCities.execute(citiesUrl).get();
                Toast.makeText(SampleAPIconnection.this, "Worked!!!", Toast.LENGTH_SHORT).show();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            // Testing API calls for two cities
//            cities.add("Berlin");
//            cities.add("Hamburg");
//
//            for (String city : cities) {
//                tempUrl = pricesUrl + "?country_name=" + country + "&city_name=" + city;
//                AsyncTaskRunnerPrices runner = new AsyncTaskRunnerPrices();
//                runner.execute(tempUrl);
//            }
//            Toast.makeText(SampleAPIconnection.this, tempUrl.toString(), Toast.LENGTH_SHORT).show();
        });
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, ArrayList<String>> {

        ArrayList<String> citiesArrayList = new ArrayList<>();

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(SampleAPIconnection.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, response -> {
                try {
                    JSONArray cities = response.getJSONArray("cities");
                    String userInputCountry = etCountry.getText().toString().trim();
                    String city;
                    String itemName;
                    StringBuilder citiesStringBuilder = new StringBuilder();

                    for (int i = 0; i < cities.length(); i++) {
                        JSONObject jsonObjectPrice = cities.getJSONObject(i);
                        itemName = jsonObjectPrice.getString("country_name");
                        itemName = itemName.toLowerCase(Locale.ROOT);
                        if (itemName.contains(userInputCountry)) {
                            city = jsonObjectPrice.getString("city_name");
                            citiesArrayList.add(city);
                        }
                    }
                    for (String s : citiesArrayList) {
                        citiesStringBuilder.append(s).append("\n");
                    }
                    tvResult.setText(citiesStringBuilder);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(SampleAPIconnection.this, error.toString(), Toast.LENGTH_SHORT).show()) {

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
            return citiesArrayList;
        }
    }

    private class AsyncTaskRunnerPrices extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(SampleAPIconnection.this);
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
                        Toast.makeText(SampleAPIconnection.this, String.valueOf(prices.length()), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SampleAPIconnection.this, error.toString(), Toast.LENGTH_SHORT).show();
                }

            })

            {

                /** Passing some request headers* */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-RapidAPI-Key", "4858721f31msh5ed87e7963024b3p1da073jsn4d95cf387203");
                    headers.put("X-RapidAPI-Host", "cost-of-living-and-prices.p.rapidapi.com");
                    return headers;
                }
            };
            queue.add(request);
            return null;
        }

        protected void onSuccess() {
            if (cityInfo.size() == cities.size()) {
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

                Intent intent = new Intent(SampleAPIconnection.this, ResultsPage.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }
}