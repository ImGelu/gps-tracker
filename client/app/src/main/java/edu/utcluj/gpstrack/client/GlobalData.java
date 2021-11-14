package edu.utcluj.gpstrack.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import edu.utcluj.gpstrack.client.room.User;

public abstract class GlobalData {
    public final static String USERS_DB_NAME = "users.db";
    public final static String LOGGED_IN_USER = "LOGGED_IN_USER";
    public final static String ENDPOINT = "ENDPOINT";

    private static User loggedInUser;
    private static String endpoint;
    public static String defaultEndpoint = "10.0.2.2:8082";

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static String getEndpoint() {
        return endpoint;
    }

    public static void setLoggedInUser(User loggedInUser, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        if (loggedInUser != null) {
            String userJson = gson.toJson(loggedInUser);
            editor.putString(GlobalData.LOGGED_IN_USER, userJson);
        } else {
            editor.putString(GlobalData.LOGGED_IN_USER, null);
        }

        editor.apply();

        if (loggedInUser != null) {
            GlobalData.loggedInUser = gson.fromJson(preferences.getString(GlobalData.LOGGED_IN_USER, ""), User.class);
        } else {
            GlobalData.loggedInUser = null;
        }

    }

    public static void setEndpoint(String ip, int port, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        if (preferences.getString(GlobalData.ENDPOINT, null) != null) {
            editor.putString(GlobalData.ENDPOINT, ip + ":" + port);
        } else {
            editor.putString(GlobalData.ENDPOINT, GlobalData.defaultEndpoint);
        }

        editor.apply();

        GlobalData.endpoint = preferences.getString(GlobalData.ENDPOINT, GlobalData.defaultEndpoint);
    }
}
