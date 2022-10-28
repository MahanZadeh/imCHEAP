package com.example.milkyway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    Context c;
    String[] cities, costs;
    int[] images;
    private ResultsItemClickListener clickListener;

    public MyRecyclerViewAdapter(Context c, String[] cities, String[] costs, int[] images) {
        this.c = c;
        this.cities = cities;
        this.costs = costs;
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.results_row_layout, parent, false);
        return new MyViewHolder((view));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.text1.setText(cities[position]);
        holder.text2.setText(costs[position]);
        holder.image.setImageResource(images[0]);
    }

    @Override
    public int getItemCount() {
        return cities.length;
    }

    public void setClickListener(ResultsItemClickListener resultsItemClickListener) {
        this.clickListener = resultsItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text1, text2;
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.city);
            text2 = itemView.findViewById(R.id.cost);
            image = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
//            image.setOnClickListener(this);
        }

        public void onClick(View itemView) {
            if (clickListener != null) clickListener.onClick(itemView, getBindingAdapterPosition());
        }
    }
}
