package com.rsherry.popularmovies2;

import java.util.List;

import okhttp3.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetEndpointData {
    @GET("/3/movie/popular?api_key={api_key}")
    retrofit2.Call<List<RetroMovie>> getMoviesByPopularity(@Path("api_key") String api_key);

    @GET("/3/movie/top_rated?api_key={api_key}")
    retrofit2.Call<List<RetroMovie>> getMoviesByRating (@Path("api_key") String api_key);
}
