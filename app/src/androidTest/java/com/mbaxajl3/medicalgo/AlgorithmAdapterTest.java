package com.mbaxajl3.medicalgo;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.Entity;
import com.mbaxajl3.medicalgo.models.Option;
import com.mbaxajl3.medicalgo.models.Pair;
import com.mbaxajl3.medicalgo.ui.algorithm.AlgorithmAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;
import static com.mbaxajl3.medicalgo.TestUtils.clickChildViewWithTag;
import static com.mbaxajl3.medicalgo.TestUtils.skipIntro;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AlgorithmAdapterTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);
    private Algorithm algorithm;
    private AlgorithmAdapter algorithmAdapter;

    @Before
    public void setUp() {
        JSONController jsonController = Factory.getJSONController();
        jsonController.getCategories();
        algorithm = jsonController.getAlgorithmById(0);
        algorithmAdapter = new AlgorithmAdapter(activityRule.getActivity(), null, algorithm);

        skipIntro(activityRule.getActivity());
    }

    // only first item is shown at the start
    @Test
    public void getItemCount_CorrectSize() {
        assertThat(algorithmAdapter.getItemCount()).isEqualTo(1);
    }

    //"performing cpr" should go to stepID 2
    @Test
    public void findStepByIntentAndEntities_GoToStepId2_Correct() {
        Pair<String, List<Entity>> pair = getCPRPair();
        algorithmAdapter.findStepByIntentAndEntities(pair);
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(2);
    }

    //"remove stoma cover" should go to stepID 5
    @Test
    public void findStepByIntentAndEntities_GoToStepId5_Correct() {
        String intent = "medical_action";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("stoma cover"));
        entities.add(new Entity("remove"));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        algorithmAdapter.findStepByIntentAndEntities(pair);
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(5);
    }

    // "stoma cover" should not go anywhere
    @Test
    public void findStepByIntentAndEntities_Incorrect() {
        String intent = "medical_action";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("stoma cover"));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        algorithmAdapter.findStepByIntentAndEntities(pair);
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(0);
    }

    // get the last step of "performing cpr" which occurs in step 15,
    // as it the algorithm has progressed beyond that
    @Test
    public void findStepByIntentAndEntities_LastRelevantStep() {
        String intent = "medical_action";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("stoma"));
        entities.add(new Entity("lma"));
        entities.add(new Entity("apply"));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        algorithmAdapter.findStepByIntentAndEntities(pair);
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(17);
        algorithmAdapter.findStepByIntentAndEntities(getCPRPair());
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(15);
    }

    @Test
    public void resetSteps_ToStart_Correct() {
        Pair<String, List<Entity>> pair = getCPRPair();
        algorithmAdapter.findStepByIntentAndEntities(pair);
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(2);
        algorithmAdapter.resetSteps();
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(0);
    }

    @Test
    public void resetSteps_To1_Correct() {
        algorithmAdapter.findStepByIntentAndEntities(getCPRPair());
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(2);
        algorithmAdapter.resetSteps(1);
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(1);
    }

    @Test
    public void previousStep_Correct() {
        algorithmAdapter.findStepByIntentAndEntities(getCPRPair());
        algorithmAdapter.previousStep();
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(1);
    }

    @Test
    public void previousStep_From0_Correct() {
        algorithmAdapter.previousStep();
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(0);
    }

    @Test
    public void resetSteps_ToNegative_NoAction() {
        Pair<String, List<Entity>> pair = getCPRPair();
        algorithmAdapter.findStepByIntentAndEntities(pair);
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(2);
        algorithmAdapter.resetSteps(-1);
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(2);
    }

    //performing cpr
    @Test
    public void assessment_Started_DirectlyJumpToStep2_InCorrect() {
        algorithmAdapter.startAssessment();
        algorithmAdapter.findStepByIntentAndEntities(getCPRPair());
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(0);
    }

    // patient is not breathing
    @Test
    public void assessment_Started_JumpToStep2_Correct() {
        algorithmAdapter.startAssessment();
        algorithmAdapter.findStepByIntentAndEntities(getPatientNotBreathingPair());
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(2);
    }

    // repeating the step is valid
    @Test
    public void assessment_RepeatStep_Correct() {
        algorithmAdapter.startAssessment();
        assertThat(algorithmAdapter.findStepByIntentAndEntities(getPatientNotBreathingPair())).isFalse();
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(2);
        assertThat(algorithmAdapter.findStepByIntentAndEntities(getPatientNotBreathingPair())).isTrue();
        assertThat(algorithmAdapter.findStepByIntentAndEntities(getCPRPair())).isFalse();
    }

    // anything said after the last step is incorrect
    @Test
    public void assessment_LastStep_Correct() {
        algorithmAdapter.startAssessment();
        assertThat(algorithmAdapter.findStepByIntentAndEntities(getPatientNotBreathingPair())).isFalse();
        algorithmAdapter.findStepByIntentAndEntities(getAssessLaryngectomyPatencyPair());
        algorithmAdapter.findStepByIntentAndEntities(getPassedSuctionCatheterPair());
        assertThat(algorithmAdapter.findStepByIntentAndEntities(getCPRPair())).isTrue();
    }

    // optional step should be ignored
    @Test
    public void assessment_OptionalStep_Correct() {
        algorithmAdapter.startAssessment();
        assertThat(algorithmAdapter.findStepByIntentAndEntities(getPatientNotBreathingPair())).isFalse();
        assertThat(algorithmAdapter.findStepByIntentAndEntities(getAssessLaryngectomyPatencyPair())).isFalse();
        algorithmAdapter.findStepByIntentAndEntities(getPassedSuctionCatheterPair());
        assertThat(algorithmAdapter.getLastStep().getId()).isEqualTo(9);
    }

    @Test
    public void stopAssessment_ReturnsTrue() {
        algorithmAdapter.stopAssessment();
        assertThat(algorithmAdapter.inAssessment()).isFalse();
    }

    @Test
    public void nextStep_Null() {
        assertThat(algorithmAdapter.nextStep(null)).isFalse();
    }

    @Test
    public void nextStep_ReturnsTrue() {
        Option option = algorithmAdapter.getLastStep().getOptions().get(0);
        assertThat(algorithmAdapter.nextStep(option)).isTrue();
    }

    @Test
    public void clickOnNextButtonOfFirstStep_ShouldHave2Steps() {
        onView(withId(R.id.categories_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.algorithms_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.algorithm_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithTag("button1")));
        onView(withId(R.id.algorithm_view)).check(matches(hasChildCount(2)));
    }

    @Test
    public void clickOnNextButtonOfFirstStepAfter3Steps_ShouldHave2Steps() throws InterruptedException {
        onView(withId(R.id.categories_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.algorithms_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.algorithm_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithTag("button1")));
        onView(withId(R.id.algorithm_view)).perform(RecyclerViewActions.actionOnItemAtPosition(1, clickChildViewWithTag("button2")));
        onView(withId(R.id.algorithm_view)).check(matches(hasChildCount(3)));
        onView(withId(R.id.algorithm_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithTag("button1")));
        TestUtils.addDelay();
        onView(withId(R.id.algorithm_view)).check(matches(hasChildCount(2)));
    }

    private Pair<String, List<Entity>> getCPRPair() {
        String intent = "medical_action";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("perform"));
        entities.add(new Entity("cpr"));
        return new Pair<>(intent, entities);
    }

    private Pair<String, List<Entity>> getPatientNotBreathingPair() {
        String intent = "negate_patient_symptom";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("pulse"));
        return new Pair<>(intent, entities);
    }

    private Pair<String, List<Entity>> getAssessLaryngectomyPatencyPair() {
        String intent = "medical_action";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("assess"));
        entities.add(new Entity("stoma"));
        return new Pair<>(intent, entities);
    }

    private Pair<String, List<Entity>> getPassedSuctionCatheterPair() {
        String intent = "medical_action";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("insert"));
        entities.add(new Entity("suction catheter"));
        return new Pair<>(intent, entities);
    }

    private Pair<String, List<Entity>> getRemoveStomaCoverPair() {
        String intent = "medical_action";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("remove"));
        entities.add(new Entity("stoma cover"));
        return new Pair<>(intent, entities);
    }

    @After
    public void cleanup() {
        Preferences.saveOnboarding(false, activityRule.getActivity());
    }
}
