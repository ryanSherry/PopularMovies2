package com.rsherry.popularmovies2.database;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.rsherry.popularmovies2.RetroMovie;

import java.util.List;

public interface MovieFavoritesDao {
    @Query("SELECT * FROM favorite_movies Order by mTitle")
    List<RetroMovie> loadAllFavoriteMovies();

    @Insert
    void insertFavoriteMovie(RetroMovie movie);

    @Delete
    void deleteFavoriteMovie(RetroMovie movie);
}
