package com.kelompoklima.cinemape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kelompoklima.cinemape.databinding.FragmentAddMovieBinding;
import java.util.ArrayList;
import java.util.List;

public class AddMovieFragment extends Fragment {

    private FragmentAddMovieBinding binding;
    private MovieAdapter adapter;
    private List<Movie> addedMovies = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        fetchMovies();

        // Klik ikon plus di empty state -> Buka Form
        binding.ivAddPlaceholder.setOnClickListener(v -> showForm());

        // Klik FAB -> Buka Form
        binding.fabAddMovie.setOnClickListener(v -> showForm());

        // Klik Batal -> Kembali ke List/Empty State
        binding.btnCancelAdd.setOnClickListener(v -> hideForm());

        binding.btnSaveMovie.setOnClickListener(v -> {
            String title = binding.etAddTitle.getText().toString();
            String category = binding.etAddCategory.getText().toString();
            String rating = binding.etAddRating.getText().toString();
            String poster = binding.etAddPoster.getText().toString();
            String description = binding.etAddDescription.getText().toString();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Judul dan Ringkasan harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            Movie movieBaru = new Movie();
            movieBaru.setJudul(title);
            movieBaru.setKategori(category);
            movieBaru.setSkorRating(rating);
            movieBaru.setGambarPoster(poster);
            movieBaru.setRingkasan(description);
            movieBaru.setTanggalRilis(System.currentTimeMillis() / 1000);

            ApiService.createMovie(movieBaru, new ApiService.ApiCallback<Movie>() {
                @Override
                public void onSuccess(Movie result) {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Movie berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                        clearForm();
                        fetchMovies(); // Refresh data agar muncul di list
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Gagal: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter();
        binding.rvAddedMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAddedMovies.setAdapter(adapter);
    }

    private void fetchMovies() {
        ApiService.getAllMovie(new ApiService.ApiCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> result) {
                if (isAdded()) {
                    addedMovies = result;
                    updateUIState();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    updateUIState();
                }
            }
        });
    }

    private void updateUIState() {
        binding.layoutForm.setVisibility(View.GONE);
        if (addedMovies == null || addedMovies.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.layoutListState.setVisibility(View.GONE);
            binding.fabAddMovie.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.layoutListState.setVisibility(View.VISIBLE);
            binding.fabAddMovie.setVisibility(View.VISIBLE);
            adapter.setMovieList(addedMovies);
        }
    }

    private void showForm() {
        binding.layoutForm.setVisibility(View.VISIBLE);
        binding.layoutEmptyState.setVisibility(View.GONE);
        binding.layoutListState.setVisibility(View.GONE);
        binding.fabAddMovie.setVisibility(View.GONE);
    }

    private void hideForm() {
        binding.layoutForm.setVisibility(View.GONE);
        updateUIState();
    }

    private void clearForm() {
        binding.etAddTitle.setText("");
        binding.etAddCategory.setText("");
        binding.etAddRating.setText("");
        binding.etAddPoster.setText("");
        binding.etAddDescription.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}