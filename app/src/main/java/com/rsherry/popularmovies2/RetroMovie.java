package com.rsherry.popularmovies2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroMovie implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mOverview);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdrop_path);
        dest.writeDouble(mVoteAverage);
    }

    private RetroMovie (Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mOverview = in.readString();
        mPosterPath = in.readString();
        mBackdrop_path = in.readString();
        mVoteAverage = in.readDouble();
    }

    public static final Creator<RetroMovie> CREATOR = new Creator<RetroMovie>() {
        @Override
        public RetroMovie createFromParcel(Parcel source) {
            return new RetroMovie(source);
        }

        @Override
        public RetroMovie[] newArray(int size) {
            return new RetroMovie[size];
        }
    };
}
