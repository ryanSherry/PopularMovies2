package com.rsherry.popularmovies2;

import com.google.gson.annotations.SerializedName;

public class RetroMovie {
    @SerializedName("id")
    private String mId;
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

    public RetroMovie(String id, String title, String releaseDate, String overview, String posterPath, String backdrop_path, double voteAverage) {
        mId = id;
        mTitle = title;
        mReleaseDate = releaseDate;
        mOverview = overview;
        mPosterPath = posterPath;
        mBackdrop_path = backdrop_path;
        mVoteAverage = voteAverage;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
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
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = BASE_IMAGE_URL + posterPath;
    }

    public String getBackdrop_path() {
        return mBackdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        mBackdrop_path = BASE_IMAGE_URL + backdrop_path;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        mVoteAverage = voteAverage;
    }
}
