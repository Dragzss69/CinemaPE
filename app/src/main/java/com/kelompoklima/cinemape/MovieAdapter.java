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

/**
 * MovieAdapter adalah penghubung antara data List<Movie> dengan tampilan RecyclerView.
 * Mengatur bagaimana setiap item film ditampilkan di dalam daftar.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList = new ArrayList<>();
    private Set<String> savedMovieIds = new HashSet<>(); // Menyimpan ID film yang difavoritkan untuk pewarnaan ikon
    private OnSaveClickListener onSaveClickListener;
    private OnItemClickListener onItemClickListener;

    // Interface untuk menangani klik pada tombol save/favorit
    public interface OnSaveClickListener {
        void onSaveClick(Movie movie);
    }

    // Interface untuk menangani klik pada baris item (buka detail)
    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.onSaveClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * Memasukkan data film ke adapter dan memperbarui tampilan.
     */
    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged(); // Memberitahu RecyclerView bahwa data berubah
    }

    /**
     * Mengatur daftar ID film yang sudah difavoritkan oleh user.
     */
    public void setSavedMovieIds(List<String> savedIds) {
        this.savedMovieIds = new HashSet<>(savedIds);
        notifyDataSetChanged();
    }

    /**
     * Membuat tampilan layout per item (item_movie.xml).
     */
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    /**
     * Memasukkan data dari objek Movie ke elemen UI (TextView, ImageView, dll).
     */
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        
        // Mengisi teks data film
        holder.tvTitle.setText(movie.getJudul());
        holder.tvDescription.setText(movie.getRingkasan());
        holder.tvRating.setText("⭐ " + movie.getSkorRating());
        holder.tvCategory.setText(movie.getKategori());

        // Format tanggal dari Timestamp (long) ke format dd MMM yyyy
        if (movie.getTanggalRilis() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(movie.getTanggalRilis() * 1000L));
            holder.tvReleaseDate.setText("Rilis: " + formattedDate);
            holder.tvReleaseDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvReleaseDate.setVisibility(View.GONE);
        }

        // Memuat gambar dari URL menggunakan library Glide
        Glide.with(holder.itemView.getContext())
                .load(movie.getGambarPoster())
                .placeholder(android.R.drawable.progress_horizontal)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.ivPoster);

        // Update warna icon favorit: Orange jika tersimpan, Putih jika tidak
        if (savedMovieIds.contains(movie.getId())) {
            ImageViewCompat.setImageTintList(holder.ivSaveMovie, ColorStateList.valueOf(Color.parseColor("#FF8C00")));
        } else {
            ImageViewCompat.setImageTintList(holder.ivSaveMovie, ColorStateList.valueOf(Color.WHITE));
        }

        // Event Klik: Tombol Simpan
        holder.ivSaveMovie.setOnClickListener(v -> {
            if (onSaveClickListener != null) {
                onSaveClickListener.onSaveClick(movie);
            }
        });

        // Event Klik: Seluruh item (masuk detail)
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

    /**
     * ViewHolder bertugas memegang referensi ke elemen-elemen UI dalam satu item.
     */
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
