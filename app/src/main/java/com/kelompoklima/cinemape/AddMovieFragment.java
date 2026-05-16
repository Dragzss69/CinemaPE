package com.kelompoklima.cinemape;

import android.os.Bundle;
import android.util.Log;
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
import java.util.stream.Collectors;

public class AddMovieFragment extends Fragment {

    private FragmentAddMovieBinding binding;
    private MovieAdapter adapter;
    private List<Movie> myAddedMovies = new ArrayList<>();
    private SessionManager sessionManager;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        
        // AMBIL IDENTITAS USER: Coba ambil ID, jika tidak ada gunakan Email (untuk user lama)
        currentUserId = sessionManager.getUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            currentUserId = sessionManager.getEmail();
        }

        setupRecyclerView();
        fetchMyMovies();

        binding.ivAddPlaceholder.setOnClickListener(v -> showForm());
        binding.fabAddMovie.setOnClickListener(v -> showForm());
        binding.btnCancelAdd.setOnClickListener(v -> hideForm());

        binding.btnSaveMovie.setOnClickListener(v -> {
            String title = binding.etAddTitle.getText().toString();
            String category = binding.etAddCategory.getText().toString();
            String rating = binding.etAddRating.getText().toString();
            String poster = binding.etAddPoster.getText().toString();
            String trailer = binding.etAddTrailer.getText().toString();
            String description = binding.etAddDescription.getText().toString();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Judul dan Ringkasan wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            Movie movieBaru = new Movie();
            movieBaru.setJudul(title);
            movieBaru.setKategori(category);
            movieBaru.setSkorRating(rating);
            movieBaru.setGambarPoster(poster);
            movieBaru.setUrlTrailer(trailer);
            movieBaru.setRingkasan(description);
            movieBaru.setTanggalRilis(System.currentTimeMillis() / 1000);
            movieBaru.setUserId(currentUserId); // Menandai movie dengan ID user saat ini

            ApiService.createMovie(movieBaru, new ApiService.ApiCallback<Movie>() {
                @Override
                public void onSuccess(Movie result) {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Movie berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                        clearForm();
                        fetchMyMovies(); 
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Gagal menyimpan: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter();
        binding.rvAddedMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAddedMovies.setAdapter(adapter);

        // Set listener untuk tombol save (Lokal)
        adapter.setOnSaveClickListener(movie -> {
            sessionManager.saveMovieLocally(movie);
            Toast.makeText(getContext(), "Movie disimpan ke favorit!", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchMyMovies() {
        ApiService.getAllMovie(new ApiService.ApiCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> result) {
                if (isAdded()) {
                    // FILTER: Pastikan movie yang ditampilkan adalah milik user ini
                    myAddedMovies = result.stream()
                            .filter(movie -> currentUserId != null && currentUserId.equals(movie.getUserId()))
                            .collect(Collectors.toList());
                    
                    updateUIState();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Gagal mengambil data: " + errorMessage, Toast.LENGTH_SHORT).show();
                    updateUIState();
                }
            }
        });
    }

    private void updateUIState() {
        if (binding == null) return;

        binding.layoutForm.setVisibility(View.GONE);
        if (myAddedMovies == null || myAddedMovies.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.layoutListState.setVisibility(View.GONE);
            binding.fabAddMovie.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.layoutListState.setVisibility(View.VISIBLE);
            binding.fabAddMovie.setVisibility(View.VISIBLE);
            adapter.setMovieList(myAddedMovies);
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
        binding.etAddTrailer.setText("");
        binding.etAddDescription.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}