package com.example.milkyway;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;


public class SampleAllCities extends AppCompatActivity {
    EditText etCountry;
    TextView tvResult;
    Button btnGetData;
    private final String url = "https://cost-of-living-and-prices.p.rapidapi.com/cities";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_all_cities);

        etCountry = findViewById(R.id.country);
        btnGetData = findViewById(R.id.btnGetData);
        btnGetData.setOnClickListener(view -> {
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute(url);
            Toast.makeText(SampleAllCities.this, url, Toast.LENGTH_SHORT).show();
        });
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(SampleAllCities.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray cities = response.getJSONArray("cities");
                        tvResult = findViewById(R.id.all_city_result);
                        String userInputCountry = etCountry.getText().toString().trim();
                        String city = "";
                        String itemName = "";
                        StringBuilder citiesStringBuilder = new StringBuilder();

                        for (int i=0; i < cities.length(); i++) {
                            JSONObject jsonObjectPrice = cities.getJSONObject(i);
                            itemName = jsonObjectPrice.getString("country_name");
                            itemName = itemName.toLowerCase(Locale.ROOT);
                            if (itemName.contains(userInputCountry)) {
                                city = jsonObjectPrice.getString("city_name");
                                citiesStringBuilder.append(city).append("\n");
                            }
                        }
                        Toast.makeText(SampleAllCities.this, city, Toast.LENGTH_SHORT).show();
                        tvResult.setText(citiesStringBuilder);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SampleAllCities.this, error.toString(), Toast.LENGTH_SHORT).show();
                }

            })
            {
                /** Passing some request headers* */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-RapidAPI-Key", "a7a771dd33mshd86919ea5896511p1a2b01jsnd7f94fc97590");
                    headers.put("X-RapidAPI-Host", "cost-of-living-and-prices.p.rapidapi.com");
                    return headers;
                }
            };

            queue.add(request);
            return null;
        }
    }
}