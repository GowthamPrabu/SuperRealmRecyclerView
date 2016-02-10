package com.tortelabs.superrealmrecyclerview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.tortelabs.superrealmrecyclerview.R;

/**
 * Created by gowtham on 10/02/16.
 */
public class SuperRealmRecyclerView extends FrameLayout {

    protected LAYOUT_MANAGER_TYPE layoutManagerType;
    protected int ITEM_LEFT_TO_LOAD_MORE;
    protected OnMoreListener mOnMoreListener;

    private int mSuperRealmRecyclerViewMainLayout;
    private boolean mClipToPadding;
    private int mPadding;
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mScrollbarStyle;
    private int mEmptyId;
    private int mMoreProgressId;
    private int mProgressId;
    private SwipeRefreshLayout mPtrLayout;
    private ViewStub mProgress;
    private View mProgressView;
    private ViewStub mMoreProgress;
    private View mMoreProgressView;
    private ViewStub mEmpty;
    private View mEmptyView;
    private RecyclerView mRecycler;
    private RecyclerView.OnScrollListener mExternalOnScrollListener;
    private boolean isLoadingMore;

    public SuperRealmRecyclerView(Context context) {
        super(context);
        initView();
    }

    public SuperRealmRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public SuperRealmRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.super_realm_recycler_view);
        try {
            mSuperRealmRecyclerViewMainLayout = a.getResourceId(R.styleable.super_realm_recycler_view_realm_mainLayoutId, R.layout.layout_progress_recyclerview);
            mClipToPadding = a.getBoolean(R.styleable.super_realm_recycler_view_realm_recyclerClipToPadding, false);
            mPadding = (int) a.getDimension(R.styleable.super_realm_recycler_view_realm_recyclerPadding, -1.0f);
            mPaddingTop = (int) a.getDimension(R.styleable.super_realm_recycler_view_realm_recyclerPaddingTop, 0.0f);
            mPaddingBottom = (int) a.getDimension(R.styleable.super_realm_recycler_view_realm_recyclerPaddingBottom, 0.0f);
            mPaddingLeft = (int) a.getDimension(R.styleable.super_realm_recycler_view_realm_recyclerPaddingLeft, 0.0f);
            mPaddingRight = (int) a.getDimension(R.styleable.super_realm_recycler_view_realm_recyclerPaddingRight, 0.0f);
            mScrollbarStyle = a.getInt(R.styleable.super_realm_recycler_view_realm_scrollbarStyle, -1);
            mEmptyId = a.getResourceId(R.styleable.super_realm_recycler_view_realm_layout_empty, 0);
            mMoreProgressId = a.getResourceId(R.styleable.super_realm_recycler_view_realm_layout_moreProgress, R.layout.layout_more_progress);
            mProgressId = a.getResourceId(R.styleable.super_realm_recycler_view_realm_layout_progress, R.layout.layout_progress);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        View v = LayoutInflater.from(getContext()).inflate(mSuperRealmRecyclerViewMainLayout, this);
        mPtrLayout = (SwipeRefreshLayout) v.findViewById(R.id.ptr_layout);
        mPtrLayout.setEnabled(false);

        mProgress = (ViewStub) v.findViewById(android.R.id.progress);

        mProgress.setLayoutResource(mProgressId);
        mProgressView = mProgress.inflate();

        mMoreProgress = (ViewStub) v.findViewById(R.id.more_progress);
        mMoreProgress.setLayoutResource(mMoreProgressId);
        if (mMoreProgressId != 0)
            mMoreProgressView = mMoreProgress.inflate();
        mMoreProgress.setVisibility(View.GONE);

        mEmpty = (ViewStub) v.findViewById(R.id.empty);
        mEmpty.setLayoutResource(mEmptyId);
        if (mEmptyId != 0)
            mEmptyView = mEmpty.inflate();
        mEmpty.setVisibility(View.GONE);

        initRecyclerView(v);
    }

    /**
     * Implement this method to customize the AbsListView
     */
    protected void initRecyclerView(View view) {
        View recyclerView = view.findViewById(android.R.id.list);

        if (recyclerView == null) {
            throw new IllegalArgumentException("SuperRealmRecyclerView's main layout should have RecyclerView with id `android.R.id.list`");
        }

        if (recyclerView instanceof RecyclerView)
            mRecycler = (RecyclerView) recyclerView;
        else
            throw new IllegalArgumentException("SuperRealmRecyclerView works with a RecyclerView!");

        mRecycler.setClipToPadding(mClipToPadding);
        RecyclerView.OnScrollListener mInternalOnScrollListener = new RecyclerView.OnScrollListener() {
            private int[] lastPositions;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mExternalOnScrollListener != null)
                    mExternalOnScrollListener.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();

                int lastVisibleItemPosition = -1;
                if (layoutManagerType == null) {
                    if (layoutManager instanceof GridLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
                    } else {
                        throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
                    }
                }

                if (((totalItemCount - lastVisibleItemPosition) <= ITEM_LEFT_TO_LOAD_MORE ||
                        (totalItemCount - lastVisibleItemPosition) == 0 && totalItemCount > visibleItemCount)
                        && !isLoadingMore) {

                    isLoadingMore = true;
                    if (mOnMoreListener != null) {
                        mMoreProgress.setVisibility(View.VISIBLE);
                        mOnMoreListener.onMoreAsked(mRecycler.getAdapter().getItemCount(), ITEM_LEFT_TO_LOAD_MORE, lastVisibleItemPosition);

                    }
                }

                if (mExternalOnScrollListener != null)
                    mExternalOnScrollListener.onScrolled(recyclerView, dx, dy);

            }
        };
        mRecycler.addOnScrollListener(mInternalOnScrollListener);

        if (mPadding != -1.0f) {
            mRecycler.setPadding(mPadding, mPadding, mPadding, mPadding);
        } else {
            mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        }

        if (mScrollbarStyle != -1) {
            mRecycler.setScrollBarStyle(mScrollbarStyle);
        }
    }

    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    /**
     * Set the layout manager to the recycler
     *
     * @param manager
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecycler.setLayoutManager(manager);
    }

    public void clear() {
        mRecycler.setAdapter(null);
    }

    public void showProgress() {
        hideRecycler();
        if (mEmptyId != 0) mEmpty.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
    }

    public void showRecycler() {
        hideProgress();
        mRecycler.setVisibility(View.VISIBLE);
    }

    public void showMoreProgress() {
        mMoreProgress.setVisibility(View.VISIBLE);

    }

    public void hideMoreProgress() {
        mMoreProgress.setVisibility(View.GONE);
    }

    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mPtrLayout.setEnabled(true);
        mPtrLayout.setOnRefreshListener(listener);
    }

    public void setRefreshingColorResources(@ColorRes int colRes1, @ColorRes int colRes2, @ColorRes int colRes3, @ColorRes int colRes4) {
        mPtrLayout.setColorSchemeResources(colRes1, colRes2, colRes3, colRes4);
    }

    public void setRefreshingColor(int col1, int col2, int col3, int col4) {
        mPtrLayout.setColorSchemeColors(col1, col2, col3, col4);
    }

    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    public void hideRecycler() {
        mRecycler.setVisibility(View.GONE);
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mExternalOnScrollListener = listener;
    }

    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.addOnItemTouchListener(listener);
    }

    public void removeOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.removeOnItemTouchListener(listener);
    }

    public RecyclerView.Adapter getAdapter() {
        return mRecycler.getAdapter();
    }

    /**
     * Set the adapter to the recycler
     * Automatically hide the progressbar
     * Set the refresh to false
     * If adapter is empty, then the emptyview is shown
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecycler.setAdapter(adapter);
        mProgress.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
        mPtrLayout.setRefreshing(false);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    super.onItemRangeChanged(positionStart, itemCount);
                    update();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    update();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    update();
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                    update();
                }

                @Override
                public void onChanged() {
                    super.onChanged();
                    update();
                }

                private void update() {
                    mProgress.setVisibility(View.GONE);
                    isLoadingMore = false;
                    mPtrLayout.setRefreshing(false);
                    if (mRecycler.getAdapter().getItemCount() == 0 && mEmptyId != 0) {
                        mEmpty.setVisibility(View.VISIBLE);
                    } else if (mEmptyId != 0) {
                        mEmpty.setVisibility(View.GONE);
                    }
                }
            });

        }
        if ((adapter == null || adapter.getItemCount() == 0) && mEmptyId != 0) {
            mEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void setOnMoreListener(OnMoreListener onMoreListener, int max) {
        mOnMoreListener = onMoreListener;
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    public void setOnMoreListener(OnMoreListener onMoreListener) {
        mOnMoreListener = onMoreListener;
    }

    public void setNumberBeforeMoreIsCalled(int max) {
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void setLoadingMore(boolean isLoadingMore) {
        this.isLoadingMore = isLoadingMore;
    }

    public void removeMoreListener() {
        mOnMoreListener = null;
    }


    public void setOnTouchListener(OnTouchListener listener) {
        mRecycler.setOnTouchListener(listener);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecycler.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.removeItemDecoration(itemDecoration);
    }

    public View getProgressView() {
        return mProgressView;
    }

    public View getMoreProgressView() {
        return mMoreProgressView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }

    public interface OnMoreListener {
        /**
         * @param overallItemsCount
         * @param itemsBeforeMore
         * @param maxLastVisiblePosition for staggered grid this is max of all spans
         */
        void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition);
    }
}
