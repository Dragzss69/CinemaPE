package com.kelompoklima.cinemape;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kelompoklima.cinemape.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HomeFragment adalah halaman utama yang menampilkan daftar semua film dari API.
 * Memiliki fitur pencarian dan fitur simpan favorit.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding; // View Binding untuk akses UI
    private MovieAdapter adapter; // Adapter untuk RecyclerView
    private List<Movie> allMovies = new ArrayList<>(); // Menampung semua data film dari API
    private SessionManager sessionManager; // Untuk mengelola data favorit lokal

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout menggunakan View Binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        // 1. Inisialisasi RecyclerView & Adapter
        adapter = new MovieAdapter();
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMovies.setAdapter(adapter);

        // Update status favorit agar ikon berwarna orange jika sudah disimpan sebelumnya
        updateSavedStateInAdapter();

        // 2. Listener untuk tombol SIMPAN (Favorit) pada tiap item
        adapter.setOnSaveClickListener(movie -> {
            boolean isSaved = sessionManager.toggleMovieLocally(movie);
            String msg = isSaved ? "Berhasil simpan ke favorit" : "Dihapus dari favorit";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            
            // Refresh warna icon di adapter setelah diklik (Toggle)
            updateSavedStateInAdapter();
        });

        // 3. Listener untuk KLIK ITEM (Buka halaman Detail Movie)
        adapter.setOnItemClickListener(movie -> {
            DetailMovieFragment detailFragment = DetailMovieFragment.newInstance(movie);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null) // Memungkinkan user kembali ke Home dengan tombol back HP
                    .commit();
        });

        // 4. Logika Pencarian (Search Bar) - Berjalan saat teks di etSearch berubah
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString()); // Filter list berdasarkan input user
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Memulai pengambilan data dari server
        fetchMovies();
    }

    /**
     * Sinkronisasi daftar ID favorit dari SharedPreferences ke Adapter.
     * Tujuannya agar ikon favorit tahu kapan harus berwarna orange.
     */
    private void updateSavedStateInAdapter() {
        List<Movie> savedMovies = sessionManager.getSavedMoviesLocally();
        List<String> savedIds = new ArrayList<>();
        for (Movie m : savedMovies) {
            savedIds.add(m.getId());
        }
        adapter.setSavedMovieIds(savedIds);
    }

    /**
     * Memanggil ApiService untuk mendapatkan semua data film dari MockAPI.
     */
    private void fetchMovies() {
        ApiService.getAllMovie(new ApiService.ApiCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> result) {
                if (isAdded()) { // Pastikan fragment masih menempel pada Activity
                    allMovies = result; // Simpan data asli untuk keperluan filter/search
                    adapter.setMovieList(result); // Tampilkan di UI
                }
            }
            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Menyaring list film berdasarkan judul yang diketik user di kolom pencarian.
     */
    private void filterMovies(String query) {
        if (allMovies == null) return;
        List<Movie> filteredList = allMovies.stream()
                .filter(movie -> movie.getJudul() != null && 
                        movie.getJudul().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        adapter.setMovieList(filteredList);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh status favorit saat kembali ke tab Home (misal setelah dari tab Saved)
        updateSavedStateInAdapter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Membersihkan binding untuk menghindari memory leak
    }
}
