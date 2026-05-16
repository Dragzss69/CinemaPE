package com.kelompoklima.cinemape;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * SessionManager mengelola data sesi login dan penyimpanan film favorit secara lokal.
 */
public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    
    private static final String PREF_NAME = "CinemaPE_Session";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LOGGED_IN_USERNAME = "loggedInUsername";
    private static final String KEY_REGISTERED_USERNAME = "reg_username";
    private static final String KEY_REGISTERED_PASS = "reg_pass";
    
    // Key untuk menyimpan daftar film favorit dalam bentuk JSON
    private static final String KEY_SAVED_MOVIES = "saved_movies_list";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    // --- AUTENTIKASI ---
    public void registerUser(String username, String password) {
        editor.putString(KEY_REGISTERED_USERNAME, username);
        editor.putString(KEY_REGISTERED_PASS, password);
        editor.apply();
    }

    public boolean checkLogin(String username, String password) {
        String regUser = sharedPreferences.getString(KEY_REGISTERED_USERNAME, "");
        String regPass = sharedPreferences.getString(KEY_REGISTERED_PASS, "");
        return username.equals(regUser) && password.equals(regPass);
    }

    public void createLoginSession(String username) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_LOGGED_IN_USERNAME, username);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_LOGGED_IN_USERNAME, null);
    }

    public void logout() {
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(KEY_LOGGED_IN_USERNAME, null);
        editor.apply();
    }

    // --- FITUR SIMPAN FAVORIT (LOKAL) ---

    /**
     * Mengambil daftar film yang disimpan dari SharedPreferences.
     */
    public List<Movie> getSavedMoviesLocally() {
        String json = sharedPreferences.getString(KEY_SAVED_MOVIES, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Movie>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Menyimpan atau Menghapus film dari daftar favorit (Toggle).
     * @return true jika film akhirnya tersimpan, false jika dihapus.
     */
    public boolean toggleMovieLocally(Movie movie) {
        List<Movie> savedMovies = getSavedMoviesLocally();
        boolean isAlreadySaved = false;
        int indexToRemove = -1;

        // Cek apakah film sudah ada di list berdasarkan ID
        for (int i = 0; i < savedMovies.size(); i++) {
            if (savedMovies.get(i).getId().equals(movie.getId())) {
                isAlreadySaved = true;
                indexToRemove = i;
                break;
            }
        }

        if (isAlreadySaved) {
            // Jika sudah ada, hapus dari favorit
            savedMovies.remove(indexToRemove);
        } else {
            // Jika belum ada, tambahkan ke favorit
            savedMovies.add(movie);
        }

        // Simpan kembali list yang sudah diupdate ke SharedPreferences
        String json = gson.toJson(savedMovies);
        editor.putString(KEY_SAVED_MOVIES, json);
        editor.apply();

        return !isAlreadySaved;
    }
}
