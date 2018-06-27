package com.rsherry.popularmovies2;

import com.google.gson.annotations.SerializedName;

public class RetroReview {
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
}
