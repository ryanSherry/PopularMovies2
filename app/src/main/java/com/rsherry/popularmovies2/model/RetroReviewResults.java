package com.rsherry.popularmovies2.model;

import com.google.gson.annotations.SerializedName;
import com.rsherry.popularmovies2.model.RetroReview;

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
