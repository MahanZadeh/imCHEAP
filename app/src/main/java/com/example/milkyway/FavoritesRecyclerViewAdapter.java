package com.example.milkyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.MyViewHolder> {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    Context c;
    ArrayList<String> keys, countries, cities, costs;
    String url;
    private FavoritesItemClickListener clickListener;

    public FavoritesRecyclerViewAdapter(Context c,ArrayList<String> keys,
                                        ArrayList<String> countries, ArrayList<String> cities,
                                        ArrayList<String> costs, String url) {
        this.c = c;
        this.keys = keys;
        this.countries = countries;
        this.cities = cities;
        this.costs = costs;
        this.url = url;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.favorites_row_layout, parent, false);
        return new MyViewHolder((view));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.text1.setText(cities.get(position));
        holder.text2.setText(costs.get(position));
        Picasso.get()
                .load(url + countries.get(position))
                .resize(800, 600) // resizes the image to these dimensions (in pixel)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.image_not_found)
                .into(holder.image);
        holder.hiddenInfoKey.setText(keys.get(position));
//        holder.hiddenInfoKey.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount()  {
        return cities == null ? 0 : cities.size();
    }

    public void setClickListener(FavoritesItemClickListener favoritesItemClickListener) {
        this.clickListener = favoritesItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text1, text2, hiddenInfoKey;
        ImageView image;
        Button button;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.cityFav);
            text2 = itemView.findViewById(R.id.costFav);
            image = itemView.findViewById(R.id.imageViewFav);
            button = itemView.findViewById(R.id.deleteButton);
            hiddenInfoKey = itemView.findViewById(R.id.hiddenInfoKey);

            itemView.setOnClickListener(this);
            image.setOnClickListener(this);
            button.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                String id = view.getResources().getResourceName(view.getId());
                if (id.contains("deleteButton")) {
                    clickListener.onClickDelete(view, getBindingAdapterPosition());
                } else {
                    clickListener.onClickCitySummary(view, getBindingAdapterPosition());
                }
            }
        }
    }
}