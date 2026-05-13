package com.kelompoklima.cinemape;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationBarView;
import com.kelompoklima.cinemape.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set Toolbar sebagai ActionBar
        setSupportActionBar(binding.toolbar);

        // Load fragment pertama kali (Home)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Listener untuk navigasi bawah
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    fragment = new HomeFragment();
                } else if (itemId == R.id.navigation_search) {
                    fragment = new SearchFragment();
                } else if (itemId == R.id.navigation_profile) {
                    fragment = new ProfileFragment();
                }

                if (fragment != null) {
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}