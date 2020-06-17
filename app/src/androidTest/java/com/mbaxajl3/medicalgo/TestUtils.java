package com.mbaxajl3.medicalgo;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;

import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class TestUtils {

    public static void addDelay(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    public static void addDelay() throws InterruptedException {
        Thread.sleep(2000);
    }

    public static ViewAction clickChildViewWithTag(final String tag) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewWithTag(tag);
                v.performClick();
            }
        };
    }

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

    public static void skipIntro(Context context) {
        if (!Preferences.getOnboarding(context)) {
            onView(withId(R.id.pager_introduction)).perform(swipeLeft());
            onView(withId(R.id.get_started_btn)).perform(ViewActions.click());
        }
    }
}
