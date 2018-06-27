package com.rsherry.popularmovies2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rsherry.popularmovies2.ListItemClickListener;
import com.rsherry.popularmovies2.R;
import com.rsherry.popularmovies2.model.RetroTrailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> implements ListItemClickListener {
    private List<RetroTrailer> mTrailers;
    final private ListItemClickListener mOnClickListener;

    public TrailerAdapter(List<RetroTrailer> trailers, ListItemClickListener listener) {
        mTrailers = trailers;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_item, parent, false);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        RetroTrailer trailer = mTrailers.get(position);

        holder.mTrailerTitle.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.playTrailer)
        ImageView mPlayTrailer;
        @BindView(R.id.trailerTitle)
        TextView mTrailerTitle;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
