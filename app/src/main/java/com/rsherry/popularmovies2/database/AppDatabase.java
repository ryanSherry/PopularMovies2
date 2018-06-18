package com.rsherry.popularmovies2.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.rsherry.popularmovies2.RetroMovie;

@Database(entities = {RetroMovie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    public static final Object LOCK = new Object();
    public static final String DATABASE_NAME = "MY_MOVIES";
    private static AppDatabase sInstance;

    public static AppDatabase getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG,"Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        //Temporarily allowing queries on main thread for testing purposes
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return sInstance;
    }
    public abstract MovieFavoritesDao movieFavoritesDao();
}
