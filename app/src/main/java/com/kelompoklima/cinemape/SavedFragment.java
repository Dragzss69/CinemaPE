package com.kelompoklima.cinemape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kelompoklima.cinemape.databinding.FragmentSavedBinding;
import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {

    private FragmentSavedBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dummy Data for Saved Movies
        List<Movie> savedMovies = new ArrayList<>();
        savedMovies.add(new Movie("The Godfather", "The aging patriarch of an organized crime dynasty."));
        savedMovies.add(new Movie("Interstellar", "A team of explorers travel through a wormhole in space."));

        MovieAdapter adapter = new MovieAdapter(savedMovies);
        binding.rvSavedMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSavedMovies.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}