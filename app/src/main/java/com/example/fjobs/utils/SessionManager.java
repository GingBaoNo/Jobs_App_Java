package com.example.fjobs.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getString(Constants.KEY_TOKEN, null) != null;
    }

    public int getUserId() {
        return sharedPreferences.getInt(Constants.KEY_USER_ID, -1);
    }

    public String getUserRole() {
        return sharedPreferences.getString(Constants.KEY_USER_ROLE, "");
    }

    public String getUsername() {
        return sharedPreferences.getString(Constants.KEY_USERNAME, "");
    }

    public String getToken() {
        return sharedPreferences.getString(Constants.KEY_TOKEN, "");
    }
}