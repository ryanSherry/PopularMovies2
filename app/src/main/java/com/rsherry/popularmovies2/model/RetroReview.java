package com.rsherry.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RetroReview implements Parcelable {
    @SerializedName("author")
    private String mAuthor;
    @SerializedName("content")
    private String mContent;
    @SerializedName("url")
    private String mUrl;
    private static final String BASE_URL = "https://www.youtube.com/watch?v=";

    public RetroReview(String author, String content, String url) {
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    public String getAuthor() {
        return mAuthor + " writes:";
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getUrl() {
        return BASE_URL + mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }

    protected RetroReview(Parcel in) {
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<RetroReview> CREATOR = new Creator<RetroReview>() {
        @Override
        public RetroReview createFromParcel(Parcel in) {
            return new RetroReview(in);
        }

        @Override
        public RetroReview[] newArray(int size) {
            return new RetroReview[size];
        }
    };
}
