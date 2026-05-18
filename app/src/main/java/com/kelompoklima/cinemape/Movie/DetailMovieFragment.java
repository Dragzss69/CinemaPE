package com.kelompoklima.cinemape.Movie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.kelompoklima.cinemape.API.ApiService;
import com.kelompoklima.cinemape.Model.Movie;
import com.kelompoklima.cinemape.R;
import com.kelompoklima.cinemape.Session.SessionManager;
import com.kelompoklima.cinemape.databinding.FragmentDetailMovieBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DetailMovieFragment extends Fragment {

    private FragmentDetailMovieBinding binding;
    private Movie movie;
    private SessionManager sessionManager;


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
        sessionManager = new SessionManager(requireContext());
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

        setupToolbar();

        if (movie != null) {
            displayMovieDetails();

            binding.layoutOwnerActions.setVisibility(View.VISIBLE);
        }

        binding.btnWatchTrailer.setOnClickListener(v -> {
            String trailerUrl = movie.getUrlTrailer();
            if (trailerUrl == null || trailerUrl.isEmpty()) {
                Toast.makeText(getContext(), "Trailer tidak tersedia", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Gagal membuka link trailer", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnEditMovie.setOnClickListener(v -> {
            EditMovieFragment editFragment = EditMovieFragment.newInstance(movie);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnDeleteMovie.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Movie")
                .setMessage("Apakah Anda yakin ingin menghapus movie ini?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteMovie())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteMovie() {
        ApiService.deleteMovie(movie.getId(), new ApiService.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Movie berhasil dihapus!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Gagal menghapus: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayMovieDetails() {
        binding.tvDetailTitle.setText(movie.getJudul());
        binding.chipCategory.setText(movie.getKategori());
        binding.tvDetailRating.setText("⭐ " + movie.getSkorRating());
        binding.tvDetailDescription.setText(movie.getRingkasan());
        binding.tvDetailMovieId.setText("#" + movie.getId());

        try {
            long timestamp = Long.parseLong(movie.getTanggalRilis());
            if (timestamp > 0) {
                Date date = new Date(timestamp * 1000L);

                SimpleDateFormat fullSdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                binding.tvDetailFullDate.setText(fullSdf.format(date));

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                binding.tvDetailReleaseYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            } else {
                binding.tvDetailFullDate.setText("-");
                binding.tvDetailReleaseYear.setText("-");
            }
        } catch (Exception e) {
            binding.tvDetailFullDate.setText("-");
            binding.tvDetailReleaseYear.setText("-");
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
