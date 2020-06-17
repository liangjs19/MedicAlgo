package com.mbaxajl3.medicalgo;

import android.content.Context;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.mbaxajl3.medicalgo.controllers.FavouritesController;
import com.mbaxajl3.medicalgo.controllers.JSONController;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;
import static com.mbaxajl3.medicalgo.TestUtils.addDelay;
import static com.mbaxajl3.medicalgo.TestUtils.clickChildViewWithId;
import static com.mbaxajl3.medicalgo.TestUtils.skipIntro;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FavouritesAdapterTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    private FavouritesController favouritesController;
    private Context context;
    private JSONController jsonController;

    @Before
    public void setUp() throws InterruptedException {
        context = activityRule.getActivity();
        skipIntro(context);
        Preferences.setList("favourites", "favourites", null, context);
        favouritesController = Factory.getFavouritesController();

        jsonController = Factory.getJSONController();
        favouritesController.like(jsonController.getAlgorithmMetadataById(0));
        onView(withId(R.id.nav_bar_item_favourites)).perform(click());
    }

    @Test
    public void clickOnItem() throws InterruptedException {
        onView(withId(R.id.favourites_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        addDelay();
        assertThat(Fragments.getCurrentFragment()).isEqualTo(Fragments.AlgorithmFragment);
        onView(withId(R.id.nav_bar_item_favourites)).perform(click());
    }
    // doesnt work
//    @Test
//    public void like() throws InterruptedException {
//        onView(withId(R.id.favourites_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.favourite_button)));
//        int favouritesCount = favouritesController.getFavourites().size();
//        addDelay();
//        onView(withId(R.id.favourites_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.favourite_button)));
//        assertThat(favouritesController.getFavourites().size()).isEqualTo(favouritesCount + 1);
//    }

    @Test
    public void unlike() {
        int favouritesCount = favouritesController.getFavourites().size();
        onView(withId(R.id.favourites_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.favourite_button)));
        assertThat(favouritesController.getFavourites().size()).isEqualTo(favouritesCount - 1);
    }

    @After
    public void cleanup() {
        Preferences.setList("favourites", "favourites", null, context);
        Preferences.saveOnboarding(false, activityRule.getActivity());
    }

}
