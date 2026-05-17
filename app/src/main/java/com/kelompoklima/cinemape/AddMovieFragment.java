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
    private String currentUsername;
    private Movie movieToEdit;

    public static AddMovieFragment newInstanceForEdit(Movie movie) {
        AddMovieFragment fragment = new AddMovieFragment();
        Bundle args = new Bundle();
        args.putSerializable("movie_to_edit", movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieToEdit = (Movie) getArguments().getSerializable("movie_to_edit");
        }
    }

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

        if (movieToEdit != null) {
            showForm();
            prefillForm(movieToEdit);
        }

        binding.ivAddPlaceholder.setOnClickListener(v -> showForm());
        binding.fabAddMovie.setOnClickListener(v -> showForm());
        binding.btnCancelAdd.setOnClickListener(v -> {
            if (movieToEdit != null) {
                getParentFragmentManager().popBackStack();
            } else {
                hideForm();
            }
        });

        binding.btnSaveMovie.setOnClickListener(v -> saveMovie());
    }

    private void prefillForm(Movie movie) {
        binding.etAddTitle.setText(movie.getJudul());
        binding.etAddCategory.setText(movie.getKategori());
        binding.etAddRating.setText(movie.getSkorRating());
        binding.etAddPoster.setText(movie.getGambarPoster());
        binding.etAddTrailer.setText(movie.getUrlTrailer());
        binding.etAddDescription.setText(movie.getRingkasan());
        binding.btnSaveMovie.setText("Update Movie");
    }

    private void saveMovie() {
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

        Movie movieData = movieToEdit != null ? movieToEdit : new Movie();
        movieData.setJudul(title);
        movieData.setKategori(category);
        movieData.setSkorRating(rating);
        movieData.setGambarPoster(poster);
        movieData.setUrlTrailer(trailer);
        movieData.setRingkasan(description);
        movieData.setUserId(currentUsername);

        if (movieToEdit == null) {
            movieData.setTanggalRilis(System.currentTimeMillis() / 1000);
            ApiService.createMovie(movieData, new ApiService.ApiCallback<Movie>() {
                @Override
                public void onSuccess(Movie result) {
                    handleSaveSuccess("Movie berhasil ditambahkan!");
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getContext(), "Gagal menyimpan: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ApiService.updateMovie(movieToEdit.getId(), movieData, new ApiService.ApiCallback<Movie>() {
                @Override
                public void onSuccess(Movie result) {
                    handleSaveSuccess("Movie berhasil diperbarui!");
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getContext(), "Gagal memperbarui: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void handleSaveSuccess(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            clearForm();
            if (movieToEdit != null) {
                getParentFragmentManager().popBackStack();
            } else {
                fetchMyMovies();
                hideForm();
            }
        }
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter();
        binding.rvAddedMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAddedMovies.setAdapter(adapter);

        updateSavedStateInAdapter();

        adapter.setOnSaveClickListener(movie -> {
            boolean isSaved = sessionManager.toggleMovieLocally(movie);
            String msg = isSaved ? "Berhasil simpan ke favorit" : "Dihapus dari favorit";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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
        for (Movie m : savedMovies) savedIds.add(m.getId());
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
        binding.btnSaveMovie.setText("Simpan Movie");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSavedStateInAdapter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
