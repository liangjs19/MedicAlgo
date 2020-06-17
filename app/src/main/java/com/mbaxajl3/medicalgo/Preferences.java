package com.mbaxajl3.medicalgo;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mbaxajl3.medicalgo.models.AlgorithmMetadata;
import com.mbaxajl3.medicalgo.models.Assessment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_ACCESS_KEY;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_API_KEY;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_ASSESSMENT;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_ENGINE;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_FAVOURITES;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_HOTWORD;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_ONBOARDING;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_SECRET_KEY;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_TRANSCRIPT;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_TTS;
public class Preferences {
    public static void saveAccessKey(String key, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(SHARE_PREF_ACCESS_KEY, key)
                .apply();
    }

    public static void saveSecretKey(String key, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(SHARE_PREF_SECRET_KEY, key)
                .apply();
    }

    public static void saveApiKey(String key, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(SHARE_PREF_API_KEY, key)
                .apply();
    }

    public static void saveEnginePref(String key, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(SHARE_PREF_ENGINE, key)
                .apply();
    }

    public static void saveOnboarding(boolean onboarding, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SHARE_PREF_ONBOARDING, onboarding)
                .apply();
    }

    public static void saveHotword(boolean hotword, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SHARE_PREF_HOTWORD, hotword)
                .apply();
    }

    public static void set(String preference, String key, String value, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static List<AlgorithmMetadata> getFavouritesList(String key, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SHARE_PREF_FAVOURITES, Context.MODE_PRIVATE);
        List<AlgorithmMetadata> arrayItems = new ArrayList<>();
        String serializedObject = sharedpreferences.getString(key, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<AlgorithmMetadata>>() {
            }.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return arrayItems;
    }

    public static List<Assessment> getAssessmentsList(String key, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SHARE_PREF_ASSESSMENT, Context.MODE_PRIVATE);
        List<Assessment> arrayItems = new ArrayList<>();
        String serializedObject = sharedpreferences.getString(key, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Assessment>>() {
            }.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return arrayItems;
    }

    public static <T> void setList(String preference, String key, List<T> list, Context context) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        set(preference, key, json, context);
    }

    public static String getApiKey(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SHARE_PREF_API_KEY, "");
    }

    public static String getEngine(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SHARE_PREF_ENGINE, "none");
    }

    public static boolean getTranscript(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SHARE_PREF_TRANSCRIPT, true);
    }

    public static boolean getHotword(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SHARE_PREF_HOTWORD, false);
    }

    public static boolean getTts(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SHARE_PREF_TTS, true);
    }

    public static boolean getOnboarding(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                SHARE_PREF_ONBOARDING, false);
    }

}
