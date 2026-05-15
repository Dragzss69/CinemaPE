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

public class ProfileDialogFragment extends DialogFragment {

    private LayoutProfileDialogBinding binding;
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Style for the "floating" look
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutProfileDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        // Menampilkan email yang sedang login
        String email = sessionManager.getEmail();
        if (email != null) {
            binding.tvUserEmail.setText(email);
        }

        binding.btnLogout.setOnClickListener(v -> {
            // Proses Logout
            sessionManager.logout();
            Toast.makeText(getContext(), "Berhasil Logout: " + email, Toast.LENGTH_SHORT).show();
            
            // Pindah ke LoginActivity dan tutup semua activity sebelumnya
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            dismiss();
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
