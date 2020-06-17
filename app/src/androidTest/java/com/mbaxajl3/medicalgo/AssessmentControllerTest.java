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
public class AssessmentControllerTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);
    private Context context;
    private Algorithm algorithm;
    private AssessmentController assessmentController;
    private JSONController jsonController;
    private Assessment assessment;

    @Before
    public void setUp() {
        context = activityRule.getActivity();
        jsonController = Factory.getJSONController();
        jsonController.getCategories();
        algorithm = jsonController.getAlgorithmById(0);

        assessmentController = Factory.getAssessmentController();
        Preferences.setList("assessment", "assessment", null, context);

        skipIntro(context);
    }

    @Test
    public void createAssessment_ListSizeIsEqualTo1_EmptyList() {
        assessmentController.createAssessment(algorithm);
        setStepsTaken();
        assertThat(assessmentController.getAssessmentList().size()).isEqualTo(1);
    }

    @Test
    public void createAssessment_ListSizeIsEqualTo2() {
        Preferences.setList("assessment", "assessment", null, context);

        assessmentController.createAssessment(algorithm);
        setStepsTaken();
        assessmentController.createAssessment(algorithm);
        setStepsTaken();
        assertThat(assessmentController.getAssessmentList().size()).isEqualTo(2);
    }

    @Test
    public void getLastAssessment_isEmpty() {
        Preferences.setList("assessment", "assessment", null, context);
        assertThat(assessmentController.getLastAssessment()).isNull();
    }

    @Test
    public void getLastAssessment_Correct() {
        assessmentController.createAssessment(algorithm);
        setStepsTaken();
        assertThat(assessmentController.getLastAssessment().getAlgorithm().getId()).isEqualTo(algorithm.getId());
    }

    @Test
    public void saveAssessment() {
        Preferences.setList("assessment", "assessment", null, context);

        assessmentController.createAssessment(algorithm);
        List<Pair<Step, Boolean>> stepsTaken = new ArrayList<>();
        stepsTaken.add(new Pair<>(algorithm.getSteps().get(0), true));

        assessmentController.saveAssessment(stepsTaken);
        assertThat(assessmentController.getAssessmentList().size()).isEqualTo(1);
        Preferences.setList("assessment", "assessment", null, context);
    }

    private void setStepsTaken() {
        List<Pair<Step, Boolean>> stepsTaken = new ArrayList<>();
        stepsTaken.add(new Pair<>(algorithm.getSteps().get(0), true));
        assessmentController.saveAssessment(stepsTaken);
    }

    @After
    public void cleanup() {
        Preferences.saveOnboarding(false, activityRule.getActivity());
    }
}
