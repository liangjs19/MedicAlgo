package com.mbaxajl3.medicalgo;

import android.content.Context;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.mbaxajl3.medicalgo.controllers.FavouritesController;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;
import static com.mbaxajl3.medicalgo.TestUtils.clickChildViewWithId;
import static com.mbaxajl3.medicalgo.TestUtils.skipIntro;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchAdapterTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    private FavouritesController favouritesController;
    private Context context;

    @Before
    public void setUp() {
        context = activityRule.getActivity();
        skipIntro(context);
        Preferences.setList("favourites", "favourites", null, context);
        favouritesController = Factory.getFavouritesController();
        onView(withId(R.id.nav_bar_item_search)).perform(click());
    }

    @Test
    public void like() {
        Preferences.setList("favourites", "favourites", null, context);
        int favouritesCount = favouritesController.getFavourites().size();
        onView(withId(R.id.search_algorithms_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.favourite_button)));
        assertThat(favouritesController.getFavourites().size()).isEqualTo(favouritesCount + 1);
    }

    @Test
    public void unlike() {
        Preferences.setList("favourites", "favourites", null, context);
        onView(withId(R.id.search_algorithms_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.favourite_button)));
        int favouritesCount = favouritesController.getFavourites().size();
        onView(withId(R.id.search_algorithms_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.favourite_button)));
        assertThat(favouritesController.getFavourites().size()).isEqualTo(favouritesCount - 1);
    }

    @After
    public void cleanup() {
        Preferences.saveOnboarding(false, activityRule.getActivity());
    }
}
