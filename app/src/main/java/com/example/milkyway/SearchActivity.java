package com.example.milkyway;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        setNationalitySpinner();
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

}
