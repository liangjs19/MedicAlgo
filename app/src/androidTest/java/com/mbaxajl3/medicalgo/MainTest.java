package com.mbaxajl3.medicalgo;


import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;
import static com.mbaxajl3.medicalgo.TestUtils.skipIntro;

@RunWith(AndroidJUnit4.class)
public class MainTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        skipIntro(activityRule.getActivity());
    }

    @Test
    public void clickOnFavorites_ReturnsTrue() throws InterruptedException {
        onView(withId(R.id.nav_bar_item_favourites)).perform(click());
        TestUtils.addDelay();
        assertThat(Fragments.getCurrentFragment()).isEqualTo(Fragments.FavouritesFragment);
    }

    @Test
    public void clickOnAssessments_ReturnsTrue() throws InterruptedException {
        onView(withId(R.id.nav_bar_item_assessment)).perform(click());
        TestUtils.addDelay();
        assertThat(Fragments.getCurrentFragment()).isEqualTo(Fragments.AssessmentFragment);
    }

    @Test
    public void clickOnSearch_ReturnsTrue() throws InterruptedException {
        onView(withId(R.id.nav_bar_item_search)).perform(click());
        TestUtils.addDelay();
        assertThat(Fragments.getCurrentFragment()).isEqualTo(Fragments.SearchFragment);
        closeSoftKeyboard();
    }

    @Test
    public void clickOnSearchResults() throws InterruptedException {
        onView(withId(R.id.nav_bar_item_search)).perform(click());
        TestUtils.addDelay();
        assertThat(Fragments.getCurrentFragment()).isEqualTo(Fragments.SearchFragment);
        onView(withId(R.id.search_algorithms_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        TestUtils.addDelay();
        assertThat(Fragments.getCurrentFragment()).isEqualTo(Fragments.AlgorithmFragment);
        closeSoftKeyboard();
    }

    @After
    public void cleanup() {
        closeSoftKeyboard();
        Preferences.saveOnboarding(false, activityRule.getActivity());
    }

}
