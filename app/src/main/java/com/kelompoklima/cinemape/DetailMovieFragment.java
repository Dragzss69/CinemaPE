package com.kelompoklima.cinemape;

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
import com.kelompoklima.cinemape.databinding.FragmentDetailMovieBinding;
import java.text.SimpleDateFormat;
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

        if (movie != null) {
            displayMovieDetails();
            checkOwnership();
        }

        // Tombol Kembali
        binding.btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Tombol Edit
        binding.btnEditMovie.setOnClickListener(v -> {
            AddMovieFragment editFragment = AddMovieFragment.newInstanceForEdit(movie);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Tombol Hapus
        binding.btnDeleteMovie.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void checkOwnership() {
        String currentUsername = sessionManager.getUsername();
        if (currentUsername != null && currentUsername.equals(movie.getUserId())) {
            binding.layoutOwnerActions.setVisibility(View.VISIBLE);
        } else {
            binding.layoutOwnerActions.setVisibility(View.GONE);
        }
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
                    Toast.makeText(getContext(), "Movie berhasil dihapus", Toast.LENGTH_SHORT).show();
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
