package com.kelompoklima.cinemape;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "CinemaPE_Session";
    
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_LOGGED_IN_EMAIL = "loggedInEmail";

    // Key untuk data registrasi (Simulasi)
    private static final String KEY_REGISTERED_ID = "reg_id";
    private static final String KEY_REGISTERED_EMAIL = "reg_email";
    private static final String KEY_REGISTERED_PASS = "reg_pass";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void registerUser(String id, String email, String password) {
        editor.putString(KEY_REGISTERED_ID, id);
        editor.putString(KEY_REGISTERED_EMAIL, email);
        editor.putString(KEY_REGISTERED_PASS, password);
        editor.apply();
    }

    public boolean checkLogin(String email, String password) {
        String registeredEmail = sharedPreferences.getString(KEY_REGISTERED_EMAIL, "");
        String registeredPass = sharedPreferences.getString(KEY_REGISTERED_PASS, "");
        return email.equals(registeredEmail) && password.equals(registeredPass);
    }

    public void createLoginSession(String id, String email) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, id);
        editor.putString(KEY_LOGGED_IN_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public String getUserId() {
        // Mengambil ID user yang sedang login
        return sharedPreferences.getString(KEY_USER_ID, null);
    }
    
    public String getRegisteredId() {
        return sharedPreferences.getString(KEY_REGISTERED_ID, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, null);
    }

    public void logout() {
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(KEY_USER_ID, null);
        editor.putString(KEY_LOGGED_IN_EMAIL, null);
        editor.apply();
    }
}
