package com.rsherry.popularmovies2.model;

import com.google.gson.annotations.SerializedName;
import com.rsherry.popularmovies2.model.RetroTrailer;

import java.util.List;

public class RetroTrailerResults {
    @SerializedName("id")
    private int mId;
    @SerializedName("results")
    private List<RetroTrailer> mTrailers;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public List<RetroTrailer> getTrailers() {
        return mTrailers;
    }

    public void setTrailers(List<RetroTrailer> trailers) {
        mTrailers = trailers;
    }
}
