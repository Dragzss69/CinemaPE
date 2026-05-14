package com.kelompoklima.cinemape;

import android.app.Dialog;
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

        binding.btnSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Settings Clicked", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        binding.btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}