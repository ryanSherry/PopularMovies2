package com.rsherry.popularmovies2;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private List<RetroMovie> mMovies;
    private MovieAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);
        Call<List<RetroMovie>> call = service.getMoviesByPopularity(ApiKey.getApiKey());
        call.enqueue(new Callback<List<RetroMovie>>() {
            @Override
            public void onResponse(Call<List<RetroMovie>> call, Response<List<RetroMovie>> response) {
                generateMovieList(response.body());
            }

            @Override
            public void onFailure(Call<List<RetroMovie>> call, Throwable t) {

            }
        });
    }
    private void generateMovieList(List<RetroMovie> movieList) {
        mAdapter = new MovieAdapter(mMovies);
        mLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
