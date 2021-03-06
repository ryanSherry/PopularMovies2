package com.rsherry.popularmovies2;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcelable;
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

import com.rsherry.popularmovies2.adapters.ReviewAdapter;
import com.rsherry.popularmovies2.adapters.TrailerAdapter;
import com.rsherry.popularmovies2.database.AppDatabase;
import com.rsherry.popularmovies2.model.RetroMovie;
import com.rsherry.popularmovies2.model.RetroReview;
import com.rsherry.popularmovies2.model.RetroReviewResults;
import com.rsherry.popularmovies2.model.RetroTrailer;
import com.rsherry.popularmovies2.model.RetroTrailerResults;
import com.rsherry.popularmovies2.networking.GetEndpointData;
import com.rsherry.popularmovies2.networking.RetrofitClentInstance;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity implements ListItemClickListener {
    private static final String API_KEY = ApiKey.getApiKey();
    private static final String SAVED_TRAILERS_LAYOUT_MANAGER = "SAVED_TRAILERS_LAYOUT_MANAGER";
    private static final String SAVED_REVIEWS_LAYOUT_MANAGER = "SAVED_REVIEWS_LAYOUT_MANAGER";
    private static final String SAVED_TRAILERS_LIST = "SAVED_TRAILERS_LIST";
    private static final String SAVED_REVIEWS_LIST = "SAVED_REVIEWS_LIST";
    private static final String SAVED_FAVORITES_TEXT_COLOR = "SAVED_FAVORITES_TEXT_COLOR";
    private static final String SAVED_TRAILERS_HEADER = "SAVED_TRAILERS_HEADER";
    private static final String SAVED_REVIEWS_HEADER = "SAVED_REVIEWS_HEADER";
    private List<RetroTrailer> mTrailers;
    private List<RetroReview> mReviews;
    private List<RetroMovie> mFavorites;
    private RetroMovie mMovie;
    private Parcelable mTrailerListState;
    private Parcelable mReviewListState;
    private int mFavoriteTextColor;

    // Member variable for the Database
    private AppDatabase mDb;

    @BindView(R.id.detailMoviePoster)
    ImageView mMoviePoster;
    @BindView(R.id.movieTitle)
    TextView mTitle;
    @BindView(R.id.releaseDate)
    TextView mReleaseDate;
    @BindView(R.id.plotSynopsis)
    TextView mPlotSynopsis;
    @BindView(R.id.ratingBar)
    RatingBar mRating;
    @BindView(R.id.favoriteButton)
    ToggleButton mFavoriteButton;
    @BindView(R.id.reviewHeader)
    TextView mReviewHeader;
    @BindView(R.id.trailerHeader)
    TextView mTrailerHeader;
    @BindView(R.id.reviewRecyclerView)
    RecyclerView mReviewRecyclerView;
    @BindView(R.id.trailerRecyclerView)
    RecyclerView mTrailerRecyclerView;
    @BindView(R.id.favoriteText)
    TextView mFavoriteText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        RetroMovie movie = intent.getParcelableExtra("MOVIE");
        mMovie = movie;


        mFavorites = intent.getParcelableArrayListExtra(MainActivity.SAVED_FAVORITE_LIST);
        if (mFavorites.contains(mMovie)) {
            mFavoriteButton.setChecked(true);
            mFavoriteText.setTextColor(Color.parseColor("#00DDFF"));
        } else {
            mFavoriteButton.setChecked(false);
            mFavoriteText.setTextColor(Color.parseColor("#808080"));
        }

        if (savedInstanceState != null) {
            mTrailerListState = savedInstanceState.getParcelable(SAVED_TRAILERS_LAYOUT_MANAGER);
            mReviewListState = savedInstanceState.getParcelable(SAVED_REVIEWS_LAYOUT_MANAGER);
            mTrailers = savedInstanceState.getParcelableArrayList(SAVED_TRAILERS_LIST);
            mReviews = savedInstanceState.getParcelableArrayList(SAVED_REVIEWS_LIST);
            mFavoriteTextColor = savedInstanceState.getInt(SAVED_FAVORITES_TEXT_COLOR);
            mFavoriteText.setTextColor(mFavoriteTextColor);
            mTrailerHeader.setText(savedInstanceState.getCharSequence(SAVED_TRAILERS_HEADER));
            mReviewHeader.setText(savedInstanceState.getCharSequence(SAVED_REVIEWS_HEADER));
        }


        Uri uri = Uri.parse(movie.getBackDropUrl());
        Picasso.get().load(uri)
                .error(R.drawable.no_image_available)
                .resize(600, 200)
                .centerInside()
                .into(mMoviePoster);

        mDb = AppDatabase.getsInstance(getApplicationContext());


        getTrailersAndReviews(movie);

        populateUI(movie);

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavoriteButton(mFavoriteButton);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mTrailerListState = mTrailerRecyclerView.getLayoutManager().onSaveInstanceState();
        mReviewListState = mReviewRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SAVED_TRAILERS_LAYOUT_MANAGER, mTrailerListState);
        outState.putParcelable(SAVED_REVIEWS_LAYOUT_MANAGER, mReviewListState);
        outState.putParcelableArrayList(SAVED_TRAILERS_LIST, (ArrayList<RetroTrailer>) mTrailers);
        outState.putParcelableArrayList(SAVED_REVIEWS_LIST, (ArrayList<RetroReview>) mReviews);
        outState.putInt(SAVED_FAVORITES_TEXT_COLOR, mFavoriteText.getCurrentTextColor());
        outState.putCharSequence(SAVED_TRAILERS_HEADER, mTrailerHeader.getText());
        outState.putCharSequence(SAVED_REVIEWS_HEADER, mReviewHeader.getText());
    }

    private void getTrailersAndReviews(RetroMovie movie) {
        GetEndpointData service = RetrofitClentInstance.getRetrofitInstance().create(GetEndpointData.class);

        if (mTrailers != null) {
            generateTrailerList(mTrailers);
        } else {
            Call<RetroTrailerResults> trailersCall = service.getMovieTrailers(movie.getId(), API_KEY);
            trailersCall.enqueue(new Callback<RetroTrailerResults>() {
                @Override
                public void onResponse(Call<RetroTrailerResults> call, Response<RetroTrailerResults> response) {
                    if (response.body() != null) {
                        mTrailers = response.body().getTrailers();
                        generateTrailerList(mTrailers);

                        if (mTrailers.size() < 1) {
                            mTrailerHeader.setText(R.string.noTrailersMessage);
                        } else {
                            mTrailerHeader.setText(R.string.trailersMessage);
                        }
                    }

                }

                @Override
                public void onFailure(Call<RetroTrailerResults> call, Throwable t) {
                    if (t instanceof IOException) {
                        Toast.makeText(getApplicationContext(), "network failure", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "conversion issue", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        if (mReviewListState != null) {
            generateReviewList(mReviews);
        } else {
            Call<RetroReviewResults> reviewsCall = service.getMovieReviews(movie.getId(), API_KEY);
            reviewsCall.enqueue(new Callback<RetroReviewResults>() {
                @Override
                public void onResponse(Call<RetroReviewResults> call, Response<RetroReviewResults> response) {
                    if (response.body() != null) {
                        mReviews = response.body().getReviews();
                        generateReviewList(mReviews);

                        if (mReviews.size() < 1) {
                            mReviewHeader.setText(R.string.noReviewsMessage);
                        } else {
                            mReviewHeader.setText(R.string.reviewsMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RetroReviewResults> call, Throwable t) {

                    //Test to see if there is a network or configuration issue with retrofit

                    if (t instanceof IOException) {
                        Toast.makeText(getApplicationContext(), "network failure", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "conversion issue", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void populateUI(RetroMovie movie) {

        mTitle.setText(movie.getTitle());
        mReleaseDate.setText(dateFormatter(movie.getReleaseDate()));
        mPlotSynopsis.setText(movie.getOverview());
        mRating.setRating((float) movie.getVoteAverage() / 2);
    }

    private void generateTrailerList(List<RetroTrailer> trailers) {
        TrailerAdapter adapter = new TrailerAdapter(trailers, this);
        mTrailerRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        if (mTrailerListState != null) {
            layoutManager.onRestoreInstanceState(mTrailerListState);
        }
        mTrailerRecyclerView.setLayoutManager(layoutManager);
        mTrailerRecyclerView.addItemDecoration(new DividerItemDecoration(mTrailerRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter.notifyDataSetChanged();
    }

    private void generateReviewList(List<RetroReview> reviews) {
        ReviewAdapter adapter = new ReviewAdapter(reviews);
        mReviewRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        if (mReviewListState != null) {
            layoutManager.onRestoreInstanceState(mReviewListState);
        }
        mReviewRecyclerView.setLayoutManager(layoutManager);
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
        return (String) android.text.format.DateFormat.format("MMMM d yyyy", newDate);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        // Clicking Trailer takes user to youtube

        RetroTrailer trailer = mTrailers.get(clickedItemIndex);
        String youtubeKey = trailer.getKey();
        String url = trailer.getUrlString(youtubeKey);
        Uri webpage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            this.startActivity(intent);
        }
    }

    public void saveFavorite() {


        int movieId = mMovie.getId();
        String movieTitle = mMovie.getTitle();
        String releaseDate = mMovie.getReleaseDate();
        String overView = mMovie.getOverview();
        String posterPath = mMovie.getPosterPath();
        String backDropPath = mMovie.getBackdropPath();
        double voteAverage = mMovie.getVoteAverage();
        boolean isFavorite = mMovie.isFavorite();

        final RetroMovie favoriteMovie = new RetroMovie(movieId, movieTitle, releaseDate, overView, posterPath, backDropPath, voteAverage, isFavorite);

        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieFavoritesDao().insertFavoriteMovie(favoriteMovie);
            }
        });
    }

    public void deleteFavorite() {

        int movieId = mMovie.getId();
        String movieTitle = mMovie.getTitle();
        String releaseDate = mMovie.getReleaseDate();
        String overView = mMovie.getOverview();
        String posterPath = mMovie.getPosterPath();
        String backDropPath = mMovie.getBackdropPath();
        double voteAverage = mMovie.getVoteAverage();
        boolean isFavorite = mMovie.isFavorite();

        final RetroMovie favoriteMovie = new RetroMovie(movieId, movieTitle, releaseDate, overView, posterPath, backDropPath, voteAverage, isFavorite);

        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieFavoritesDao().deleteFavoriteMovie(favoriteMovie);
            }
        });
    }

    public void toggleFavoriteButton(ToggleButton toggleButton) {
        if (toggleButton.isChecked()) {
            saveFavorite();
            mFavoriteText.setTextColor(Color.parseColor("#00DDFF"));
        } else {
            deleteFavorite();
            mFavoriteText.setTextColor(Color.parseColor("#808080"));
        }
    }
}