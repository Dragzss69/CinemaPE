package com.kelompoklima.cinemape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kelompoklima.cinemape.databinding.FragmentProfileBinding;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dummy Data untuk Riwayat Film
        List<Movie> historyMovies = new ArrayList<>();
        historyMovies.add(new Movie("Avengers: Endgame", "Watched on 20 Oct 2023"));
        historyMovies.add(new Movie("Parasite", "Watched on 15 Oct 2023"));
        historyMovies.add(new Movie("Joker", "Watched on 10 Oct 2023"));

        MovieAdapter adapter = new MovieAdapter(historyMovies);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}