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
    private static final String SAVED_SORTING_OPTION = "SAVED_SORTING_OPTION";
    public static final String SAVED_FAVORITE_LIST = "SAVED_FAVORITE_LIST";
    private MovieAdapter mAdapter;
    private static final String API_KEY = ApiKey.getApiKey();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    List<RetroMovie> mMovies;
    List<RetroMovie> mHighestRated;
    List<RetroMovie> mMostPopular;
    List<RetroMovie> mFavorites;
    AppDatabase mDb;
    Parcelable mListState;

    public enum SortingBy {
        MOST_POPULAR, HIGHEST_RATED, FAVORITES
    }

    SortingBy mSortingBy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getsInstance(getApplicationContext());

        if(savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
            mMovies = savedInstanceState.getParcelableArrayList(SAVED_MOVIE_LIST);
            String mSortingByValue = savedInstanceState.getString(SAVED_SORTING_OPTION);
            mSortingBy = SortingBy.valueOf(mSortingByValue);

        } else {
            mSortingBy = SortingBy.MOST_POPULAR;
        }

        if(mFavorites == null) {
            loadSavedFavorites();
        }

        switch (mSortingBy) {
            case MOST_POPULAR:
                sortByMostPopular();
                break;
            case HIGHEST_RATED:
                sortByHighestRated();
            case FAVORITES:
                viewFavorites();
        }

//        if(mMovies == null) {
//        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);
//            Call<RetroMovieResults> call = service.getMoviesByPopularity(API_KEY);
//            call.enqueue(new Callback<RetroMovieResults>() {
//                @Override
//                public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
//                        mMovies = response.body().getResults();
//                        mMostPopular = mMovies;
//                        Log.i("networkcall", "made a network call");
//                    generateMovieList(mMovies);
//                    mAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onFailure(Call<RetroMovieResults> call, Throwable t) {
//                    if (t instanceof IOException) {
//                        Toast.makeText(getApplicationContext(), "There is currently no network connection", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "This is a software bug. Please contact the developer of this app to investigate", Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//        }
   }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SAVED_LAYOUT_MANAGER, mListState);
        outState.putParcelableArrayList(SAVED_MOVIE_LIST,(ArrayList<RetroMovie>)mMovies);
//        outState.putParcelableArrayList(SAVED_FAVORITE_LIST,(ArrayList<RetroMovie>)mFavorites);
        outState.putString(SAVED_SORTING_OPTION, mSortingBy.name());
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
        mSortingBy = SortingBy.FAVORITES;
        if(mFavorites.size() > 0) {
            mMovies = mFavorites;
            generateMovieList(mMovies);
        } else {
            Toast.makeText(getApplicationContext(), "There are currently no saved favorites", Toast.LENGTH_LONG).show();
        }
    }

    private void loadSavedFavorites() {
        final LiveData<List<RetroMovie>> favoriteMovies = mDb.movieFavoritesDao().loadAllFavoriteMovies();
        favoriteMovies.observe(this, new Observer<List<RetroMovie>>() {
            @Override
            public void onChanged(@Nullable List<RetroMovie> retroMovies) {
                mFavorites = retroMovies;
                if (mSortingBy == SortingBy.FAVORITES){
                    mMovies = mFavorites;
                    mAdapter = new MovieAdapter(mMovies,MainActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                    if (mMovies.size() < 1) {
                        Toast.makeText(getApplicationContext(), "There are currently no saved favorites", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void sortByHighestRated() {
        mSortingBy = SortingBy.HIGHEST_RATED;
        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);
        if(mHighestRated != null) {
            mMovies = mHighestRated;
            generateMovieList(mMovies);
        }
        else {
            Call<RetroMovieResults> call = service.getMoviesByRating(API_KEY);
            call.enqueue(new Callback<RetroMovieResults>() {
                @Override
                public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                    mMovies = response.body().getResults();
                    mHighestRated = mMovies;
                    generateMovieList(mMovies);
                    Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<RetroMovieResults> call, Throwable t) {
                    if (t instanceof IOException) {
                        Toast.makeText(getApplicationContext(), "There is currently no network connection", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "This is a software bug. Please contact the developer of this app to                        investigate", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void sortByMostPopular(){
        mSortingBy = SortingBy.MOST_POPULAR;
        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);
        if(mMostPopular != null) {
            mMovies = mMostPopular;
            generateMovieList(mMovies);
        } else {

            Call<RetroMovieResults> call = service.getMoviesByPopularity(API_KEY);
            call.enqueue(new Callback<RetroMovieResults>() {
                @Override
                public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                    mMovies = response.body().getResults();
                    mMostPopular = mMovies;
                    generateMovieList(mMovies);
                }

                @Override
                public void onFailure(Call<RetroMovieResults> call, Throwable t) {
                    if (t instanceof IOException) {
                        Toast.makeText(getApplicationContext(), "There is currently no network connection", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "This is a software bug. Please contact the developer of this app to                        investigate", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
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
        intent.putParcelableArrayListExtra(SAVED_FAVORITE_LIST,(ArrayList<RetroMovie>)mFavorites);
        this.startActivity(intent);
    }
}
