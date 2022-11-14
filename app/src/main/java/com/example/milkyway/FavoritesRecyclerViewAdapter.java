package com.example.milkyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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

import java.util.ArrayList;

public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.MyViewHolder> {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    Context c;
    ArrayList<String> keys, countries, cities, costs;
    int[] images;
//    private ResultsItemClickListener clickListener;

    public FavoritesRecyclerViewAdapter(Context c,ArrayList<String> keys, ArrayList<String> countries, ArrayList<String> cities, ArrayList<String> costs, int[] images) {
        this.c = c;
        this.keys = keys;
        this.countries = countries;
        this.cities = cities;
        this.costs = costs;
        this.images = images;
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
        holder.image.setImageResource(images[0]);
        holder.hiddenInfoKey.setText(keys.get(position));
//        holder.hiddenInfoKey.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount()  {
        return cities.size();
    }

//    public void setClickListener(View.OnClickListener lis)

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text1, text2, text3, hiddenInfoKey;
        ImageView image;
        Button button;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.countryFav);
            text2 = itemView.findViewById(R.id.costFav);
            text3 = itemView.findViewById(R.id.cityFav);
            image = itemView.findViewById(R.id.imageViewFav);
            button = itemView.findViewById(R.id.deleteButton);
            hiddenInfoKey = itemView.findViewById(R.id.hiddenInfoKey);
//            itemView.setOnClickListener(this);
//            image.setOnClickListener(this);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(c.getApplicationContext(), "DELETED.", Toast.LENGTH_SHORT).show();


                    user = FirebaseAuth.getInstance().getCurrentUser();
                    reference = FirebaseDatabase.getInstance().getReference("Fav");
                    userID = user.getUid();
                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Fav")
                            .child(userID);
                    dr.child(hiddenInfoKey.getText().toString()).removeValue();
//                    Toast.makeText(c.getApplicationContext(), "Huh", Toast.LENGTH_SHORT).show();

                }
            });
        }


    }
}