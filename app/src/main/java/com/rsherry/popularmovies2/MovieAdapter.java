package com.rsherry.popularmovies2;

import android.content.Context;
import android.graphics.Movie;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    List<RetroMovie> mMovies;

    MovieAdapter(List<RetroMovie> movies) {
        mMovies = movies;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item,parent,false);

        return new MovieViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        RetroMovie movie = mMovies.get(position);

        Uri uri = Uri.parse(movie.getPosterPath());

        Picasso.get().load(uri).into(holder.mMoviePoster);
        //picasso magic here
        //onClick fun here
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.moviePoster) ImageView mMoviePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

    }
}
