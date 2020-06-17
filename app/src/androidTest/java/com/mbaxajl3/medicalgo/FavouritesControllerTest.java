package com.mbaxajl3.medicalgo;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.mbaxajl3.medicalgo.controllers.FavouritesController;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.AlgorithmMetadata;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static com.mbaxajl3.medicalgo.TestUtils.skipIntro;

@RunWith(AndroidJUnit4.class)
public class FavouritesControllerTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    private JSONController jsonController;
    private AlgorithmMetadata algorithm;
    private FavouritesController favouritesController;
    private Context context;

    @Before
    public void setUp() {
        context = activityRule.getActivity();
        jsonController = Factory.getJSONController();
        jsonController.getCategories();
        algorithm = jsonController.getAlgorithmMetadataById(0);

        favouritesController = Factory.getFavouritesController();
        Preferences.setList("favourites", "favourites", null, context);

        skipIntro(context);
    }

    @Test
    public void getList_Empty() {
        favouritesController.getFavourites();
        assertThat(favouritesController.getFavourites()).isEmpty();
    }

    @Test
    public void like() {
        assertThat(favouritesController.like(algorithm)).isTrue();
        assertThat(favouritesController.getFavourites().size()).isEqualTo(1);
    }

    @Test
    public void like_Null() {
        assertThat(favouritesController.like(null)).isFalse();
    }

    @Test
    public void unlike() {
        favouritesController.like(algorithm);
        assertThat(favouritesController.getFavourites().size()).isEqualTo(1);
        favouritesController.unlike(algorithm);
        assertThat(favouritesController.getFavourites().size()).isEqualTo(0);
    }

    @Test
    public void unlike_Null() {
        assertThat(favouritesController.unlike(null)).isFalse();
    }

    @Test
    public void unlike_NotInList() {
        AlgorithmMetadata algorithmMetadata = jsonController.getAlgorithmMetadataById(1);
        assertThat(favouritesController.unlike(algorithmMetadata)).isFalse();
    }

    @Test
    public void isAlgorithmInFavourites() {
        favouritesController.like(algorithm);
        assertThat(favouritesController.getFavourites().size()).isEqualTo(1);
        assertThat(favouritesController.isAlgorithmInFavourites(algorithm)).isTrue();
    }

    @Test
    public void isAlgorithmInFavourites_Null() {
        favouritesController.like(algorithm);
        assertThat(favouritesController.getFavourites().size()).isEqualTo(1);
        assertThat(favouritesController.isAlgorithmInFavourites(null)).isFalse();
    }

    @Test
    public void isAlgorithmInFavourites_NotFound() {
        AlgorithmMetadata algorithmMetadata = jsonController.getAlgorithmMetadataById(1);
        assertThat(favouritesController.isAlgorithmInFavourites(algorithmMetadata)).isFalse();
    }

    @After
    public void cleanup() {
        Preferences.saveOnboarding(false, activityRule.getActivity());
    }
}
