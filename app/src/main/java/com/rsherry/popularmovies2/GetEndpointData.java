package com.rsherry.popularmovies2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetEndpointData {
    @GET("movie/popular")
    retrofit2.Call<RetroMovieResults> getMoviesByPopularity(@Query("api_key") String api_key);

    @GET("movie/top_rated")
    retrofit2.Call<RetroMovieResults> getMoviesByRating (@Query("api_key") String api_key);

    @GET("movie/{id}/videos")
    retrofit2.Call<RetroTrailerResults> getMovieTrailers (@Path("id") int id, @Query("api_key") String api_key);

    @GET("movie/{id}/reviews")
    retrofit2.Call<RetroReviewResults> getMovieReviews (@Path("id") int id, @Query("api_key") String api_key);

}
