package com.kelompoklima.cinemape;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "CinemaPE_Session";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(User user) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_ID, null);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}
