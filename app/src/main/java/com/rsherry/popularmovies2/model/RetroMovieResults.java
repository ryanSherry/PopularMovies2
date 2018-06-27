package com.rsherry.popularmovies2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroMovieResults {

    @SerializedName("page")
    private int mPage;
    @SerializedName("total_results")
    private int mTotalResults;
    @SerializedName("total_pages")
    private int mTotalPages;
    @SerializedName("results")
    private List<RetroMovie> mResults;

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public int getTotalResults() {
        return mTotalResults;
    }

    public void setTotalResults(int totalResults) {
        mTotalResults = totalResults;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(int totalPages) {
        mTotalPages = totalPages;
    }

    public List<RetroMovie> getResults() {
        return mResults;
    }

    public void setResults(List<RetroMovie> results) {
        mResults = results;
    }
}
