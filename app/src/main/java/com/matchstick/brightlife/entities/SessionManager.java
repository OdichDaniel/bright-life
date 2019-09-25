package com.matchstick.brightlife.entities;

import android.content.SharedPreferences;

public class SessionManager {
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    private SessionManager(SharedPreferences prefs) {
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    static SessionManager INSTANCE = null;

    public static synchronized SessionManager getInstance(SharedPreferences prefs) {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager(prefs);
        }
        return INSTANCE;
    }

    public void saveToken(String token) {
        editor.putString(Constant.ACCESS_TOKEN, token).commit();
    }

    public void deleteToken() {
        editor.remove(Constant.ACCESS_TOKEN).commit();
    }


    public String getToken() {
        String token = prefs.getString(Constant.ACCESS_TOKEN, null);
        return token;
    }
}