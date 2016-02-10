package com.tortelabs.superrealmrecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by gowtham on 10/02/16.
 */
public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter<RealmViewHolder> {

    private final RealmChangeListener mListener;
    Context context;
    RealmResults<T> realmResults;

    public RealmRecyclerViewAdapter(Context context,
                                    RealmResults<T> realmResults,
                                    boolean automaticUpdate) {
        this.context = context;
        this.realmResults = realmResults;
        this.mListener = automaticUpdate ? getRealmListener() : null;
    }

    @Override
    public final RealmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public final void onBindViewHolder(RealmViewHolder holder, int position) {

    }

    public abstract RealmViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType);

    public abstract void onBindRealmViewHolder(RealmViewHolder holder, int position);

    private RealmChangeListener getRealmListener() {

        return new RealmChangeListener() {
            @Override
            public void onChange() {
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param queryResults the new RealmResults coming from the new query.
     */
    public void updateRealmResults(RealmResults<T> queryResults) {
        if (mListener != null && realmResults != null) {
            realmResults.removeChangeListener(mListener);
        }

        this.realmResults = queryResults;

        if (realmResults != null && mListener != null) {
            realmResults.addChangeListener(mListener);
        }

        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return 0;
    }
}
