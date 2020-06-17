package com.mbaxajl3.medicalgo;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.mbaxajl3.medicalgo.TestUtils.addDelay;
import static org.hamcrest.core.IsNot.not;

public class OnboardingTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Preferences.saveOnboarding(false, mainActivityRule.getActivity());
        onView(withId(R.id.pager_introduction)).perform(swipeLeft());
    }

    @Test
    public void clickOnGoogle() {
        onView(withId(R.id.radio_google)).perform(click());
        onView(withId(R.id.google_fields_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.amazon_fields_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void clickOnAmazon() {
        onView(withId(R.id.radio_amazon)).perform(click());
        onView(withId(R.id.google_fields_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.amazon_fields_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void clickOnNone() {
        onView(withId(R.id.radio_none)).perform(click());
        onView(withId(R.id.google_fields_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.amazon_fields_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void nothingTypedIntoGoogleFields_ButtonShouldNotBeEnabled() {
        onView(withId(R.id.radio_google)).perform(click());
        onView(withId(R.id.get_started_btn)).check(matches(not(isClickable())));
    }

    @Test
    public void typeIntoGoogleFieldsAndClear_ButtonShouldNotBeEnabled() {
        onView(withId(R.id.radio_google)).perform(click());
        onView(withId(R.id.onboarding_api_key)).perform(typeText("a"));
        onView(withId(R.id.onboarding_api_key)).perform(clearText());
        onView(withId(R.id.get_started_btn)).check(matches(not(isClickable())));
    }

    @Test
    public void typeIntoGoogleFields_ButtonShouldBeEnabled() {
        onView(withId(R.id.radio_google)).perform(click());
        onView(withId(R.id.onboarding_api_key)).perform(typeText("a"));
        onView(withId(R.id.get_started_btn)).check(matches(isClickable()));
    }

    @Test
    public void typeIntoAmazonAccessKey_ButSecretIsNot_ButtonShouldBeDisabled() {
        onView(withId(R.id.radio_amazon)).perform(click());
        onView(withId(R.id.onboarding_access_key)).perform(typeText("a"));
        onView(withId(R.id.get_started_btn)).check(matches(not(isClickable())));
    }

    @Test
    public void typeIntoAmazonSecretKey_ButSecretIsNot_ButtonShouldBeDisabled() {
        onView(withId(R.id.radio_amazon)).perform(click());
        onView(withId(R.id.onboarding_secret_key)).perform(typeText("a"));
        onView(withId(R.id.get_started_btn)).check(matches(not(isClickable())));
    }

    @Test
    public void typeIntoAmazonFields_ButtonShouldBeEnabled() {
        onView(withId(R.id.radio_amazon)).perform(click());
        onView(withId(R.id.onboarding_secret_key)).perform(typeText("a"));
        onView(withId(R.id.onboarding_access_key)).perform(typeText("a"));
        onView(withId(R.id.get_started_btn)).check(matches(isClickable()));
    }

    @Test
    public void typeIntoAmazonFieldsAndClearAccessKey_ButtonShouldNotBeEnabled() {
        onView(withId(R.id.radio_amazon)).perform(click());
        onView(withId(R.id.onboarding_secret_key)).perform(typeText("a"));
        onView(withId(R.id.onboarding_access_key)).perform(typeText("a"));
        onView(withId(R.id.onboarding_access_key)).perform(clearText());
        onView(withId(R.id.get_started_btn)).check(matches(not(isClickable())));
    }

    @Test
    public void typeIntoAmazonFieldsAndClearSecretKey_ButtonShouldNotBeEnabled() {
        onView(withId(R.id.radio_amazon)).perform(click());
        onView(withId(R.id.onboarding_secret_key)).perform(typeText("a"));
        onView(withId(R.id.onboarding_access_key)).perform(typeText("a"));
        onView(withId(R.id.onboarding_secret_key)).perform(clearText());
        onView(withId(R.id.get_started_btn)).check(matches(not(isClickable())));
    }

    @Test
    public void getStarted_None() throws InterruptedException {
        onView(withId(R.id.get_started_btn)).perform(click());
        addDelay();
        onView(withId(R.id.bottom_nav_bar)).check(matches(isDisplayed()));
    }

    @After
    public void cleanup() {
        Preferences.saveOnboarding(false, mainActivityRule.getActivity());
    }
}
