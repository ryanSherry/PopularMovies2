package com.rsherry.popularmovies2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Parcelable;
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

import com.rsherry.popularmovies2.adapters.MovieAdapter;
import com.rsherry.popularmovies2.database.AppDatabase;
import com.rsherry.popularmovies2.model.RetroMovie;
import com.rsherry.popularmovies2.model.RetroMovieResults;
import com.rsherry.popularmovies2.networking.GetEndpointData;
import com.rsherry.popularmovies2.networking.RetrofitClentInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ListItemClickListener {
    public static final String SAVED_LAYOUT_MANAGER = "SAVED_LAYOUT_MANAGER";
    public static final String SAVED_MOVIE_LIST = "SAVED_MOVIE_LIST";
    public static final String SAVED_SORTING_OPTION = "SAVED_SORTING_OPTION";
    public static final String SAVED_FAVORITE_LIST = "SAVED_FAVORITE_LIST";
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String SAVED_HIGHEST_RATED = "SAVED_HIGHEST_RATED";
    public static final String SAVED_MOST_POPULAR = "SAVED_MOST_POPULAR";

    //Sorting by list options

    public static final String MOST_POPULAR = "MOST_POPULAR";
    public static final String HIGHEST_RATED = "HIGHEST_RATED";
    public static final String FAVORITES = "FAVORITES";

    private MovieAdapter mAdapter;
    private static final String API_KEY = ApiKey.getApiKey();
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    List<RetroMovie> mMovies;
    List<RetroMovie> mHighestRated;
    List<RetroMovie> mMostPopular;
    List<RetroMovie> mFavorites;
    AppDatabase mDb;
    Parcelable mListState;
    String mSortingBy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDb = AppDatabase.getsInstance(getApplicationContext());

        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
            mMovies = savedInstanceState.getParcelableArrayList(SAVED_MOVIE_LIST);
            mMostPopular = savedInstanceState.getParcelableArrayList(SAVED_MOST_POPULAR);
            mHighestRated = savedInstanceState.getParcelableArrayList(SAVED_HIGHEST_RATED);
            mSortingBy = savedInstanceState.getString(SAVED_SORTING_OPTION);

        } else {

            //initialize incase null values
            mMovies = new ArrayList<>();
            mAdapter = new MovieAdapter(mMovies, this);
            mAdapter.setMovies(mMovies);
            mRecyclerView.setAdapter(mAdapter);

            mSortingBy = MOST_POPULAR;
        }

        loadSavedFavorites();

        switch (mSortingBy) {
            case MOST_POPULAR:
                sortByMostPopular();
                break;
            case HIGHEST_RATED:
                sortByHighestRated();
                break;
            case FAVORITES:
                viewFavorites(mFavorites);
                break;
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SAVED_LAYOUT_MANAGER, mListState);

        // Saving Favorites list only so it can be passed to the detailActivity via an intent and used to determine if the movie is a favorite or not. The actual persistence of favorites is done via Room and ViewModel

        outState.putParcelableArrayList(SAVED_MOVIE_LIST, (ArrayList<RetroMovie>) mMovies);

        outState.putParcelableArrayList(SAVED_HIGHEST_RATED, (ArrayList<RetroMovie>) mHighestRated);
        outState.putParcelableArrayList(SAVED_MOST_POPULAR, (ArrayList<RetroMovie>) mMostPopular);
        outState.putString(SAVED_SORTING_OPTION, mSortingBy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_sort_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        mSortingBy = FAVORITES;
        if (favorites != null) {
            mMovies = favorites;
        }
        generateMovieList(mMovies);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        if (mMovies.size() < 1) {
            Toast.makeText(getApplicationContext(), R.string.noSavedFavoritesMessage , Toast.LENGTH_LONG).show();
        }
    }

    private void loadSavedFavorites() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavorites().observe(this, new Observer<List<RetroMovie>>() {
            @Override
            public void onChanged(@Nullable List<RetroMovie> favoriteList) {
                Log.d(TAG, "Updating list of favorites from LiveData in ViewModel");
                mFavorites = favoriteList;

                if (mSortingBy.equals(FAVORITES)) {
                    mMovies = favoriteList;
                    mFavorites = favoriteList;
                    viewFavorites(mMovies);

                    if (mMovies.size() < 1) {
                        Toast.makeText(getApplicationContext(), R.string.noSavedFavoritesMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void sortByHighestRated() {
        mSortingBy = HIGHEST_RATED;
        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);

        //Only make the network call if movies are not cached

        if (mHighestRated != null) {
            mMovies = mHighestRated;
            generateMovieList(mMovies);
        } else {
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

                    //Test to see if there is a network or configuration issue with retrofit

                    if (t instanceof IOException) {
                        Toast.makeText(getApplicationContext(), "There is currently no network connection", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "This is a software bug. Please contact the developer of this app to                        investigate", Toast.LENGTH_LONG).show();
                    }
                    generateMovieList(mMovies);
                }
            });
        }
    }

    public void sortByMostPopular() {
        mSortingBy = MOST_POPULAR;
        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);

        //Test to see if there is a network or configuration issue with retrofit

        if (mMostPopular != null) {
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

                    //Test to see if there is a network or configuration issue with retrofit

                    if (t instanceof IOException) {
                        Toast.makeText(getApplicationContext(), "There is currently no network connection", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "This is a software bug. Please contact the developer of this app to                        investigate", Toast.LENGTH_LONG).show();
                    }
                    generateMovieList(mMovies);
                }
            });
        }
    }

    private void generateMovieList(List<RetroMovie> movieList) {
        mAdapter = new MovieAdapter(movieList, this);
        mAdapter.setMovies(movieList);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(this, 2);

        //Restore adapter to maintain scroll position

        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        final RetroMovie movie = mMovies.get(clickedItemIndex);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("MOVIE", movie);
        intent.putParcelableArrayListExtra(SAVED_FAVORITE_LIST, (ArrayList<RetroMovie>) mFavorites);
        this.startActivity(intent);
    }
}
