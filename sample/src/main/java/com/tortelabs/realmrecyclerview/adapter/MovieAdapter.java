package com.tortelabs.realmrecyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tortelabs.realmrecyclerview.R;
import com.tortelabs.realmrecyclerview.adapter.viewHolder.MovieViewHolder;
import com.tortelabs.realmrecyclerview.pojo.Movie;
import com.tortelabs.superrealmrecyclerview.adapter.RealmRecyclerViewAdapter;
import com.tortelabs.superrealmrecyclerview.adapter.RealmViewHolder;
import com.tortelabs.superrealmrecyclerview.widget.SuperRealmRecyclerView;

import io.realm.RealmResults;

/**
 * Created by gowtham on 11/02/16.
 */
public class MovieAdapter extends RealmRecyclerViewAdapter<Movie> {

    private RealmResults<Movie> realmResults;
    private Context context;

    public MovieAdapter(Context context, RealmResults<Movie> realmResults, boolean automaticUpdate, SuperRealmRecyclerView.loadMoreLayoutType loadMoreType) {
        super(context, realmResults, automaticUpdate, loadMoreType);

        this.realmResults = realmResults;
        this.context = context;
    }

    @Override
    public RealmViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.movie_single_item, viewGroup, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindRealmViewHolder(RealmViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder){
            MovieViewHolder viewHolder = (MovieViewHolder) holder;
            viewHolder.populateView(realmResults.get(position));
        }
    }

    @Override
    public int getRealmItemCount() {
        return realmResults.size();
    }
}
