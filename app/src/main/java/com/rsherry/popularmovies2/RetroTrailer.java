package com.rsherry.popularmovies2;

import com.google.gson.annotations.SerializedName;

public class RetroTrailer {
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

}
