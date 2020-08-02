package com.mbaxajl3.medicalgo.controllers;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mbaxajl3.medicalgo.Callback;
import com.mbaxajl3.medicalgo.Factory;
import com.mbaxajl3.medicalgo.models.Entity;
import com.mbaxajl3.medicalgo.models.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NLUController {
    public final static String TAG = "NLUController";
    public final static String appId = "e66f327a-e965-4d73-ba96-d637cd89e4e3";
    public final static String key = "395e955eee534992848dbc9a53bbd268";
    public final static String endpoint = "medicalgo.cognitiveservices.azure.com/";

    public static void get(String utterance, final Callback callback) {
        // Begin endpoint URL string building
        String uri = "https://" + endpoint + "/luis/prediction/v3.0/apps/" + appId + "/slots/production/predict";
        Uri builtUri = Uri.parse(uri).buildUpon()
                .appendQueryParameter("subscription-key", key)
                .appendQueryParameter("verbose", "true")
                .appendQueryParameter("log", "true")
                .appendQueryParameter("query", utterance)
                .build();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, builtUri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, response.toString());
                        try {
                            callback.onSuccess(parse(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e(TAG, "Error: " + error.toString());
                        try {
                            callback.onError(error.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

// Access the RequestQueue through your singleton class.
        Factory.getRequestQueue().add(jsonObjectRequest);
    }

    public static void cancelAllRequests() {
        Factory.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    public static Pair<String, List<Entity>> parse(JSONObject obj) throws JSONException {
        List<Entity> entityList = new ArrayList<>();
        JSONObject prediction = obj.getJSONObject("prediction");
        String intent = prediction.getString("topIntent");

        JSONObject objEntities = prediction.optJSONObject("entities");

        if (objEntities != null) {
            Iterator<String> keys = objEntities.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                if (objEntities.get(key) instanceof JSONArray) {
                    JSONArray entities = (JSONArray) objEntities.get(key);

                    List<String> values = null;

                    Set<String> entitiesSet = new HashSet<>();
                    for (int i = 0; i < entities.length(); i++) {
                        if (entities.get(i) instanceof JSONArray) {
                            JSONArray entity = (JSONArray) entities.get(i);

                            for (int j = 0; j < entity.length(); j++) {
                                Entity newEntity = new Entity(entity.get(j).toString());

                                if (!entitiesSet.contains(entity.get(j).toString())) {
                                    entityList.add(new Entity(entity.get(j).toString()));
                                    entitiesSet.add(entity.get(j).toString());
                                }
                            }
                        } else if (entities.get(i) instanceof String) {
                            if (values == null)
                                values = new ArrayList<>();
                            values.add(entities.get(i).toString());
                        }
                    }

                    if (values != null) {
                        entityList.add(new Entity(key, values));
                    }
                }
            }
        }

        return new Pair<>(intent, entityList);
    }
}
