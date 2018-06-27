package com.rsherry.popularmovies2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
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
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SAVED_HIGHEST_RATED = "SAVED_HIGHEST_RATED";
    private static final String SAVED_MOST_POPULAR = "SAVED_MOST_POPULAR";
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
            mMostPopular = savedInstanceState.getParcelableArrayList(SAVED_MOST_POPULAR);
            mHighestRated = savedInstanceState.getParcelableArrayList(SAVED_HIGHEST_RATED);
            String mSortingByValue = savedInstanceState.getString(SAVED_SORTING_OPTION);
            mSortingBy = SortingBy.valueOf(mSortingByValue);

        }
        else {
            mSortingBy = SortingBy.MOST_POPULAR;
        }

        loadSavedFavorites();

        switch (mSortingBy) {
            case MOST_POPULAR:
                sortByMostPopular();
                break;
            case HIGHEST_RATED:
                sortByHighestRated();
            case FAVORITES:
                viewFavorites(mFavorites);
        }

   }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SAVED_LAYOUT_MANAGER, mListState);
        outState.putParcelableArrayList(SAVED_MOVIE_LIST,(ArrayList<RetroMovie>)mMovies);
        outState.putParcelableArrayList(SAVED_HIGHEST_RATED,(ArrayList<RetroMovie>)mHighestRated);
        outState.putParcelableArrayList(SAVED_MOST_POPULAR,(ArrayList<RetroMovie>)mMostPopular);
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
                viewFavorites(mFavorites);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewFavorites(List<RetroMovie> favorites) {
//        if(mFavorites.size() > 0) {
            mSortingBy = SortingBy.FAVORITES;
            if(favorites != null) {
                mMovies = favorites;
            }
            generateMovieList(mMovies);
            mRecyclerView.getAdapter().notifyDataSetChanged();
//        }
        if (mMovies.size() < 1) {
            Toast.makeText(getApplicationContext(), "There are currently no saved favorites", Toast.LENGTH_LONG).show();
        }
    }

    private void loadSavedFavorites() {
//        final LiveData<List<RetroMovie>> favoriteMovies = mDb.movieFavoritesDao().loadAllFavoriteMovies();
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavorites().observe(this, new Observer<List<RetroMovie>>() {
            @Override
            public void onChanged(@Nullable List<RetroMovie> favoriteList) {
                Log.d(TAG, "Updating list of favorites from LiveData in ViewModel");
                mFavorites = favoriteList;
                if (mSortingBy == SortingBy.FAVORITES){
//                    mAdapter = new MovieAdapter(favoriteList,MainActivity.this);
//                    mAdapter.setMovies(favoriteList);
//                    mRecyclerView.setAdapter(mAdapter);
                    mMovies = favoriteList;
                    mFavorites = favoriteList;
                    viewFavorites(mMovies);
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
        mAdapter.setMovies(movieList);
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
