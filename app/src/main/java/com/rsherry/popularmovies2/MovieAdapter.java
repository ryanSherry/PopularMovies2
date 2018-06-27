package com.rsherry.popularmovies2;

import android.content.Context;
import android.graphics.Movie;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> implements ListItemClickListener {
    List<RetroMovie> mMovies;
    final private ListItemClickListener mOnClickListener;

    MovieAdapter(List<RetroMovie> movies,ListItemClickListener listener) {
        mMovies = movies;
        mOnClickListener = listener;
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
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.moviePoster) ImageView mMoviePoster;

        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
