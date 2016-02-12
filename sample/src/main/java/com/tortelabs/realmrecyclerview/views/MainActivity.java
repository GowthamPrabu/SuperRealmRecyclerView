package com.tortelabs.realmrecyclerview.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tortelabs.realmrecyclerview.R;
import com.tortelabs.realmrecyclerview.adapter.MovieAdapter;
import com.tortelabs.realmrecyclerview.db.RealmDbHelper;
import com.tortelabs.realmrecyclerview.http.Api;
import com.tortelabs.realmrecyclerview.pojo.ApiResponse;
import com.tortelabs.realmrecyclerview.pojo.Movie;
import com.tortelabs.superrealmrecyclerview.widget.SuperRealmRecyclerView;

import io.realm.RealmObject;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SuperRealmRecyclerView.OnMoreListener {

    SuperRealmRecyclerView list;
    Api api;

    private int currentPage = 0;
    private int totalPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (SuperRealmRecyclerView) findViewById(R.id.SuperRealmRecyclerView);
        setupApi();
        setupRecyclerView();
        loadDataFromApi(currentPage + 1);
    }

    private void loadDataFromApi(int page) {
        list.hideMoreProgress();
        if (page == 1) {
            //clear all old db data
            RealmDbHelper.clearDb(MainActivity.this);
        }
        Call<ApiResponse> call = api.listMovies(page);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> res) {
                ApiResponse response = res.body();
                RealmDbHelper.saveMovies(response.getData(), MainActivity.this);
                if (currentPage == 0) {
                    loadAdapter();
                }
                currentPage = response.getCurrentPage();
                totalPages = response.getTotal() / response.getPerPage();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAdapter() {
        RealmResults<Movie> realmResults = RealmDbHelper.getAllMovies(MainActivity.this);
        MovieAdapter adapter = new MovieAdapter(this,
                realmResults,
                true,
                SuperRealmRecyclerView.loadMoreLayoutType.FOOTER);
        list.setAdapter(adapter);

    }

    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        list.setLayoutManager(manager);
        //load next page when there are 5 items left
        list.setOnMoreListener(this, 5);

    }

    private void setupApi() {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.138:8000")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        api = retrofit.create(Api.class);
    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
        if (currentPage == totalPages) {
            list.hideMoreProgress();
            return;
        }
        list.showMoreProgress();
        loadDataFromApi(currentPage + 1);
    }
}
