package com.kelompoklima.cinemape;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.kelompoklima.cinemape.databinding.LayoutProfileDialogBinding;

/**
 * ProfileDialogFragment menampilkan jendela pop-up (dialog) informasi profil pengguna.
 * Di sini user bisa melihat username yang sedang login dan melakukan Logout.
 */
public class ProfileDialogFragment extends DialogFragment {

    private LayoutProfileDialogBinding binding; // Akses UI menggunakan View Binding
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mengatur gaya dialog agar tidak memiliki judul bawaan dan menggunakan tema kustom
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout layout_profile_dialog.xml
        binding = LayoutProfileDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        // 1. Ambil username dari session dan tampilkan di layar
        String username = sessionManager.getUsername();
        if (username != null) {
            binding.tvUsername.setText(username);
        }

        // 2. Logika tombol Logout
        binding.btnLogout.setOnClickListener(v -> {
            // Hapus status login di SharedPreferences
            sessionManager.logout();
            Toast.makeText(getContext(), "Berhasil Logout: " + username, Toast.LENGTH_SHORT).show();
            
            // Arahkan kembali ke halaman Login dan bersihkan tumpukan halaman (clear task)
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            // Tutup dialog
            dismiss();
            
            // Tutup activity utama agar benar-benar keluar
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Mencegah kebocoran memori (memory leak)
    }
}
