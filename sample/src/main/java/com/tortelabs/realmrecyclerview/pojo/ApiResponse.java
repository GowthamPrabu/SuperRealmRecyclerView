package com.tortelabs.realmrecyclerview.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gowtham on 11/02/16.
 */
public class ApiResponse {
    @SerializedName("total")
    private int total;
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("per_page")
    private int perPage;
    @SerializedName("data")
    private ArrayList<Movie> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public ArrayList<Movie> getData() {
        return data;
    }

    public void setData(ArrayList<Movie> data) {
        this.data = data;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }
}
