package com.rsherry.popularmovies2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String API_KEY = ApiKey.getApiKey();
    private TrailerAdapter mAdapter;
    RecyclerView mRecyclerView;
    private List<RetroTrailer> mTrailers;
    private List<RetroReview> mReviews;

    @BindView(R.id.detailMoviePoster) ImageView mMoviePoster;
    @BindView(R.id.movieTitle) TextView mTitle;
    @BindView(R.id.releaseDate) TextView mReleaseDate;
    @BindView(R.id.plotSynopsis) TextView mPlotSynopsis;
    @BindView(R.id.ratingBar) RatingBar mRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        RetroMovie movie = intent.getParcelableExtra("MOVIE");

        Uri uri = Uri.parse(movie.getBackdrop_path());
        Picasso.get().load(uri).into(mMoviePoster);

        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);
        Call<RetroTrailerResults> trailersCall = service.getMovieTrailers(movie.getId(),API_KEY);
        trailersCall.enqueue(new Callback<RetroTrailerResults>() {
            @Override
            public void onResponse(Call<RetroTrailerResults> call, Response<RetroTrailerResults> response) {
                mTrailers = response.body().getTrailers();
                generateTrailerList(mTrailers);
            }

            @Override
            public void onFailure(Call<RetroTrailerResults> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(getApplicationContext(),"network failure",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"conversion issue",Toast.LENGTH_LONG).show();
                }
            }
        });

        Call<RetroReviewResults> reviewsCall = service.getMovieReviews(movie.getId(),API_KEY);
        reviewsCall.enqueue(new Callback<RetroReviewResults>() {
            @Override
            public void onResponse(Call<RetroReviewResults> call, Response<RetroReviewResults> response) {
                mReviews = response.body().getReviews();
            }

            @Override
            public void onFailure(Call<RetroReviewResults> call, Throwable t) {

            }
        });

        populateUI(movie);

    }

    private void populateUI(RetroMovie movie) {

        mTitle.setText(movie.getTitle());
        mReleaseDate.setText(dateFormatter(movie.getReleaseDate()));
        mPlotSynopsis.setText(movie.getOverview());
        mRating.setRating((float) movie.getVoteAverage()/2);
    }

    private void generateTrailerList(List<RetroTrailer> list) {
        mRecyclerView = findViewById(R.id.trailerRecyclerView);
        mAdapter = new TrailerAdapter(list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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