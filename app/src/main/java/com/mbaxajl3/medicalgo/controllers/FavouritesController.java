package com.mbaxajl3.medicalgo.controllers;

import android.content.Context;

import com.mbaxajl3.medicalgo.Preferences;
import com.mbaxajl3.medicalgo.models.AlgorithmMetadata;

import java.util.ArrayList;
import java.util.List;

import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_FAVOURITES;

public class FavouritesController {
    private final static String TAG = "FavouritesController";
    private Context context;
    private List<AlgorithmMetadata> favourites;

    public FavouritesController(Context context) {
        this.context = context;
        favourites = getFavourites();
    }

    public boolean like(AlgorithmMetadata algorithmMetadata) {
        if (algorithmMetadata == null) {
            return false;
        }

        favourites = getFavourites();
        favourites.add(algorithmMetadata);
        saveFavourites();

        return true;
    }

    public boolean unlike(AlgorithmMetadata algorithmMetadata) {
        if (removeAlgorithmFromFavourites(algorithmMetadata)) {
            saveFavourites();
            return true;
        }

        return false;
    }

    public List<AlgorithmMetadata> getFavourites() {
        favourites = Preferences.getFavouritesList(SHARE_PREF_FAVOURITES, context);

        if (favourites == null) {
            favourites = new ArrayList<>();
        }

        return favourites;
    }

    public boolean isAlgorithmInFavourites(AlgorithmMetadata algorithm) {
        if (algorithm == null) {
            return false;
        }

        for (AlgorithmMetadata a : favourites) {
            if (a.getAlgorithmId() == algorithm.getAlgorithmId()) {
                return true;
            }
        }

        return false;
    }

    private boolean removeAlgorithmFromFavourites(AlgorithmMetadata algorithm) {
        if (algorithm == null) {
            return false;
        }

        for (AlgorithmMetadata a : favourites) {
            if (a.getAlgorithmId() == algorithm.getAlgorithmId()) {
                favourites.remove(a);
                return true;
            }
        }
        return false;
    }

    private void saveFavourites() {
        Preferences.setList(SHARE_PREF_FAVOURITES, SHARE_PREF_FAVOURITES, favourites, context);
        favourites = getFavourites();
    }
}
