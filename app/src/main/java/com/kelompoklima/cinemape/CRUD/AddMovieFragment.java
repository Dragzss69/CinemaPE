package com.kelompoklima.cinemape.CRUD;

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
import com.kelompoklima.cinemape.UI.DetailMovieFragment;
import com.kelompoklima.cinemape.UI.MovieAdapter;
import com.kelompoklima.cinemape.databinding.FragmentAddMovieBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddMovieFragment extends Fragment {

    private FragmentAddMovieBinding binding;
    private MovieAdapter adapter;
    private List<Movie> myAddedMovies = new ArrayList<>();
    private SessionManager sessionManager;
    private String currentUsername;

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
        currentUsername = sessionManager.getUsername();

        setupRecyclerView();
        fetchMyMovies();

        binding.ivAddPlaceholder.setOnClickListener(v -> showForm());
        binding.fabAddMovie.setOnClickListener(v -> showForm());

        binding.btnCancelAdd.setOnClickListener(v -> hideForm());

        binding.btnSaveMovie.setOnClickListener(v -> saveNewMovie());
    }

    private void saveNewMovie() {
        String title = binding.etAddTitle.getText().toString().trim();
        String category = binding.etAddCategory.getText().toString().trim();
        String ratingStr = binding.etAddRating.getText().toString().trim();
        String poster = binding.etAddPoster.getText().toString().trim();
        String trailer = binding.etAddTrailer.getText().toString().trim();
        String description = binding.etAddDescription.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || ratingStr.isEmpty() || 
            poster.isEmpty() || trailer.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Harap isi semua data form, jangan dikosongkan!", Toast.LENGTH_SHORT).show();
            return;
        }

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

        Movie movieBaru = new Movie();
        movieBaru.setJudul(title);
        movieBaru.setKategori(category);
        movieBaru.setSkorRating(ratingStr);
        movieBaru.setGambarPoster(poster);
        movieBaru.setUrlTrailer(trailer);
        movieBaru.setRingkasan(description);
        movieBaru.setTanggalRilis(String.valueOf(System.currentTimeMillis() / 1000));
        movieBaru.setUserId(currentUsername);

        ApiService.createMovie(movieBaru, new ApiService.ApiCallback<Movie>() {
            @Override
            public void onSuccess(Movie result) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Movie berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    fetchMyMovies();
                    hideForm();
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

    private void setupRecyclerView() {
        adapter = new MovieAdapter();
        binding.rvAddedMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAddedMovies.setAdapter(adapter);

        updateSavedStateInAdapter();

        adapter.setOnSaveClickListener(movie -> {
            boolean isSaved = sessionManager.toggleMovieLocally(movie);
            Toast.makeText(getContext(), isSaved ? "Disimpan" : "Dihapus", Toast.LENGTH_SHORT).show();
            updateSavedStateInAdapter();
        });

        adapter.setOnItemClickListener(movie -> {
            DetailMovieFragment detailFragment = DetailMovieFragment.newInstance(movie);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void updateSavedStateInAdapter() {
        List<Movie> savedMovies = sessionManager.getSavedMoviesLocally();
        List<String> savedIds = new ArrayList<>();
        for (Movie m : savedMovies) {
            if (m.getId() != null) savedIds.add(m.getId());
        }
        adapter.setSavedMovieIds(savedIds);
    }

    private void fetchMyMovies() {
        ApiService.getAllMovie(new ApiService.ApiCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> result) {
                if (isAdded()) {
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
