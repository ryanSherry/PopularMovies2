package com.rsherry.popularmovies2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.detailMoviePoster) ImageView mMoviePoster;
    @BindView(R.id.movieTitle) TextView mTitle;
    @BindView(R.id.releaseDate) TextView mReleaseDate;
    @BindView(R.id.plotSynopsis) TextView mPlotSynopsis;
    @BindView(R.id.ratingBar)
    RatingBar mRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        RetroMovie movie = intent.getParcelableExtra("MOVIE");

        Uri uri = Uri.parse(movie.getBackdrop_path());
        Picasso.get().load(uri).into(mMoviePoster);

        populateUI(movie);

    }

    private void populateUI(RetroMovie movie) {

        mTitle.setText(movie.getTitle());
        mReleaseDate.setText(dateFormatter(movie.getReleaseDate()));
        mPlotSynopsis.setText(movie.getOverview());
        mRating.setRating((float) movie.getVoteAverage()/2);
    }

    private String dateFormatter(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = new Date();
        try {
            newDate = format.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (String) android.text.format.DateFormat.format("MMMM yyyy",newDate);
    }
}