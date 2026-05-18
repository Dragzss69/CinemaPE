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

/**
 * EditMovieFragment mengelola fitur untuk mengubah data film yang sudah ada.
 * Menerima objek Movie yang akan diedit melalui Bundle.
 */
public class EditMovieFragment extends Fragment {

    private FragmentEditMovieBinding binding; // Akses komponen UI
    private Movie movie; // Objek film yang sedang diedit

    /**
     * Metode static untuk membuat instance fragment baru dengan mengirim data Movie.
     */
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
        // Mengambil data film dari argumen yang dikirim saat newInstance
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

        // Jika data movie ada, isi form dengan data tersebut (pre-fill)
        if (movie != null) {
            prefillData();
        }

        // Tombol kembali ke halaman sebelumnya
        binding.btnBackEdit.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Tombol untuk mengirim pembaruan ke server
        binding.btnUpdateMovie.setOnClickListener(v -> updateMovie());
    }

    /**
     * Mengisi kolom input di form dengan data film yang lama.
     */
    private void prefillData() {
        binding.etEditTitle.setText(movie.getJudul());
        binding.etEditCategory.setText(movie.getKategori());
        binding.etEditRating.setText(movie.getSkorRating());
        binding.etEditPoster.setText(movie.getGambarPoster());
        binding.etEditTrailer.setText(movie.getUrlTrailer());
        binding.etEditDescription.setText(movie.getRingkasan());
    }

    /**
     * Mengambil data baru dari form dan mengirimkan request Update ke server.
     */
    private void updateMovie() {
        String title = binding.etEditTitle.getText().toString();
        String category = binding.etEditCategory.getText().toString();
        String rating = binding.etEditRating.getText().toString();
        String poster = binding.etEditPoster.getText().toString();
        String trailer = binding.etEditTrailer.getText().toString();
        String description = binding.etEditDescription.getText().toString();

        // Validasi input
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Judul dan Ringkasan wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update atribut objek movie
        movie.setJudul(title);
        movie.setKategori(category);
        movie.setSkorRating(rating);
        movie.setGambarPoster(poster);
        movie.setUrlTrailer(trailer);
        movie.setRingkasan(description);

        // Panggil ApiService untuk melakukan request PUT ke server
        ApiService.updateMovie(movie.getId(), movie, new ApiService.ApiCallback<Movie>() {
            @Override
            public void onSuccess(Movie result) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Movie berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                    // Kembali ke halaman sebelumnya setelah sukses
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
