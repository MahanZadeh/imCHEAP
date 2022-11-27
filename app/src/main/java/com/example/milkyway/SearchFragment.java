package com.example.milkyway;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment {

    private Animation animation1;
    private String countryName;
    private final Bundle bundle = new Bundle();
    private int currentCount = 0;
    private int totalCount = 10;
    private final Random random = new Random();
    private String searchChoice;
    private final ArrayList<String> citiesList = new ArrayList<>();
    private final HashMap<List<String>, Double> cityInfo = new HashMap<>();

    private final String citiesUrl = "https://cost-of-living-and-prices.p.rapidapi.com/cities";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        animation1 = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(),
                R.anim.loading_text1);

        Spinner spinner = view.findViewById(R.id.nationality_spinner);
        setNationalitySpinner(spinner);

        // Create search option checkboxes
        RadioGroup radioGroup = view.findViewById(R.id.searchRadioGroup);
        String[] searchOptions = getResources().getStringArray(R.array.search_options);
        RadioButton rb = view.findViewById(R.id.radioButton1);
        rb.setText(searchOptions[0]);
        for (int i = 1; i < searchOptions.length; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setId(1 + i);
            radioButton.setText(searchOptions[i]);
            int color = ContextCompat.getColor(requireContext(), R.color.white);
            radioButton.setTextColor(color);
            radioButton.setBackgroundTintList(requireContext().getColorStateList(R.color.white));
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            radioButton.setButtonTintList(requireContext().getColorStateList(R.color.sand));
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 10, 0, 0);
            radioButton.setLayoutParams(params);
            radioGroup.addView(radioButton);
        }

        Button toResults = view.findViewById(R.id.btnSearch);
        toResults.setOnClickListener(View -> {
            // Get selected search choice
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

        return view;
    }

    public String provideQuery(String optionText) {
        switch (optionText) {
            case "Bottle of Beer":
                return "Beer";
            case "Bottle/Can of Coca-Cola":
                return "Coca-Cola";
            case "Bottle of Water":
                return "Water";
            case "Bottle of Wine":
                return "Wine";
            case "Cinema ticket, 1 Seat":
                return "Cinema";
            case "Eggs, 12 pack":
                return "Eggs";
            case "Loaf of Fresh White Bread":
                return "Loaf";
            case "McMeal at McDonalds or Alternative Combo Meal":
                return "McMeal";
            case "Local Cheese":
            case "Gasoline, 1 liter":
            case "Cappuccino":
            case "One-way Ticket, Local Transport":
                return optionText;
            case "Taxi, price for 1 hour Waiting":
                return "Taxi";
            case "Meal in inexpensive restaurant":
                return "Meal in Inexpensive Restaurant";
            case "Rent for one bedroom apartment in city centre":
                return "One bedroom apartment in city centre";
            case "Pack of Cigarettes":
                return "Cigarettes";
            case "Pair of Jeans":
                return "Jeans";
            case "Prepaid Mobile, price per 1 min, No Discounts or Plans":
                return "Prepaid Mobile";
            default:
                return null;
        }
    }

    public void setNationalitySpinner(Spinner spinner) {
        ArrayList<String> countries = new ArrayList<>();

        InputStream inputStream = requireActivity().getBaseContext().getResources()
                .openRawResource(R.raw.countries);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        try {
            line = bufferedReader.readLine();
            if (line != null) {
                countries.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null) {
            // make use of the line read
            try {
                line = bufferedReader.readLine();
                if (line != null) {
                    countries.add(line);
                }
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

        AsyncTaskRunnerCities() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            requireView().findViewById(R.id.loadingScreen).setVisibility(View.VISIBLE);
            requireView().findViewById(R.id.loadingMessage).startAnimation(animation1);
        }

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
                int numberOfCities = 10;
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

        AsyncTaskRunnerPrices() {
            super();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0],
                    null, response -> {
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
            if (cityInfo.size() != 0) {
                LinkedHashMap<List<String>, Double> sortedData = cityInfo.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                LinkedHashMap::new));
                List<List<String>> keys = new ArrayList<>(sortedData.keySet());

                ArrayList<String> sortedCities = new ArrayList<>();
                ArrayList<String> costsDescription = new ArrayList<>();
                for (List<String> key : keys) {
                    String cityName = key.get(0);
                    sortedCities.add(cityName);
                    String itemName = key.get(1);
                    String cCode = key.get(2);
                    String fullDescription = itemName + ", \nPrice: " + cityInfo.get(key)
                            + " " + cCode;
                    costsDescription.add(fullDescription);
                }
                bundle.putStringArrayList("Sorted Cities", sortedCities);
                bundle.putStringArrayList("Sorted Price Descriptions", costsDescription);
            }

            bundle.putString("Country Name", countryName);
            Fragment resultsFragment = new ResultsFragment();
            resultsFragment.setArguments(bundle);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, resultsFragment)
                    .commit();
        }
    }
}