package com.kelompoklima.cinemape;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager berfungsi untuk mengelola penyimpanan data lokal sederhana (SharedPreferences).
 * Kelas ini digunakan untuk mensimulasikan database (pendaftaran user) dan menjaga status login.
 */
public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    
    // Nama file penyimpanan yang akan dibuat di sistem Android
    private static final String PREF_NAME = "CinemaPE_Session";

    // Kunci (key) untuk menandai apakah user sudah login atau belum
    private static final String IS_LOGGED_IN = "isLoggedIn";
    
    // Kunci (key) untuk menyimpan username yang sedang aktif login
    private static final String KEY_LOGGED_IN_USERNAME = "loggedInUsername";

    // Kunci (key) untuk menyimpan data Registrasi (Simulasi Database Lokal)
    private static final String KEY_REGISTERED_USERNAME = "reg_username";
    private static final String KEY_REGISTERED_PASS = "reg_pass";

    public SessionManager(Context context) {
        // Inisialisasi SharedPreferences dengan mode private agar hanya aplikasi ini yang bisa mengakses
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Fungsi untuk menyimpan data pendaftaran user ke penyimpanan lokal HP.
     */
    public void registerUser(String username, String password) {
        editor.putString(KEY_REGISTERED_USERNAME, username);
        editor.putString(KEY_REGISTERED_PASS, password);
        editor.apply(); // Simpan data secara background
    }

    /**
     * Fungsi untuk memvalidasi apakah username dan password cocok dengan data yang terdaftar.
     */
    public boolean checkLogin(String username, String password) {
        String regUser = sharedPreferences.getString(KEY_REGISTERED_USERNAME, "");
        String regPass = sharedPreferences.getString(KEY_REGISTERED_PASS, "");
        
        // Cek apakah input sama dengan data yang tersimpan
        return username.equals(regUser) && password.equals(regPass);
    }

    /**
     * Fungsi untuk membuat sesi login aktif (aplikasi akan ingat user sudah masuk).
     */
    public void createLoginSession(String username) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_LOGGED_IN_USERNAME, username);
        editor.apply();
    }

    /**
     * Mengecek status apakah user sedang login atau tidak.
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    /**
     * Mengambil username user yang sedang login saat ini.
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_LOGGED_IN_USERNAME, null);
    }

    /**
     * Menghapus status login saja (digunakan saat Logout).
     */
    public void logout() {
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(KEY_LOGGED_IN_USERNAME, null);
        editor.apply();
    }
}
