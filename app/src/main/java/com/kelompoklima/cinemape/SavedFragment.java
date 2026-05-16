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
import com.kelompoklima.cinemape.databinding.FragmentSavedBinding;
import java.util.List;

public class SavedFragment extends Fragment {

    private FragmentSavedBinding binding;
    private MovieAdapter adapter;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        setupRecyclerView();
        loadSavedMoviesLocal();
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter();
        binding.rvSavedMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSavedMovies.setAdapter(adapter);

        // Tambahkan logika toggle (hapus dari favorit jika diklik lagi)
        adapter.setOnSaveClickListener(movie -> {
            boolean isSaved = sessionManager.toggleMovieLocally(movie);
            if (!isSaved) {
                Toast.makeText(getContext(), "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
                loadSavedMoviesLocal(); // Refresh list setelah dihapus
            }
        });
    }

    private void loadSavedMoviesLocal() {
        // Mengambil data dari penyimpanan lokal (SessionManager)
        List<Movie> savedMovies = sessionManager.getSavedMoviesLocally();

        if (savedMovies == null || savedMovies.isEmpty()) {
            binding.layoutEmptySaved.setVisibility(View.VISIBLE);
            binding.rvSavedMovies.setVisibility(View.GONE);
        } else {
            binding.layoutEmptySaved.setVisibility(View.GONE);
            binding.rvSavedMovies.setVisibility(View.VISIBLE);
            adapter.setMovieList(savedMovies);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data setiap kali user membuka tab Saved
        loadSavedMoviesLocal();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}