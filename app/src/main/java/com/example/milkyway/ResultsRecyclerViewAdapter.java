package com.example.milkyway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ResultsRecyclerViewAdapter extends RecyclerView.Adapter<ResultsRecyclerViewAdapter.MyViewHolder> {

    private final Context c;
    private final String[] cities;
    private final String[] costs;
    private final String url;
    private ResultsItemClickListener clickListener;

    public ResultsRecyclerViewAdapter(Context c, String[] cities, String[] costs, String url) {
        this.c = c;
        this.cities = cities;
        this.costs = costs;
        this.url = url;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view;
        if (cities[0].equals("No results were found.")) {
            view = inflater.inflate(R.layout.results_error_layout, parent, false);
        } else {
            view = inflater.inflate(R.layout.results_row_layout, parent, false);
        }
        return new MyViewHolder((view));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.text1.setText(cities[position]);
        holder.text2.setText(costs[position]);
        if (holder.image != null) {
            Picasso.get()
                    .load(url)
                    .resize(800, 600) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.image_not_found)
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return cities == null ? 0 : cities.length;
    }

    public void setClickListener(ResultsItemClickListener resultsItemClickListener) {
        this.clickListener = resultsItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView text1;
        private final TextView text2;
        private final ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.city);
            text2 = itemView.findViewById(R.id.cost);
            image = itemView.findViewById(R.id.imageView);
            Button button = itemView.findViewById(R.id.favoriteButton);
            itemView.setOnClickListener(this);
            if (image != null) {
                image.setOnClickListener(this);
            }
            if (button != null) {
                button.setOnClickListener(this);
            }
        }

        public void onClick(View itemView) {
            if (clickListener != null) {
                String id = itemView.getResources().getResourceName(itemView.getId());
                if (id.contains("favoriteButton")) {
                    clickListener.onClickFavorites(itemView, getBindingAdapterPosition());
                } else {
                    clickListener.onClickCitySummary(itemView, getBindingAdapterPosition());
                }
            }
        }
    }
}
