package com.kelompoklima.cinemape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kelompoklima.cinemape.databinding.FragmentEditMovieBinding;

public class EditMovieFragment extends Fragment {

    private FragmentEditMovieBinding binding;
    private Movie movie;

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
        binding.etEditDescription.setText(movie.getRingkasan());
    }

    private void updateMovie() {
        String title = binding.etEditTitle.getText().toString();
        String category = binding.etEditCategory.getText().toString();
        String rating = binding.etEditRating.getText().toString();
        String description = binding.etEditDescription.getText().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Judul dan Ringkasan wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        movie.setJudul(title);
        movie.setKategori(category);
        movie.setSkorRating(rating);
        movie.setRingkasan(description);

        ApiService.updateMovie(movie.getId(), movie, new ApiService.ApiCallback<Movie>() {
            @Override
            public void onSuccess(Movie result) {
                if (isAdded()) {
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
