package com.kelompoklima.cinemape.Session;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelompoklima.cinemape.Model.Movie;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * SessionManager mengelola data sesi login dan penyimpanan film favorit secara lokal.
 * Menggunakan SharedPreferences untuk menyimpan data dalam bentuk key-value.
 */
public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    
    // Nama file SharedPreferences
    private static final String PREF_NAME = "CinemaPE_Session";
    
    // Key-key yang digunakan untuk menyimpan data
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LOGGED_IN_USERNAME = "loggedInUsername";
    private static final String KEY_REGISTERED_USERNAME = "reg_username";
    private static final String KEY_REGISTERED_PASS = "reg_pass";
    
    // Key dasar untuk list favorit, nantinya digabung dengan username agar unik
    private static final String KEY_SAVED_MOVIES_BASE = "saved_movies_";

    public SessionManager(Context context) {
        // Inisialisasi SharedPreferences dengan mode private (hanya aplikasi ini yang bisa akses)
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    // --- BAGIAN AUTENTIKASI ---

    /**
     * Mendaftarkan user baru (Simpan username & password ke SP).
     */
    public void registerUser(String username, String password) {
        editor.putString(KEY_REGISTERED_USERNAME, username);
        editor.putString(KEY_REGISTERED_PASS, password);
        editor.apply(); // Simpan secara asynchronous
    }

    /**
     * Mengecek apakah input login sesuai dengan data yang terdaftar.
     */
    public boolean checkLogin(String username, String password) {
        String regUser = sharedPreferences.getString(KEY_REGISTERED_USERNAME, "");
        String regPass = sharedPreferences.getString(KEY_REGISTERED_PASS, "");
        return username.equals(regUser) && password.equals(regPass);
    }

    /**
     * Membuat sesi login setelah berhasil masuk.
     */
    public void createLoginSession(String username) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_LOGGED_IN_USERNAME, username);
        editor.apply();
    }

    /**
     * Mengecek apakah ada user yang sedang login.
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    /**
     * Mengambil username yang sedang aktif login.
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_LOGGED_IN_USERNAME, null);
    }

    /**
     * Menghapus sesi login (Logout).
     */
    public void logout() {
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(KEY_LOGGED_IN_USERNAME, null);
        editor.apply();
    }

    // --- BAGIAN FAVORIT (UNIK PER USER) ---

    /**
     * Menghasilkan key unik untuk favorit berdasarkan username (contoh: saved_movies_budi).
     */
    private String getSavedMoviesKey() {
        String username = getUsername();
        return (username != null) ? KEY_SAVED_MOVIES_BASE + username : "saved_movies_guest";
    }

    /**
     * Mengambil daftar film favorit dari SharedPreferences.
     * Data disimpan sebagai String JSON, lalu diconvert kembali ke List<Movie>.
     */
    public List<Movie> getSavedMoviesLocally() {
        String json = sharedPreferences.getString(getSavedMoviesKey(), null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Movie>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Menambah atau menghapus film dari favorit (Toggle).
     * Jika film sudah ada, maka dihapus. Jika belum ada, maka ditambah.
     */
    public boolean toggleMovieLocally(Movie movie) {
        List<Movie> savedMovies = getSavedMoviesLocally();
        boolean isAlreadySaved = false;
        int indexToRemove = -1;

        // Cari apakah film sudah ada di list
        for (int i = 0; i < savedMovies.size(); i++) {
            if (savedMovies.get(i).getId().equals(movie.getId())) {
                isAlreadySaved = true;
                indexToRemove = i;
                break;
            }
        }

        if (isAlreadySaved) {
            savedMovies.remove(indexToRemove); // Hapus jika sudah ada
        } else {
            savedMovies.add(movie); // Tambah jika belum ada
        }

        // Convert list ke JSON String dan simpan ke SharedPreferences
        String json = gson.toJson(savedMovies);
        editor.putString(getSavedMoviesKey(), json);
        editor.apply();

        return !isAlreadySaved; // Mengembalikan true jika akhirnya disimpan
    }

    /**
     * Memperbarui data film di list favorit lokal jika film tersebut ada.
     */
    public void updateMovieLocally(Movie updatedMovie) {
        List<Movie> savedMovies = getSavedMoviesLocally();
        boolean found = false;
        for (int i = 0; i < savedMovies.size(); i++) {
            if (savedMovies.get(i).getId().equals(updatedMovie.getId())) {
                savedMovies.set(i, updatedMovie);
                found = true;
                break;
            }
        }
        if (found) {
            String json = gson.toJson(savedMovies);
            editor.putString(getSavedMoviesKey(), json);
            editor.apply();
        }
    }
}
