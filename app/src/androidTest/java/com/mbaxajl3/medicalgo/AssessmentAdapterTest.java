package com.mbaxajl3.medicalgo;

import android.content.Context;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.mbaxajl3.medicalgo.controllers.AssessmentController;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.Assessment;
import com.mbaxajl3.medicalgo.models.Pair;
import com.mbaxajl3.medicalgo.models.Step;
import com.mbaxajl3.medicalgo.ui.assessment.AssessmentAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;
import static com.mbaxajl3.medicalgo.TestUtils.addDelay;
import static com.mbaxajl3.medicalgo.TestUtils.skipIntro;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AssessmentAdapterTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);
    private Context context;
    private Algorithm algorithm;
    private AssessmentController assessmentController;
    private JSONController jsonController;
    private Assessment assessment;
    private AssessmentAdapter assessmentAdapter;

    @Before
    public void setUp() throws InterruptedException {
        context = activityRule.getActivity();
        jsonController = Factory.getJSONController();
        jsonController.getCategories();
        algorithm = jsonController.getAlgorithmById(0);

        assessmentController = Factory.getAssessmentController();
        Preferences.setList("assessment", "assessment", null, context);
        addDelay();
        skipIntro(context);
        assessmentController.createAssessment(algorithm);

        List<Pair<Step, Boolean>> stepsTaken = new ArrayList<>();
        stepsTaken.add(new Pair<>(algorithm.getSteps().get(0), true));
        assessmentController.saveAssessment(stepsTaken);
        assessmentAdapter = new AssessmentAdapter(context, assessmentController.getAssessmentList());
    }

    @Test
    public void numberOfAssessments_ShouldEqualTo1() {
        assertThat(assessmentAdapter.getItemCount()).isEqualTo(1);
    }

    @Test
    public void clicking() {
        onView(withId(R.id.nav_bar_item_assessment)).perform(click());
        onView(withId(R.id.assessment_view)).check(matches(hasChildCount(1)));
    }

    @Test
    public void clickOnItem() throws InterruptedException {
        onView(withId(R.id.nav_bar_item_assessment)).perform(click());
        onView(withId(R.id.assessment_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        addDelay();
        assertThat(Fragments.getCurrentFragment()).isEqualTo(Fragments.AlgorithmFragment);
    }

    @After
    public void cleanup() {
        Preferences.saveOnboarding(false, activityRule.getActivity());
    }

}
