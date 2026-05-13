package com.kelompoklima.cinemape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kelompoklima.cinemape.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dummy Data for Suggested Movies
        List<Movie> suggestedMovies = new ArrayList<>();
        suggestedMovies.add(new Movie("The Shawshank Redemption", "Two imprisoned men bond over a number of years."));
        suggestedMovies.add(new Movie("The Godfather", "The aging patriarch of an organized crime dynasty."));
        suggestedMovies.add(new Movie("The Dark Knight", "When the menace known as the Joker wreaks havoc."));
        suggestedMovies.add(new Movie("Inception", "A thief who steals corporate secrets through the use of dream-sharing technology."));
        suggestedMovies.add(new Movie("Interstellar", "A team of explorers travel through a wormhole in space."));

        MovieAdapter adapter = new MovieAdapter(suggestedMovies);
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMovies.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}