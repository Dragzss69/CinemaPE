package com.kelompoklima.cinemape.CRUD;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.kelompoklima.cinemape.API.ApiService;
import com.kelompoklima.cinemape.Model.Movie;
import com.kelompoklima.cinemape.R;
import com.kelompoklima.cinemape.Session.SessionManager;
import com.kelompoklima.cinemape.UI.HomeFragment;
import com.kelompoklima.cinemape.databinding.FragmentDeleteMovieBinding;

/**
 * DeleteMovieFragment menangani logika penghapusan film dalam satu halaman penuh.
 */
public class DeleteMovieFragment extends Fragment {

    private FragmentDeleteMovieBinding binding;
    private Movie movie;
    private SessionManager sessionManager;

    public static DeleteMovieFragment newInstance(Movie movie) {
        DeleteMovieFragment fragment = new DeleteMovieFragment();
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
        binding = FragmentDeleteMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        if (movie != null) {
            binding.tvConfirmMessage.setText("Apakah Anda yakin ingin menghapus movie '" + movie.getJudul() + "' secara permanen?");
        }

        // Tombol Konfirmasi Hapus
        binding.btnExecuteDelete.setOnClickListener(v -> deleteMovie());

        // Tombol Batal
        binding.btnCancelDelete.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void deleteMovie() {
        if (movie == null) return;

        ApiService.deleteMovie(movie.getId(), new ApiService.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isAdded()) {
                    // Hapus juga dari favorit lokal agar tidak muncul di tab Saved
                    sessionManager.removeMovieLocally(movie.getId());

                    Toast.makeText(getContext(), "Movie berhasil dihapus!", Toast.LENGTH_SHORT).show();
                    
                    // Kembali ke Home (Membersihkan backstack detail dan delete)
                    getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Gagal menghapus: " + errorMessage, Toast.LENGTH_SHORT).show();
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
