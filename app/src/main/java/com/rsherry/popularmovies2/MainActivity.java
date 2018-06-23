package com.rsherry.popularmovies2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.rsherry.popularmovies2.database.AppDatabase;
import com.rsherry.popularmovies2.model.RetroMovieResults;
import com.rsherry.popularmovies2.networking.GetEndpointData;
import com.rsherry.popularmovies2.networking.RetrofitClentInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ListItemClickListener {
    private MovieAdapter mAdapter;
    private static final String API_KEY = ApiKey.getApiKey();
    RecyclerView mRecyclerView;
    List<RetroMovie> mMovies;
    AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getsInstance(getApplicationContext());

        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);
        Call<RetroMovieResults> call = service.getMoviesByPopularity(API_KEY);
        call.enqueue(new Callback<RetroMovieResults>() {
            @Override
            public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                mMovies = response.body().getResults();
                generateMovieList(mMovies);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<RetroMovieResults> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(getApplicationContext(),"network failure",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"conversion issue",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_sort_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sort_highest_rated:
                sortByHighestRated();
                break;
            case R.id.sort_popular:
                sortByMostPopular();
                break;
            case R.id.view_favorites:
                viewFavorites();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewFavorites() {
        mMovies = mDb.movieFavoritesDao().loadAllFavoriteMovies();
        generateMovieList(mMovies);
    }

    public void sortByMostPopular(){
        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);
        Call<RetroMovieResults> call = service.getMoviesByPopularity(API_KEY);
        call.enqueue(new Callback<RetroMovieResults>() {
            @Override
            public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                mMovies = response.body().getResults();
                generateMovieList(mMovies);
            }

            @Override
            public void onFailure(Call<RetroMovieResults> call, Throwable t) {

            }
        });
    }

    public void sortByHighestRated() {
        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);
        Call<RetroMovieResults> call = service.getMoviesByRating(API_KEY);
        call.enqueue(new Callback<RetroMovieResults>() {
            @Override
            public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                mMovies = response.body().getResults();
                generateMovieList(mMovies);
                Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<RetroMovieResults> call, Throwable t) {

            }
        });
    }

    private void generateMovieList(List<RetroMovie> movieList) {
        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new MovieAdapter(movieList,this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        final RetroMovie movie = mMovies.get(clickedItemIndex);
        Intent intent = new Intent(this,MovieDetailActivity.class);
        intent.putExtra("MOVIE",movie);
        this.startActivity(intent);
    }
}
