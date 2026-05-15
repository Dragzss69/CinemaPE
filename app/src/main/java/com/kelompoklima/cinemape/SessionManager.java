package com.kelompoklima.cinemape;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "CinemaPE_Session";
    
    // Key untuk sesi login aktif
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LOGGED_IN_EMAIL = "loggedInEmail";

    // Key untuk data registrasi (Simulasi Database Lokal)
    private static final String KEY_REGISTERED_EMAIL = "reg_email";
    private static final String KEY_REGISTERED_PASS = "reg_pass";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // --- Fungsi Registrasi (Simpan ke Lokal) ---
    public void registerUser(String email, String password) {
        editor.putString(KEY_REGISTERED_EMAIL, email);
        editor.putString(KEY_REGISTERED_PASS, password);
        editor.apply();
    }

    // --- Fungsi Cek Login ---
    public boolean checkLogin(String email, String password) {
        String registeredEmail = sharedPreferences.getString(KEY_REGISTERED_EMAIL, "");
        String registeredPass = sharedPreferences.getString(KEY_REGISTERED_PASS, "");

        return email.equals(registeredEmail) && password.equals(registeredPass);
    }

    // --- Fungsi Sesi Login ---
    public void createLoginSession(String email) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_LOGGED_IN_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, null);
    }

    public void logout() {
        // Hanya hapus status login, bukan data registrasi
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(KEY_LOGGED_IN_EMAIL, null);
        editor.apply();
    }
}
