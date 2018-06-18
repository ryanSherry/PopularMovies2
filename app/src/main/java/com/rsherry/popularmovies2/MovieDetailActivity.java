package com.rsherry.popularmovies2;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.rsherry.popularmovies2.database.AppDatabase;
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

public class MovieDetailActivity extends AppCompatActivity implements ListItemClickListener {
    private static final String API_KEY = ApiKey.getApiKey();
    RecyclerView mRecyclerView;
    private List<RetroTrailer> mTrailers;
    private List<RetroReview> mReviews;
    private RetroMovie mMovie;

    // Member variable for the Database
    private AppDatabase mDb;

    @BindView(R.id.detailMoviePoster) ImageView mMoviePoster;
    @BindView(R.id.movieTitle) TextView mTitle;
    @BindView(R.id.releaseDate) TextView mReleaseDate;
    @BindView(R.id.plotSynopsis) TextView mPlotSynopsis;
    @BindView(R.id.ratingBar) RatingBar mRating;
    @BindView(R.id.favoriteButton) ToggleButton mFavoriteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        RetroMovie movie = intent.getParcelableExtra("MOVIE");
        mMovie = movie;

        Uri uri = Uri.parse(movie.getBackdrop_path());
        Picasso.get().load(uri).into(mMoviePoster);

        mDb = AppDatabase.getsInstance(getApplicationContext());

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
                generateReviewList(mReviews);
            }

            @Override
            public void onFailure(Call<RetroReviewResults> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(getApplicationContext(),"network failure",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"conversion issue",Toast.LENGTH_LONG).show();
                }
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

    private void generateTrailerList(List<RetroTrailer> trailers) {
        mRecyclerView = findViewById(R.id.trailerRecyclerView);
        TrailerAdapter adapter = new TrailerAdapter(trailers,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),DividerItemDecoration.VERTICAL));
        adapter.notifyDataSetChanged();
    }

    private void generateReviewList(List<RetroReview> reviews) {
        mRecyclerView = findViewById(R.id.reviewRecyclerView);
        ReviewAdapter adapter = new ReviewAdapter(reviews);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    @Override
    public void onListItemClick(int clickedItemIndex) {

        RetroTrailer trailer = mTrailers.get(clickedItemIndex);
        String youtubeKey = trailer.getKey();
        String url = trailer.getUrlString(youtubeKey);
        Uri webpage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if(intent.resolveActivity(getPackageManager()) != null) {
            this.startActivity(intent);
        }
    }

    // Saves movie as a favorite

    public void onSaveFavoriteButtonClicked() {
        int movieId = mMovie.getId();
        String movieTitle = mMovie.getTitle();

        RetroMovie favoriteMovie = new RetroMovie(movieId, movieTitle);

        mDb.movieFavoritesDao().insertFavoriteMovie(favoriteMovie);

    }

    public void toggleFavoriteButton(ToggleButton toggleButton) {
        if(toggleButton.isChecked()) {
            //save favorite
        } else {
            //remove favorite
        }
        }
    }