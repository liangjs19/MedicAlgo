package com.mbaxajl3.medicalgo;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.mbaxajl3.medicalgo.controllers.AssessmentController;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.Assessment;
import com.mbaxajl3.medicalgo.models.Pair;
import com.mbaxajl3.medicalgo.models.Step;
import com.mbaxajl3.medicalgo.ui.algorithm.AlgorithmAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.mbaxajl3.medicalgo.TestUtils.skipIntro;

@RunWith(AndroidJUnit4.class)
public class AlgorithmAssessmentAdapterTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);
    private Assessment assessment;
    private AlgorithmAdapter algorithmAdapter;
    private AssessmentController assessmentController;
    private Context context;
    private JSONController jsonController;

    @Before
    public void setUp() {
        context = activityRule.getActivity();
        jsonController = Factory.getJSONController();
        jsonController.getCategories();
        Preferences.setList("assessment", "assessment", null, context);
        Algorithm algorithm = jsonController.getAlgorithmById(0);
        assessment = new Assessment(0, algorithm);

        List<Pair<Step, Boolean>> stepsTaken = new ArrayList<>();
        stepsTaken.add(new Pair<>(algorithm.getSteps().get(0), true));

        assessment.setStepsTaken(stepsTaken);
        algorithmAdapter = new AlgorithmAdapter(context, null, assessment);
        assessmentController = Factory.getAssessmentController();

        List<Assessment> assessmentList = new ArrayList<>();
        assessmentList.add(assessment);
        assessmentController.setAssessmentList(assessmentList);

        skipIntro(context);
    }

    @Test
    public void getItemCount_ShouldEqual1() {
        assertThat(algorithmAdapter.getItemCount()).isEqualTo(1);
    }

    @Test
    public void getAssessment_ShouldBeNotNull() {
        assertThat(algorithmAdapter.getAssessment()).isNotNull();
    }

    @After
    public void cleanup() {
        Preferences.saveOnboarding(false, activityRule.getActivity());
    }
}
