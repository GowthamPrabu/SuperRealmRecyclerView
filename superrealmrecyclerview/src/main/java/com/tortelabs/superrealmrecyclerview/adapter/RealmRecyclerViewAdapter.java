package com.tortelabs.superrealmrecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.tortelabs.superrealmrecyclerview.widget.SuperRealmRecyclerView;

import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by gowtham on 10/02/16.
 */
public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter<RealmViewHolder> {

    private static final int LOAD_MORE_VIEW_TYPE = 0x312435;

    private final RealmChangeListener mListener;
    Context context;
    RealmResults<T> realmResults;
    SuperRealmRecyclerView.loadMoreLayoutType loadMoreType;
    private View loadMoreView;

    public RealmRecyclerViewAdapter(Context context,
                                    RealmResults<T> realmResults,
                                    boolean automaticUpdate,
                                    SuperRealmRecyclerView.loadMoreLayoutType loadMoreType) {
        this.context = context;
        this.realmResults = realmResults;
        this.mListener = automaticUpdate ? getRealmListener() : null;
        this.loadMoreType = loadMoreType;
    }

    public RealmRecyclerViewAdapter(Context context,
                                    RealmResults<T> realmResults,
                                    boolean automaticUpdate) {
        this.context = context;
        this.realmResults = realmResults;
        this.mListener = automaticUpdate ? getRealmListener() : null;
        this.loadMoreType = SuperRealmRecyclerView.loadMoreLayoutType.FOOTER;
    }

    @Override
    public final RealmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOAD_MORE_VIEW_TYPE) {
            if (loadMoreView == null) {
                throw new IllegalStateException(
                        "Load More View cannot be null. " +
                                "Don't set the adapter using SuperRealmRecyclerView.getRecyclerView().getAdapter(). " +
                                "Always use SuperRecyclerView.getAdapter()");
            }
            return new RealmViewHolder(loadMoreView);
        }
        return onCreateRealmViewHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(RealmViewHolder holder, int position) {
        if (loadMoreType == SuperRealmRecyclerView.loadMoreLayoutType.FOOTER && position == getItemCount()) {
            // staggered layout set full span

            if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
            }
            return;
        }
        onBindRealmViewHolder(holder, position);
    }

    public abstract RealmViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType);

    public abstract void onBindRealmViewHolder(RealmViewHolder holder, int position);

    public abstract int getRealmItemCount();

    public int getRealmItemViewType(int position) {
        return super.getItemViewType(position);
    }

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
    public final int getItemCount() {
        int count = getRealmItemCount();
        if (loadMoreType == SuperRealmRecyclerView.loadMoreLayoutType.FOOTER) {
            count++;
        }
        return count;
    }

    @Override
    public final int getItemViewType(int position) {
        if (loadMoreType == SuperRealmRecyclerView.loadMoreLayoutType.FOOTER && position == getItemCount()) {
            return LOAD_MORE_VIEW_TYPE;
        } else {
            return getRealmItemViewType(position);
        }
    }

    public SuperRealmRecyclerView.loadMoreLayoutType getLoadMoreType() {
        return loadMoreType;
    }

    public void setLoadMoreView(View loadMoreView) {
        this.loadMoreView = loadMoreView;
    }
}
