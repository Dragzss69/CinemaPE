package com.kelompoklima.cinemape;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kelompoklima.cinemape.Autentikasi.LoginActivity;
import com.kelompoklima.cinemape.CRUD.AddMovieFragment;
import com.kelompoklima.cinemape.UI.HomeFragment;
import com.kelompoklima.cinemape.UI.ProfileDialogFragment;
import com.kelompoklima.cinemape.UI.SavedFragment;
import com.kelompoklima.cinemape.Session.SessionManager;
import com.kelompoklima.cinemape.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if (savedInstanceState == null) loadFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) loadFragment(new HomeFragment());
            else if (id == R.id.navigation_add) loadFragment(new AddMovieFragment());
            else if (id == R.id.navigation_saved) loadFragment(new SavedFragment());
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            new ProfileDialogFragment().show(getSupportFragmentManager(), "ProfileDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
