package com.rsherry.popularmovies2;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private MovieAdapter mAdapter;
    public static final String API_KEY = ApiKey.getApiKey();
    RecyclerView mRecyclerView;
    List<RetroMovie> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);
        Call<RetroMovieResults> call = service.getMoviesByPopularity(API_KEY);
        call.enqueue(new Callback<RetroMovieResults>() {
            @Override
            public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                mMovies = response.body().getResults();
                generateMovieList(mMovies);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<RetroMovieResults> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(getApplicationContext(),"actual network failure",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"conversion issue",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void generateMovieList(List<RetroMovie> movieList) {
        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new MovieAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
