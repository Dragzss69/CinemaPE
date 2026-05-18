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

import com.kelompoklima.cinemape.Model.Movie;
import com.kelompoklima.cinemape.R;
import com.kelompoklima.cinemape.Session.SessionManager;
import com.kelompoklima.cinemape.databinding.FragmentSavedBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * SavedFragment menampilkan daftar film yang telah disimpan ke favorit oleh user.
 * Data diambil secara lokal dari SharedPreferences melalui SessionManager.
 */
public class SavedFragment extends Fragment {

    private FragmentSavedBinding binding;
    private MovieAdapter adapter;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout fragment_saved.xml menggunakan View Binding
        binding = FragmentSavedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        setupRecyclerView();
        loadSavedMoviesLocal(); // Memuat data saat fragment pertama kali dibuat
    }

    /**
     * Konfigurasi RecyclerView: Adapter, LayoutManager, dan Klik Listener.
     */
    private void setupRecyclerView() {
        adapter = new MovieAdapter();
        binding.rvSavedMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSavedMovies.setAdapter(adapter);

        // 1. Listener untuk menghapus film dari daftar favorit (ikon save diklik)
        adapter.setOnSaveClickListener(movie -> {
            boolean isSaved = sessionManager.toggleMovieLocally(movie);
            if (!isSaved) {
                Toast.makeText(getContext(), "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
                loadSavedMoviesLocal(); // Refresh list agar item yang dihapus hilang dari layar
            }
        });

        // 2. Listener untuk klik pada item film (buka halaman Detail Movie)
        adapter.setOnItemClickListener(movie -> {
            DetailMovieFragment detailFragment = DetailMovieFragment.newInstance(movie);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    /**
     * Mengambil data favorit dari SharedPreferences dan menampilkannya ke UI.
     */
    private void loadSavedMoviesLocal() {
        List<Movie> savedMovies = sessionManager.getSavedMoviesLocally();
        
        // Mengirimkan daftar ID favorit ke adapter agar pewarnaan ikon sesuai
        List<String> savedIds = new ArrayList<>();
        for (Movie m : savedMovies) savedIds.add(m.getId());
        adapter.setSavedMovieIds(savedIds);

        // Jika tidak ada film yang disimpan, tampilkan layout kosong
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
        // Refresh data setiap kali user masuk kembali ke fragment ini
        loadSavedMoviesLocal();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
