package com.rsherry.popularmovies2;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "favorite_movies")
public class RetroMovie implements Parcelable {
    @PrimaryKey
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
    private String mBackdropPath;

    @SerializedName("vote_average")
    private double mVoteAverage;

    @Ignore public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    public RetroMovie(int id, String title, String releaseDate, String overview, String posterPath, String backdropPath, double voteAverage) {
        mId = id;
        mTitle = title;
        mReleaseDate = releaseDate;
        mOverview = overview;
        mPosterPath = posterPath;
        mBackdropPath = backdropPath;
        mVoteAverage = voteAverage;
    }


    public int getId() {
        return mId;
    }

    public void setId(int id) {
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

    public String getPosterUrl() {
        return BASE_IMAGE_URL + mPosterPath;
    }

    public String getBaseImageUrl() {
        return BASE_IMAGE_URL;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public String getBackDropUrl() {
        return BASE_IMAGE_URL + mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        mBackdropPath = backdropPath;
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
        dest.writeString(mBackdropPath);
        dest.writeDouble(mVoteAverage);
    }

    private RetroMovie (Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mOverview = in.readString();
        mPosterPath = in.readString();
        mBackdropPath = in.readString();
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
