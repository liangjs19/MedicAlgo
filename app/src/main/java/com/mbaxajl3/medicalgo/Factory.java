package com.mbaxajl3.medicalgo;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mbaxajl3.medicalgo.controllers.AssessmentController;
import com.mbaxajl3.medicalgo.controllers.FavouritesController;
import com.mbaxajl3.medicalgo.controllers.JSONController;

public class Factory {
    private static RequestQueue mRequestQueue;
    private static SpeechService speechService;
    private static JSONController jsonController;
    private static AssessmentController assessmentController;
    private static FavouritesController favouritesController;

    static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        jsonController = new JSONController(context);
        speechService = new SpeechService(context);
        assessmentController = new AssessmentController(context);
        favouritesController = new FavouritesController(context);
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public static SpeechService getSpeechService() {
        if (speechService != null) {
            return speechService;
        } else {
            throw new IllegalStateException("SpeechService not initialized");
        }
    }

    public static JSONController getJSONController() {
        if (jsonController != null) {
            return jsonController;
        } else {
            throw new IllegalStateException("JSONController not initialized");
        }
    }

    public static AssessmentController getAssessmentController() {
        if (assessmentController != null) {
            return assessmentController;
        } else {
            throw new IllegalStateException("AssessmentController not initialized");
        }
    }

    public static FavouritesController getFavouritesController() {
        if (favouritesController != null) {
            return favouritesController;
        } else {
            throw new IllegalStateException("FavouritesController not initialized");
        }
    }
}
