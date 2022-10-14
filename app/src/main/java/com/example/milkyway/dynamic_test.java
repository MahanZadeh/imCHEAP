package com.example.milkyway;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class dynamic_test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_test);

        TableLayout tableLayout1 = findViewById(R.id.table_layout1); // here we grab the tablelayout

        Random random = new Random();
        for(int i=1; i<11; i++) {
            TableRow tableRow = new TableRow(this); //making a row

            TextView textView = new TextView(this); //making the text for that row
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
}