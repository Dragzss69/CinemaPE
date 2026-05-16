package com.kelompoklima.cinemape;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "CinemaPE_Session";

    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LOGGED_IN_USERNAME = "loggedInUsername";

    // Data Registrasi Lokal
    private static final String KEY_REGISTERED_USERNAME = "reg_username";
    private static final String KEY_REGISTERED_PASS = "reg_pass";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

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
}
