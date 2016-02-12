package com.tortelabs.realmrecyclerview.adapter.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.tortelabs.realmrecyclerview.R;
import com.tortelabs.realmrecyclerview.pojo.Movie;
import com.tortelabs.superrealmrecyclerview.adapter.RealmViewHolder;

/**
 * Created by gowtham on 11/02/16.
 */
public class MovieViewHolder extends RealmViewHolder {

    TextView tvTitle;
    TextView tvGenre;

    public MovieViewHolder(View itemView) {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvGenre = (TextView) itemView.findViewById(R.id.tvGenre);
    }

    public void populateView(Movie movie) {
        tvTitle.setText(movie.getTitle());
        tvGenre.setText(movie.getGenres().replace("|"," | "));
    }
}
