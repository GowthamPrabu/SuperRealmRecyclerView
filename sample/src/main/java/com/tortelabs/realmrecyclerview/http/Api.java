package com.tortelabs.realmrecyclerview.http;

import com.tortelabs.realmrecyclerview.pojo.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by gowtham on 11/02/16.
 */
public interface Api {
    @GET("movie-list")
    Call<ApiResponse> listMovies(@Query("page") int page);
}
