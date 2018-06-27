package com.rsherry.popularmovies2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.rsherry.popularmovies2.model.RetroMovie;

import java.util.List;

@Dao
public interface MovieFavoritesDao {
    @Query("SELECT * FROM favorite_movies")
    LiveData<List<RetroMovie>> loadAllFavoriteMovies();

    @Insert
    void insertFavoriteMovie(RetroMovie movie);

    @Delete
    void deleteFavoriteMovie(RetroMovie movie);
}
