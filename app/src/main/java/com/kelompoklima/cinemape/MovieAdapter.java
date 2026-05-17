package com.kelompoklima.cinemape;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList = new ArrayList<>();
    private Set<String> savedMovieIds = new HashSet<>();
    private OnSaveClickListener onSaveClickListener;
    private OnItemClickListener onItemClickListener;

    public interface OnSaveClickListener {
        void onSaveClick(Movie movie);
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.onSaveClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public void setSavedMovieIds(List<String> savedIds) {
        this.savedMovieIds = new HashSet<>(savedIds);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvTitle.setText(movie.getJudul());
        holder.tvDescription.setText(movie.getRingkasan());
        holder.tvRating.setText("⭐ " + movie.getSkorRating());
        holder.tvCategory.setText(movie.getKategori());

        if (movie.getTanggalRilis() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(movie.getTanggalRilis() * 1000L));
            holder.tvReleaseDate.setText("Rilis: " + formattedDate);
            holder.tvReleaseDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvReleaseDate.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext())
                .load(movie.getGambarPoster())
                .placeholder(android.R.drawable.progress_horizontal)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.ivPoster);

        // Update warna icon favorit: Gunakan Orange (#FF8C00) agar sesuai tema baru
        if (savedMovieIds.contains(movie.getId())) {
            ImageViewCompat.setImageTintList(holder.ivSaveMovie, ColorStateList.valueOf(Color.parseColor("#FF8C00")));
        } else {
            ImageViewCompat.setImageTintList(holder.ivSaveMovie, ColorStateList.valueOf(Color.WHITE));
        }

        holder.ivSaveMovie.setOnClickListener(v -> {
            if (onSaveClickListener != null) {
                onSaveClickListener.onSaveClick(movie);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvRating, tvCategory, tvReleaseDate;
        ImageView ivPoster, ivSaveMovie;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_movie_title);
            tvDescription = itemView.findViewById(R.id.tv_movie_description);
            tvRating = itemView.findViewById(R.id.tv_movie_rating);
            tvCategory = itemView.findViewById(R.id.tv_movie_category);
            tvReleaseDate = itemView.findViewById(R.id.tv_movie_release_date);
            ivPoster = itemView.findViewById(R.id.iv_movie_poster);
            ivSaveMovie = itemView.findViewById(R.id.iv_save_movie);
        }
    }
}
