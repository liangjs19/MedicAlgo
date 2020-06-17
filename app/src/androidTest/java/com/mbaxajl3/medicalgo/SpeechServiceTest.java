package com.mbaxajl3.medicalgo;

import android.Manifest;
import android.content.Context;

import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.android.material.textview.MaterialTextView;
import com.mbaxajl3.medicalgo.models.Entity;
import com.mbaxajl3.medicalgo.models.Pair;
import com.mbaxajl3.medicalgo.voice.googlecloud.GoogleCloudRecognizer;

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
import static com.mbaxajl3.medicalgo.TestUtils.addDelay;
import static com.mbaxajl3.medicalgo.TestUtils.clickChildViewWithTag;
import static com.mbaxajl3.medicalgo.TestUtils.skipIntro;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SpeechServiceTest {
    private SpeechService speechService;
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO);
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);
    private Context context;

    @Before
    public void setUp() {
        speechService = Factory.getSpeechService();
        context = activityRule.getActivity();
        Preferences.saveHotword(false, context);
        skipIntro(context);
    }

    @Test
    public void startRecording_HotwordOff_Correct() {
        assertThat(speechService.isListeningToHotword()).isFalse();
    }

    @Test
    public void voiceAction_NavigateToFavorites() throws InterruptedException {
        String intent = "navigate";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("favorites"));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        speechService.voiceActions(pair);
        addDelay();
        assertThat(Fragments.getCurrentFragment()).isEqualTo(Fragments.FavouritesFragment);
        assertThat(speechService.voiceActions(pair)).isTrue();
    }

    @Test
    public void voiceAction_NavigateToHome() throws InterruptedException {
        String intent = "navigate";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("home"));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        speechService.voiceActions(pair);
        addDelay();
        assertThat(speechService.voiceActions(pair)).isTrue();
    }

    @Test
    public void voiceAction_launchImageAndGoBack() throws InterruptedException {
        onView(withId(R.id.categories_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.algorithms_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        addDelay();
        String intent = "navigate";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("image"));
        Pair<String, List<Entity>> imagePair = new Pair<>(intent, entities);
        assertThat(speechService.voiceActions(imagePair)).isTrue();
        addDelay();
        assertThat(speechService.voiceActions(imagePair)).isFalse();

        intent = "return";
        entities.clear();
        Pair<String, List<Entity>> returnPair = new Pair<>(intent, entities);
        assertThat(speechService.voiceActions(returnPair)).isTrue();
        addDelay();
        assertThat(Activities.getCurrentActivity() == Activities.ImageViewActivity).isFalse();
    }


    @Test
    public void voiceAction_SearchForS() throws InterruptedException {
        String intent = "search";
        List<Entity> entities = new ArrayList<>();
        List<String> values = new ArrayList<>();
        values.add("s");
        entities.add(new Entity("entity", values));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        assertThat(speechService.voiceActions(pair)).isTrue();
        addDelay();
        assertThat(Fragments.getCurrentFragment()).isEqualTo(Fragments.SearchFragment);
    }

    @Test
    public void voiceAction_NavigateToAssessmentCenter() throws InterruptedException {
        String intent = "navigate";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("assessments"));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        speechService.voiceActions(pair);
        addDelay();
        assertThat(Fragments.getCurrentFragment()).isEqualTo(Fragments.AssessmentFragment);
        assertThat(speechService.voiceActions(pair)).isTrue();
    }

    @Test
    public void voiceAction_InvalidIntent_ReturnFalse() throws InterruptedException {
        String intent = "hello";
        Pair<String, List<Entity>> pair = new Pair<>(intent, null);
        speechService.voiceActions(pair);
        assertThat(speechService.voiceActions(pair)).isFalse();
    }

    @UiThreadTest
    @Test
    public void correctTranscript() {
        String utterance = "open favourites";
        speechService.handleRecognizedVoice(utterance);

        MaterialTextView view = activityRule.getActivity().findViewById(R.id.transcript);
        assertThat(view.getText()).isEqualTo(utterance);
    }

    @Test
    public void voiceRecognizer_isNull() {
        speechService.startRecording();
        assertThat(speechService.getVoiceRecognizer()).isEqualTo(null);
    }

    @Test
    public void voiceRecognizer_isGoogle() {
        Preferences.saveEnginePref("google_cloud", context);
        speechService.startRecording();
        assertThat(speechService.getVoiceRecognizer()).isInstanceOf(GoogleCloudRecognizer.class);
        speechService.restart();
        assertThat(speechService.getVoiceRecognizer()).isInstanceOf(GoogleCloudRecognizer.class);
    }

    @Test
    public void voiceAction_launchCategory() throws InterruptedException {
        String intent = "navigate";
        List<Entity> entities = new ArrayList<>();
        List<String> values = new ArrayList<>();
        values.add("Airway Management");
        entities.add(new Entity("a", values));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        assertThat(speechService.voiceActions(pair)).isTrue();
        Thread.sleep(2000);
        assertThat(Fragments.getCurrentFragment() == Fragments.AlgorithmsFragment).isTrue();
    }

    //
    @Test
    public void voiceAction_launchCategory_Incorrect() throws InterruptedException {
        String intent = "navigate";
        List<Entity> entities = new ArrayList<>();
        List<String> values = new ArrayList<>();
        values.add("Airway Managemet");
        entities.add(new Entity("a", values));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        assertThat(speechService.voiceActions(pair)).isFalse();
    }

    //
    @Test
    public void voiceAction_launchAlgorithm() throws InterruptedException {
        String intent = "navigate";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("Laryngectomy"));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        assertThat(speechService.voiceActions(pair)).isTrue();
        Thread.sleep(2000);
        assertThat(Fragments.getCurrentFragment() == Fragments.AlgorithmFragment).isTrue();
    }

    //
    @Test
    public void voiceAction_launchAlgorithm_Incorrect() {
        String intent = "navigate";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("aa"));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        assertThat(speechService.voiceActions(pair)).isFalse();
    }

    @Test
    public void voiceAction_NavigateToAlgorithm() throws InterruptedException {
        String intent = "navigate";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("home"));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        speechService.voiceActions(pair);
        addDelay();
        assertThat(speechService.voiceActions(pair)).isTrue();
    }

    @After
    public void cleanup() {
        Preferences.saveOnboarding(false, activityRule.getActivity());
    }
    //doesnt run
//    @UiThreadTest
//    @Test
//    public void voiceAction_ReturnToPreviousFragment() throws InterruptedException {
//        onView(withId(R.id.nav_bar_item_favourites)).perform(click());
//        Thread.sleep(1000);
//        String intent = "return";
//        List<Entity> entities = new ArrayList<>();
//        Pair<String, List<Entity>> returnPair = new Pair<>(intent, entities);
//        assertThat(speechService.voiceActions(returnPair));
//        Thread.sleep(1000);
//        assertThat(Fragments.getCurrentFragment() == Fragments.CategoriesFragment).isTrue();
//    }

//     problems in jacoco
//    @UiThreadTest
//    @Test
//    public void speak_Correct() {
//        Preferences.set("tts", "tts", "true", context);
//        assertThat(speechService.speak("hi", true)).isNotEqualTo(-1);
//    }
//
//    @UiThreadTest
//    @Test
//    public void speak_ttsDisabled() throws InterruptedException {
//        Preferences.set("tts", "tts", "false", context);
//        assertThat(speechService.speak("hi", true)).isNotEqualTo(-1);
//    }

//    @UiThreadTest
//    @Test
//    public void voiceRecognizer_hotwordOnAndOff() throws InterruptedException {
//        Preferences.saveEnginePref("google_cloud", context);
//        Preferences.saveHotword(true, context);
//        Thread.sleep(1000);
//        speechService.startRecording();
//        assertThat(speechService.getVoiceRecognizer()).isInstanceOf(SphinxRecognizer.class);
//        speechService.restart();
//        assertThat(speechService.getVoiceRecognizer()).isInstanceOf(SphinxRecognizer.class);
//        speechService.setListeningToHotword(false);
//        assertThat(speechService.isListeningToHotword()).isFalse();
//        speechService.restart();
//        assertThat(speechService.getVoiceRecognizer()).isInstanceOf(GoogleCloudRecognizer.class);
//    }

//    @Test
//    public void voiceRecognizer_isAmazon() {
//        Preferences.saveEnginePref("amazon", context);
//        Preferences.saveHotword(false, context);
//        speechService.startRecording();
//        speechService.stopRecording();
////        assertThat(speechService.getVoiceRecognizer()).isInstanceOf(AmazonTranscribe.class);
//    }
//
//    @Test
//    public void voiceRecognizer_isAmazon_RetainObject() {
//        Preferences.saveEnginePref("amazon", context);
//        speechService.startRecording();
//        assertThat(speechService.getVoiceRecognizer()).isInstanceOf(AmazonTranscribe.class);
//        speechService.restart();
//        assertThat(speechService.getVoiceRecognizer()).isInstanceOf(AmazonTranscribe.class);
//    }
@Test
public void voiceAction_StepAction_Restart() throws InterruptedException {
    onView(withId(R.id.categories_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
    onView(withId(R.id.algorithms_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
    onView(withId(R.id.algorithm_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithTag("button1")));
    onView(withId(R.id.algorithm_view)).perform(RecyclerViewActions.actionOnItemAtPosition(1, clickChildViewWithTag("button2")));
    addDelay();
    String intent = "step_action";
    List<Entity> entities = new ArrayList<>();
    entities.add(new Entity("restart"));
    Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
    assertThat(speechService.voiceActions(pair)).isTrue();
    addDelay();
    onView(withId(R.id.algorithm_view)).check(matches(hasChildCount(1)));
}

    @Test
    public void voiceAction_previousStep() throws InterruptedException {
        onView(withId(R.id.categories_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.algorithms_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.algorithm_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithTag("button1")));
        onView(withId(R.id.algorithm_view)).perform(RecyclerViewActions.actionOnItemAtPosition(1, clickChildViewWithTag("button2")));
        String intent = "return";
        List<Entity> entities = new ArrayList<>();
        Pair<String, List<Entity>> returnPair = new Pair<>(intent, entities);
        assertThat(speechService.voiceActions(returnPair)).isTrue();
        addDelay();
        onView(withId(R.id.algorithm_view)).check(matches(hasChildCount(2)));
    }

    @Test
    public void voiceAction_StepAction_Repeat() {
        onView(withId(R.id.categories_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.algorithms_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        String intent = "step_action";
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("repeat"));
        Pair<String, List<Entity>> pair = new Pair<>(intent, entities);
        assertThat(speechService.voiceActions(pair)).isTrue();
    }
}
