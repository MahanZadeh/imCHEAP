package com.example.milkyway;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class CitySummary extends AppCompatActivity {

    String pricesUrl = "https://cost-of-living-and-prices.p.rapidapi.com/prices?city_name=Taipei&country_name=Taiwan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_summary);

        // City Highlight #1: Domestic beer costs 51.12 TWD!

        for (int i = 1; i <= 10; i++) {

            AsyncTaskRunner runner = new AsyncTaskRunner();
            try {
                runner.execute(pricesUrl).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {

        String itemPrice;

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(CitySummary.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, response -> {
                try {
                    JSONArray prices = response.getJSONArray("prices");
                    int itemId;
                    double tempPrice = 0.00;
                    String currencyCode;
                    String itemName;
                    Random random = new Random();
                    int randomItemId = random.nextInt(prices.length());

                    for (int i = 0; i < prices.length(); i++) {
                        JSONObject jsonObjectPrice = prices.getJSONObject(i);
                        itemId = jsonObjectPrice.getInt("good_id");
                        if (itemId == randomItemId) {
                            itemName = jsonObjectPrice.getString("item_name");
                            tempPrice = jsonObjectPrice.getDouble("avg");
                            currencyCode = jsonObjectPrice.getString("currency_code");
                            StringBuilder sb = new StringBuilder(itemName + " costs " + tempPrice
                                    + " " + currencyCode);
                            itemPrice = itemName + " costs " + tempPrice + " " + currencyCode;
                            Toast.makeText(CitySummary.this, sb, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        itemId = 1;
                    }
                    onSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(CitySummary.this, error.toString(), Toast.LENGTH_SHORT).show()) {

                /** Passing some request headers* */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-RapidAPI-Key", "b81516d2e7msh9653b7faa822afdp115f6fjsn863c3bf66af5");
                    headers.put("X-RapidAPI-Host", "cost-of-living-and-prices.p.rapidapi.com");
                    return headers;
                }
            };
            queue.add(request);
            return null;
        }

        protected void onSuccess() {
            Toast.makeText(CitySummary.this, "onPostExecute", Toast.LENGTH_SHORT).show();

            TableLayout tableLayout1 = findViewById(R.id.table_layout1); // here we grab the tablelayout

            Random randomColour = new Random();
            TableRow tableRow = new TableRow(CitySummary.this); //making a row
            TextView textView = new TextView(CitySummary.this); //making the text for that row

            textView.setText(itemPrice);
            textView.setWidth(900);
            textView.setHeight(100);
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.argb(255, randomColour.nextInt(256), randomColour.nextInt(256), randomColour.nextInt(256)));

            tableRow.addView(textView); // adding the text to the row
            tableLayout1.addView(tableRow); // THIS is where we add the row to the table layout

            tableLayout1.setForegroundGravity(Gravity.CENTER);
        }

    }
}