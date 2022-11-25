package com.example.milkyway;

import android.view.View;

public interface FavoritesItemClickListener {
    void onClickCitySummary(View view, int position);

    void onClickDelete(View view, int position);
}
