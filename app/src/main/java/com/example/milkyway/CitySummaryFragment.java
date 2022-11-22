package com.example.milkyway;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CitySummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CitySummaryFragment extends Fragment {

    String pricesUrl = "https://cost-of-living-and-prices.p.rapidapi.com/prices?city_name=Taipei&country_name=Taiwan";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CitySummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CitySummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CitySummaryFragment newInstance(String param1, String param2) {
        CitySummaryFragment fragment = new CitySummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 1; i <= 10; i++) {

            AsyncTaskRunner runner = new CitySummary.AsyncTaskRunner();
            try {
                runner.execute(pricesUrl).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_city_summary, container, false);
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {

        String itemPrice;

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(CitySummary.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, response -> {
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
                    if (isExists) {
                        onSuccess();
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
            return null;
        }

        protected void onSuccess() {

            TableLayout tableLayout1 = findViewById(R.id.table_layout1); // here we grab the tablelayout
            final int baseColor = Color.WHITE;
            final int baseRed = Color.red(baseColor);
            final int baseGreen = Color.green(baseColor);
            final int baseBlue = Color.blue(baseColor);

            Random random = new Random();
            TableRow tableRow = new TableRow(CitySummary.this); //making a row
            TextView textView = new TextView(CitySummary.this); //making the text for that row

            textView.setText(itemPrice);
            textView.setWidth(1000);
            textView.setHeight(100);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.argb(255,
                    (baseRed + random.nextInt(256)) / 2,
                    (baseGreen + random.nextInt(256)) /2 ,
                    (baseBlue + random.nextInt(256)) / 2));

            tableRow.addView(textView); // adding the text to the row
            tableLayout1.addView(tableRow); // THIS is where we add the row to the table layout

            tableLayout1.setForegroundGravity(Gravity.CENTER);
        }

    }

}

