package com.kelompoklima.cinemape;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.kelompoklima.cinemape.databinding.FragmentDetailMovieBinding;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailMovieFragment extends Fragment {

    private FragmentDetailMovieBinding binding;
    private Movie movie;

    public static DetailMovieFragment newInstance(Movie movie) {
        DetailMovieFragment fragment = new DetailMovieFragment();
        Bundle args = new Bundle();
        args.putSerializable("movie", movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movie = (Movie) getArguments().getSerializable("movie");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (movie != null) {
            displayMovieDetails();
        }

        binding.btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.btnWatchTrailer.setOnClickListener(v -> {
            if (movie != null && movie.getUrlTrailer() != null && !movie.getUrlTrailer().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getUrlTrailer()));
                startActivity(intent);
            }
        });
    }

    private void displayMovieDetails() {
        binding.tvDetailTitle.setText(movie.getJudul());
        binding.tvDetailCategory.setText(movie.getKategori());
        binding.tvDetailRating.setText("⭐ " + movie.getSkorRating());
        binding.tvDetailDescription.setText(movie.getRingkasan());

        if (movie.getTanggalRilis() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(movie.getTanggalRilis() * 1000L));
            binding.tvDetailRelease.setText("Released: " + formattedDate);
        }

        Glide.with(this)
                .load(movie.getGambarPoster())
                .placeholder(R.color.black_card_dark)
                .into(binding.ivDetailPoster);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
