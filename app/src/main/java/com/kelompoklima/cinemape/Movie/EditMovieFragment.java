package com.kelompoklima.cinemape.Movie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kelompoklima.cinemape.API.ApiService;
import com.kelompoklima.cinemape.Model.Movie;
import com.kelompoklima.cinemape.Session.SessionManager;
import com.kelompoklima.cinemape.databinding.FragmentEditMovieBinding;

public class EditMovieFragment extends Fragment {

    private FragmentEditMovieBinding binding;
    private Movie movie;
    private SessionManager sessionManager;

    public static EditMovieFragment newInstance(Movie movie) {
        EditMovieFragment fragment = new EditMovieFragment();
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
        binding = FragmentEditMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        if (movie != null) {
            prefillData();
        }

        binding.btnBackEdit.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        binding.btnUpdateMovie.setOnClickListener(v -> updateMovie());
    }

    private void prefillData() {
        binding.etEditTitle.setText(movie.getJudul());
        binding.etEditCategory.setText(movie.getKategori());
        binding.etEditRating.setText(movie.getSkorRating());
        binding.etEditPoster.setText(movie.getGambarPoster());
        binding.etEditTrailer.setText(movie.getUrlTrailer());
        binding.etEditDescription.setText(movie.getRingkasan());
    }

    private void updateMovie() {
        String title = binding.etEditTitle.getText().toString().trim();
        String category = binding.etEditCategory.getText().toString().trim();
        String ratingStr = binding.etEditRating.getText().toString().trim();
        String poster = binding.etEditPoster.getText().toString().trim();
        String trailer = binding.etEditTrailer.getText().toString().trim();
        String description = binding.etEditDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Judul tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ratingStr.isEmpty()) {
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
        }

        movie.setJudul(title);
        movie.setKategori(category);
        movie.setSkorRating(ratingStr);
        movie.setGambarPoster(poster);
        movie.setUrlTrailer(trailer);
        movie.setRingkasan(description);

        ApiService.updateMovie(movie.getId(), movie, new ApiService.ApiCallback<Movie>() {
            @Override
            public void onSuccess(Movie result) {
                if (isAdded()) {
                    sessionManager.updateMovieLocally(result);

                    Toast.makeText(getContext(), "Movie berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Gagal memperbarui: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
