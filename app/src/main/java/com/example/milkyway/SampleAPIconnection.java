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


public class SampleAPIconnection extends AppCompatActivity {
    EditText etCity;
    EditText etCountry;
    TextView tvResult;
    Button btnGetData;
    private final String url = "https://cost-of-living-and-prices.p.rapidapi.com/prices";
//    private final String appid = "287bb744e2msh06023a70a90dc5cp1b7209jsn23df725e6649";
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
                tempUrl = url + "?country_name=" + countryName + "&city_name=" + cityName;
            }
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute(tempUrl);
            Toast.makeText(SampleAPIconnection.this, tempUrl.toString(), Toast.LENGTH_SHORT).show();
        });
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(SampleAPIconnection.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {


                        JSONArray prices = response.getJSONArray("prices");


                        String userInputTextTest = "beer";
                        String tempPrice = "";
                        String itemName = "";
                        String currencyCode = "";
                        for (int i=0; i < prices.length(); i++) {
                            JSONObject jsonObjectPrice = prices.getJSONObject(i);
                            itemName = jsonObjectPrice.getString("item_name");
                            if (itemName.contains(userInputTextTest)) {
                                tempPrice = jsonObjectPrice.getString("avg");
                                currencyCode = jsonObjectPrice.getString("currency_code");
                                break;
                            }
                        }
                        Toast.makeText(SampleAPIconnection.this, String.valueOf(prices.length()), Toast.LENGTH_SHORT).show();
                        tvResult.setText(itemName + "\n" + "Price " + tempPrice + " " + currencyCode);
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
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-RapidAPI-Key", "287bb744e2msh06023a70a90dc5cp1b7209jsn23df725e6649");
                    headers.put("X-RapidAPI-Host", "cost-of-living-and-prices.p.rapidapi.com");
                    return headers;
                }
            };

            queue.add(request);
            return null;
        }
    }
}