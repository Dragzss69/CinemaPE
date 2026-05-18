package com.kelompoklima.cinemape.Movie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kelompoklima.cinemape.API.ApiService;
import com.kelompoklima.cinemape.Model.Movie;
import com.kelompoklima.cinemape.R;
import com.kelompoklima.cinemape.Session.SessionManager;
import com.kelompoklima.cinemape.databinding.FragmentAddMovieBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AddMovieFragment mengelola fitur penambahan film baru oleh user.
 * Film yang ditambahkan akan dikaitkan dengan username yang sedang login.
 */
public class AddMovieFragment extends Fragment {

    private FragmentAddMovieBinding binding; // Akses komponen UI
    private MovieAdapter adapter; // Menampilkan daftar film yang dibuat oleh user sendiri
    private List<Movie> myAddedMovies = new ArrayList<>(); // List lokal untuk film milik user
    private SessionManager sessionManager;
    private String currentUsername; // Menyimpan username yang sedang login

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menginisialisasi layout menggunakan View Binding
        binding = FragmentAddMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        currentUsername = sessionManager.getUsername();

        setupRecyclerView();
        fetchMyMovies(); // Ambil data film milik user dari server

        // Listener untuk menampilkan form tambah film
        binding.ivAddPlaceholder.setOnClickListener(v -> showForm());
        binding.fabAddMovie.setOnClickListener(v -> showForm());
        
        // Listener untuk membatalkan pengisian form
        binding.btnCancelAdd.setOnClickListener(v -> hideForm());

        // Listener untuk menyimpan film baru ke server
        binding.btnSaveMovie.setOnClickListener(v -> saveNewMovie());
    }

    /**
     * Mengambil data dari form dan mengirimkannya ke MockAPI melalui ApiService.
     */
    private void saveNewMovie() {
        String title = binding.etAddTitle.getText().toString().trim();
        String category = binding.etAddCategory.getText().toString().trim();
        String ratingStr = binding.etAddRating.getText().toString().trim();
        String poster = binding.etAddPoster.getText().toString().trim();
        String trailer = binding.etAddTrailer.getText().toString().trim();
        String description = binding.etAddDescription.getText().toString().trim();

        // Validasi: Cek apakah ada form yang masih kosong
        if (title.isEmpty() || category.isEmpty() || ratingStr.isEmpty() || 
            poster.isEmpty() || trailer.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Harap isi semua data form, jangan dikosongkan!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi Rating: Harus angka antara 0 - 10
        try {
            double ratingValue = Double.parseDouble(ratingStr);
            if (ratingValue < 0 || ratingValue > 10) {
                Toast.makeText(getContext(), "Rating harus antara 0 sampai 10", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Rating harus berupa angka", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membuat objek Movie baru dengan data dari form
        Movie movieBaru = new Movie();
        movieBaru.setJudul(title);
        movieBaru.setKategori(category);
        movieBaru.setSkorRating(ratingStr);
        movieBaru.setGambarPoster(poster);
        movieBaru.setUrlTrailer(trailer);
        movieBaru.setRingkasan(description);
        movieBaru.setTanggalRilis(System.currentTimeMillis() / 1000); // Waktu sekarang (Timestamp)
        movieBaru.setUserId(currentUsername); // Kaitkan film ini dengan user yang login

        // Panggil API untuk menyimpan data
        ApiService.createMovie(movieBaru, new ApiService.ApiCallback<Movie>() {
            @Override
            public void onSuccess(Movie result) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Movie berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    clearForm(); // Bersihkan isi form
                    fetchMyMovies(); // Refresh daftar film
                    hideForm(); // Kembali ke tampilan list
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Gagal menyimpan: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Inisialisasi daftar film yang ditampilkan.
     */
    private void setupRecyclerView() {
        adapter = new MovieAdapter();
        binding.rvAddedMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAddedMovies.setAdapter(adapter);

        updateSavedStateInAdapter();

        // Klik pada ikon favorit
        adapter.setOnSaveClickListener(movie -> {
            boolean isSaved = sessionManager.toggleMovieLocally(movie);
            Toast.makeText(getContext(), isSaved ? "Disimpan" : "Dihapus", Toast.LENGTH_SHORT).show();
            updateSavedStateInAdapter();
        });

        // Klik pada item untuk melihat detail
        adapter.setOnItemClickListener(movie -> {
            DetailMovieFragment detailFragment = DetailMovieFragment.newInstance(movie);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    /**
     * Memastikan ikon favorit tetap sinkron dengan data lokal.
     */
    private void updateSavedStateInAdapter() {
        List<Movie> savedMovies = sessionManager.getSavedMoviesLocally();
        List<String> savedIds = new ArrayList<>();
        for (Movie m : savedMovies) {
            if (m.getId() != null) savedIds.add(m.getId());
        }
        adapter.setSavedMovieIds(savedIds);
    }

    /**
     * Mengambil daftar film dari server dan memfilternya hanya untuk user yang sedang login.
     */
    private void fetchMyMovies() {
        ApiService.getAllMovie(new ApiService.ApiCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> result) {
                if (isAdded()) {
                    // Filter: Hanya tampilkan film yang memiliki userId sama dengan user yang login
                    myAddedMovies = result.stream()
                            .filter(movie -> currentUsername != null && currentUsername.equals(movie.getUserId()))
                            .collect(Collectors.toList());
                    updateUIState();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) updateUIState();
            }
        });
    }

    /**
     * Mengatur visibilitas UI (Empty State vs List State).
     */
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
    public void onResume() {
        super.onResume();
        updateSavedStateInAdapter();
        fetchMyMovies();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
