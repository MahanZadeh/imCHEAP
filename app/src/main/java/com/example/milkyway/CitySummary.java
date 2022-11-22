package com.example.milkyway;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

        TableLayout tableLayout1 = findViewById(R.id.table_layout1); // here we grab the tablelayout

        Random random = new Random();
        for(int i=1; i<6; i++) {
            TableRow tableRow = new TableRow(this); //making a row

            TextView textView = new TextView(this); //making the text for that row

            // new
            AsyncTaskRunner runner = new AsyncTaskRunner();
            try {
                ArrayList<String> citiesList = runner.execute(pricesUrl).get();
                Toast.makeText(CitySummary.this, "Worked!!!", Toast.LENGTH_SHORT).show();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            String text = "City Highlight (API) #" + i;  // adding details and params of the text view to be added
            textView.setText(text); // for the text/images/etc, look at the adapter thing for the city spinner lab
            textView.setWidth(900); // mahan how do I set this to match parent or something?
//            textView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

            textView.setHeight(100);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));

            tableRow.addView(textView); // adding the text to the row

            tableLayout1.addView(tableRow); // THIS is where we add the row to the table layout
        }
        tableLayout1.setForegroundGravity(Gravity.CENTER);

    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, ArrayList<String>> {

        ArrayList<String> citiesArrayList = new ArrayList<>();

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(CitySummary.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, response -> {
                try {
                    JSONArray prices = response.getJSONArray("prices");
                    String itemName = "";
                    double tempPrice = 0.00;
                    String currencyCode = "";
                    String randomItem = "beer";

                    for (int i = 0; i < prices.length(); i++) {
                        JSONObject jsonObjectPrice = prices.getJSONObject(i);
                        itemName = jsonObjectPrice.getString("item_name");
                        if (itemName.toLowerCase(Locale.ROOT)
                                .contains(randomItem.toLowerCase(Locale.ROOT))) {
                            tempPrice = jsonObjectPrice.getDouble("avg");
                            currencyCode = jsonObjectPrice.getString("currency_code");
                            StringBuilder sb = new StringBuilder(tempPrice + currencyCode);
                            Toast.makeText(CitySummary.this, sb, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        itemName = "";
                    }

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
            return citiesArrayList;
        }

//        @Override
//        protected void onPostExecute(String result) {
//        }
    }
}