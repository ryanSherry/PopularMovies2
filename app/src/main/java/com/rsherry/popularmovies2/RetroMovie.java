package com.rsherry.popularmovies2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroMovie {
    @SerializedName("id")
    private int mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("backdrop_path")
    private String mBackdrop_path;
    @SerializedName("vote_average")
    private double mVoteAverage;
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    public RetroMovie(int id, String title, String releaseDate, String overview, String posterPath, String backdropPath, double voteAverage) {
        mId = id;
        mTitle = title;
        mReleaseDate = releaseDate;
        mOverview = overview;
        mPosterPath = BASE_IMAGE_URL + posterPath;
        mBackdrop_path = BASE_IMAGE_URL + backdropPath;
        mVoteAverage = voteAverage;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getPosterPath() {
        return BASE_IMAGE_URL + mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getBackdrop_path() {
        return BASE_IMAGE_URL + mBackdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        mBackdrop_path = backdrop_path;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        mVoteAverage = voteAverage;
    }

}
