package com.example.milkyway;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class SearchFragment extends Fragment {

    String countryName;
    Bundle bundle = new Bundle();
    int currentCount = 0;
    int totalCount = 10;
    Random random = new Random();
    String searchChoice;
    int numberOfCities = 10;
    ArrayList<String> citiesList = new ArrayList<>();
    HashMap<List<String>, Double> cityInfo = new HashMap<>();

    private final String citiesUrl = "https://cost-of-living-and-prices.p.rapidapi.com/cities";

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SearchViewModel mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        // TODO: Use the ViewModel

        setNationalitySpinner();

        Button toResults = requireView().findViewById(R.id.btnSearch);
        toResults.setOnClickListener(view -> {
            // Get selected search choice
            RadioGroup radioGroup = requireView().findViewById(R.id.searchRadioGroup);
            int radioButtonID = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = radioGroup.findViewById(radioButtonID);
            String selectedText = (String) radioButton.getText();
            searchChoice = provideQuery(selectedText);

            if (searchChoice != null) {
                // Grab cities
                AsyncTaskRunnerCities runnerCities = new AsyncTaskRunnerCities();
                runnerCities.execute(citiesUrl);
            }
        });
    }

    public String provideQuery(String optionText) {
        switch (optionText) {
            case "Bottle of Beer":
                return "Beer"; // needs full query
            case "Gasoline, 1 liter":
            case "Cappuccino":
                return optionText;
            case "Taxi, price for 1 hour Waiting":
                return "Taxi"; // needs full query
            case "Meal in inexpensive restaurant":
                return "Meal in Inexpensive Restaurant";
            default:
                return null;
        }
    }

    public void setNationalitySpinner() {
        Spinner spinner = requireView().findViewById(R.id.nationality_spinner);
        ArrayList<String> countries = new ArrayList<>();

        InputStream inputStream = requireActivity().getBaseContext().getResources()
                .openRawResource(R.raw.countries);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
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
        ArrayAdapter<String> countriesData = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, countries);
        spinner.setAdapter(countriesData);
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunnerCities extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0],
                    null, response -> {
                try {
                    JSONArray cities = response.getJSONArray("cities");
                    Spinner spinner = requireView().findViewById(R.id.nationality_spinner);
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
            }, error -> Toast.makeText(requireActivity().getApplicationContext(),
                    error.toString(), Toast.LENGTH_SHORT).show()) {
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

        protected void onSuccess() {
            if (citiesList.size() != 0) {
                for (int i = 0; i < numberOfCities; i++) {
                    String pricesUrl = "https://cost-of-living-and-prices.p.rapidapi.com/prices";
                    int newRandom = random.nextInt(citiesList.size());
                    String tempUrl = pricesUrl + "?country_name=" + countryName + "&city_name="
                            + citiesList.get(newRandom);
                    AsyncTaskRunnerPrices runner = new AsyncTaskRunnerPrices();
                    runner.execute(tempUrl);
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunnerPrices extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, response -> {
                try {

                    String cityName = response.getString("city_name");
                    JSONArray prices = response.getJSONArray("prices");

                    String queryString = searchChoice;
                    double tempPrice = 0.00;
                    String itemName = "";
                    String currencyCode = "";
                    for (int i = 0; i < prices.length(); i++) {
                        JSONObject jsonObjectPrice = prices.getJSONObject(i);
                        itemName = jsonObjectPrice.getString("item_name");
                        if (itemName.toLowerCase(Locale.ROOT)
                                .contains(queryString.toLowerCase(Locale.ROOT))) {
                            tempPrice = jsonObjectPrice.getDouble("avg");
                            currencyCode = jsonObjectPrice.getString("currency_code");
                            break;
                        }
                        itemName = "";
                    }
                    if (!itemName.equals("")) {
                        List<String> cityItemCode = new ArrayList<>();
                        cityItemCode.add(cityName);
                        cityItemCode.add(itemName);
                        cityItemCode.add(currencyCode);
                        cityInfo.put(cityItemCode, tempPrice);
                        currentCount++;
                    } else {
                        totalCount--;
                    }
                    if (currentCount == totalCount) {
                        onSuccess();
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

        protected void onSuccess() {
            Double[] rawPrices = cityInfo.values().toArray(new Double[0]);
            // Remove duplicate prices
            LinkedHashSet<Double> pricesNoDuplicates = new LinkedHashSet<>(Arrays.asList(rawPrices));
            Double[] prices = pricesNoDuplicates.toArray(new Double[0]);
            Arrays.sort(prices);
            ArrayList<List<String>> sortedData = new ArrayList<>();
            for (Double price : prices) {
                for (List<String> key : cityInfo.keySet()) {
                    if (Objects.equals(cityInfo.get(key), price)) {
                        sortedData.add(key);
                    }
                }
            }
            // We may need to implement more robust sorting in another class?
            ArrayList<String> sortedCities = new ArrayList<>();
            ArrayList<String> costsDescription = new ArrayList<>();
            for (List<String> data : sortedData) {
                String cityName = data.get(0);
                sortedCities.add(cityName);
                String itemName = data.get(1);
                String cCode = data.get(2);
                String fullDescription = itemName + ", Price: " + cityInfo.get(data) + " " + cCode;
                costsDescription.add(fullDescription);
            }
            bundle.putStringArrayList("Sorted Cities", sortedCities);
            bundle.putStringArrayList("Sorted Price Descriptions", costsDescription);
            bundle.putString("Country Name", countryName);

            Intent intent = new Intent(requireActivity().getApplicationContext(), ResultsPage.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}