package com.rsherry.popularmovies2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroReviewResults {
    @SerializedName("id")
    private int mId;
    @SerializedName("results")
    private List<RetroReview> mReviews;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public List<RetroReview> getReviews() {
        return mReviews;
    }

    public void setReviews(List<RetroReview> reviews) {
        mReviews = reviews;
    }
}
