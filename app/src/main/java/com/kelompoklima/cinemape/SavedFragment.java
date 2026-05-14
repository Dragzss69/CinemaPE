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

        // Inisialisasi list kosong (data dummy dihapus)
        List<Movie> savedMovies = new ArrayList<>();

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