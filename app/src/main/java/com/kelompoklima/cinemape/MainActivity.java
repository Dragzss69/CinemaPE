package com.kelompoklima.cinemape;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.kelompoklima.cinemape.databinding.ActivityMainBinding;

/**
 * MainActivity adalah activity utama yang berfungsi sebagai container untuk berbagai Fragment.
 * Mengelola Bottom Navigation dan Toolbar (App Bar).
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inisialisasi SessionManager untuk mengecek status login
        sessionManager = new SessionManager(this);

        // Alur: Jika user belum login, lempar kembali ke LoginActivity
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Tutup MainActivity agar tidak bisa di-back ke sini
            return;
        }

        // Menggunakan ViewBinding untuk mengakses elemen UI
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Mengatur toolbar sebagai Action Bar aplikasi
        setSupportActionBar(binding.toolbar);

        // Saat pertama kali dibuka, tampilkan HomeFragment
        if (savedInstanceState == null) loadFragment(new HomeFragment());

        // Logika navigasi bawah (Bottom Navigation)
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) loadFragment(new HomeFragment());
            else if (id == R.id.navigation_add) loadFragment(new AddMovieFragment());
            else if (id == R.id.navigation_saved) loadFragment(new SavedFragment());
            return true;
        });
    }

    /**
     * Membuat menu pada Toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Menangani klik pada item di Toolbar.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Jika ikon profil diklik, tampilkan dialog profil
        if (item.getItemId() == R.id.action_profile) {
            new ProfileDialogFragment().show(getSupportFragmentManager(), "ProfileDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fungsi pembantu untuk mengganti Fragment di dalam fragment_container.
     */
    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
