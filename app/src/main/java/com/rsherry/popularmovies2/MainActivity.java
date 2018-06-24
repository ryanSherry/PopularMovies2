package com.rsherry.popularmovies2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
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
    private static final String SAVED_LAYOUT_MANAGER = "SAVED_LAYOUT_MANAGER";
    private static final String SAVED_MOVIE_LIST = "SAVED_MOVIE_LIST";
    private MovieAdapter mAdapter;
    private static final String API_KEY = ApiKey.getApiKey();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    List<RetroMovie> mMovies;
    AppDatabase mDb;
    Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getsInstance(getApplicationContext());

        if(savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
            mMovies = savedInstanceState.getParcelableArrayList(SAVED_MOVIE_LIST);
        }

        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);
        Call<RetroMovieResults> call = service.getMoviesByPopularity(API_KEY);
        call.enqueue(new Callback<RetroMovieResults>() {
            @Override
            public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                if(mMovies == null) {
                    mMovies = response.body().getResults();
                    Log.i("networkcall","made a network call");
                }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SAVED_LAYOUT_MANAGER, mListState);
        outState.putParcelableArrayList(SAVED_MOVIE_LIST,((ArrayList<RetroMovie>)mMovies));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        if(savedInstanceState != null) {
//            mListState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
//            mMovies = savedInstanceState.getParcelableArrayList(SAVED_MOVIE_LIST);
//        }
//        if (mListState != null) {
//            mLayoutManager.onRestoreInstanceState(mListState);
//        }
        super.onRestoreInstanceState(savedInstanceState);
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
        final LiveData<List<RetroMovie>> favoriteMovies = mDb.movieFavoritesDao().loadAllFavoriteMovies();
        favoriteMovies.observe(this, new Observer<List<RetroMovie>>() {
            @Override
            public void onChanged(@Nullable List<RetroMovie> retroMovies) {
                mMovies = retroMovies;
                generateMovieList(retroMovies);
            }
        });

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
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(this,2);
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
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
