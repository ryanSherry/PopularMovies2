package com.rsherry.popularmovies2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.rsherry.popularmovies2.database.AppDatabase;
import com.rsherry.popularmovies2.model.RetroMovie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    //Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<RetroMovie>> mFavorites;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        mFavorites = database.movieFavoritesDao().loadAllFavoriteMovies();
    }

    public LiveData<List<RetroMovie>> getFavorites() {
        return mFavorites;
    }

}
