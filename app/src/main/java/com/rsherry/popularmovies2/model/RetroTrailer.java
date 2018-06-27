package com.rsherry.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RetroTrailer implements Parcelable {
    @SerializedName("id")
    private String mId;
    @SerializedName("key")
    private String mKey;
    @SerializedName("name")
    private String mName;
    @SerializedName("site")
    private String mSite;
    @SerializedName("type")
    private String mType;
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    public RetroTrailer(String id, String key, String name, String site, String type) {
        mId = id;
        mKey = key;
        mName = name;
        mSite = site;
        mType = type;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSite() {
        return mSite;
    }

    public void setSite(String site) {
        mSite = site;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getUrlString(String url) {
        return YOUTUBE_BASE_URL + url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mSite);
        dest.writeString(mType);
    }

    protected RetroTrailer(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
        mType = in.readString();
    }

    public static final Creator<RetroTrailer> CREATOR = new Creator<RetroTrailer>() {
        @Override
        public RetroTrailer createFromParcel(Parcel in) {
            return new RetroTrailer(in);
        }

        @Override
        public RetroTrailer[] newArray(int size) {
            return new RetroTrailer[size];
        }
    };
}
