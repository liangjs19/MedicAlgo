package com.mbaxajl3.medicalgo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.controllers.NLUController;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.Category;
import com.mbaxajl3.medicalgo.models.Entity;
import com.mbaxajl3.medicalgo.models.Option;
import com.mbaxajl3.medicalgo.models.Pair;
import com.mbaxajl3.medicalgo.ui.algorithm.AlgorithmAdapter;
import com.mbaxajl3.medicalgo.ui.algorithm.AlgorithmFragment;
import com.mbaxajl3.medicalgo.ui.algorithms.AlgorithmsFragment;
import com.mbaxajl3.medicalgo.ui.assessment.AssessmentFragment;
import com.mbaxajl3.medicalgo.ui.categories.CategoriesFragment;
import com.mbaxajl3.medicalgo.ui.favourites.FavouritesFragment;
import com.mbaxajl3.medicalgo.ui.search.SearchFragment;
import com.mbaxajl3.medicalgo.voice.SphinxRecognizer;
import com.mbaxajl3.medicalgo.voice.VoiceRecognizer;
import com.mbaxajl3.medicalgo.voice.aws.AmazonTranscribe;
import com.mbaxajl3.medicalgo.voice.googlecloud.GoogleCloudRecognizer;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.mbaxajl3.medicalgo.Constants.ALGORITHM_FRAGMENT;
import static com.mbaxajl3.medicalgo.Constants.AMAZON;
import static com.mbaxajl3.medicalgo.Constants.ASSESSMENTS;
import static com.mbaxajl3.medicalgo.Constants.ASSESSMENT_FRAGMENT;
import static com.mbaxajl3.medicalgo.Constants.CATEGORIES_FRAGMENT;
import static com.mbaxajl3.medicalgo.Constants.FAVOURITES;
import static com.mbaxajl3.medicalgo.Constants.GOOGLE_CLOUD;
import static com.mbaxajl3.medicalgo.Constants.HOME;
import static com.mbaxajl3.medicalgo.Constants.MEDICAL_ACTION;
import static com.mbaxajl3.medicalgo.Constants.NAVIGATE;
import static com.mbaxajl3.medicalgo.Constants.NEGATE_MEDICAL_ACTION;
import static com.mbaxajl3.medicalgo.Constants.NEGATE_PATIENT_SYMPTOM;
import static com.mbaxajl3.medicalgo.Constants.NONE;
import static com.mbaxajl3.medicalgo.Constants.PATIENT_SYMPTOM;
import static com.mbaxajl3.medicalgo.Constants.QUESTION;
import static com.mbaxajl3.medicalgo.Constants.REPEAT;
import static com.mbaxajl3.medicalgo.Constants.RESTART;
import static com.mbaxajl3.medicalgo.Constants.RETURN;
import static com.mbaxajl3.medicalgo.Constants.SEARCH;
import static com.mbaxajl3.medicalgo.Constants.SEARCH_FRAGMENT;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_ACCESS_KEY;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_API_KEY;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_ENGINE;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_LANGUAGE;
import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_SECRET_KEY;
import static com.mbaxajl3.medicalgo.Constants.STEP_ACTION;

public class SpeechService {
    private final static String TAG = "SpeechService";
    private Context context;
    private VoiceRecognizer voiceRecognizer;
    private AlgorithmFragment algorithmFragment;
    private static TextToSpeech textToSpeech;
    private CountDownTimer hotwordTimer;
    private boolean isListeningToHotword;
    private CountDownTimer silenceTimer;
    private JSONController jsonController;
    private View progressOverlay;
    private VariableChangeListener responseStatusListener;
    private Context imageViewerContext;
    private MaterialTextView transcriptTextView;
    private boolean isRecording = false;

    public SpeechService(Context context) {
        this.context = context;
        jsonController = Factory.getJSONController();
        progressOverlay = ((Activity) context).findViewById(R.id.progress_overlay);
        transcriptTextView = ((Activity) context).findViewById(R.id.transcript);
        isListeningToHotword = Preferences.getHotword(context);
        initTTS();
    }

    // initialise voice recognition engines
    private void initialise() {
        if (isNone()) {
            return;
        }

        //start pocketsphinx for hotword detection if hotword preference is true
        if (Preferences.getHotword(context) && isListeningToHotword) {
            if (!(voiceRecognizer instanceof SphinxRecognizer)) {
                cleanup();
                voiceRecognizer = new SphinxRecognizer(context);
            }
            ((MainActivity) context).showSnackbar(context.getString(R.string.hotword_activated), Snackbar.LENGTH_INDEFINITE);
            return;
        }

        ((MainActivity) context).dismissSnackbar();
        if (isGoogle()) {
            if (voiceRecognizer instanceof GoogleCloudRecognizer) {
                ((GoogleCloudRecognizer) voiceRecognizer).setApiKey(getApiKey());
            } else {
                cleanup();
                voiceRecognizer = new GoogleCloudRecognizer(context, getSpeechLanguage(), getApiKey(), jsonController.getWords());
            }
        } else if (isAmazon()) {
            if (voiceRecognizer instanceof AmazonTranscribe) {
                ((AmazonTranscribe) voiceRecognizer).setKeys(getAccessKey(), getSecretKey());
            } else {
                cleanup();
                voiceRecognizer = new AmazonTranscribe(context, getAccessKey(), getSecretKey());
            }
        }
    }

    private static String lastSpokenText = "";

    public int speak(String text, boolean replace) {
        if (!Preferences.getTts(context)) {
            return -1;
        }

        if (Preferences.getHotword(context)) {
            isListeningToHotword = true;
            restart();
        }

        lastSpokenText = text != null ? text : "";

        if (textToSpeech == null || text == null) {
            return -1;
        }

        return textToSpeech.speak(text, (replace ? QUEUE_FLUSH : QUEUE_ADD), null, UUID.randomUUID().toString());
    }

    public int speak(boolean replace) {
        return speak(lastSpokenText, replace);
    }

    public static void stopTTS() {
        if (textToSpeech == null) {
            return;
        }
        textToSpeech.stop();
    }

    public static void cleanTTS() {
        if (textToSpeech == null) {
            return;
        }
        textToSpeech.stop();
        textToSpeech.shutdown();
    }

    private void initTTS() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int ttsLang = textToSpeech.setLanguage(Locale.UK);

                            if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                                    || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("TTS", "The Language is not supported!");
                            } else {
                                Log.i("TTS", "Language Supported.");
                            }
                            Log.i("TTS", "Initialization success.");
                        } else {
                            // error
                            Log.e("TTS", "Initialization failure.");
                        }
                    }
                });
            }
        });
    }

    // hotword timer lasting 10 secs, in which it will activate hotword detection
    private void startHotwordTimer() {
        cancelTimer(hotwordTimer);

        if (!isRecording) {
            return;
        }

        if (!Preferences.getHotword(context)) {
            isListeningToHotword = false;
            return;
        }

        hotwordTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.v("Hotword Timer", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                isListeningToHotword = true;
                updateTranscriptTextView("");
                restart();
            }
        }.start();
    }

    // start hotword detection
    public void activateHotword() {
        if (Preferences.getHotword(context)) {
            cancelTimer(hotwordTimer);
            isListeningToHotword = true;
        } else {
            isListeningToHotword = false;
        }

        restart();
    }

    private void cancelTimer(CountDownTimer timer) {
        if (timer == null) {
            return;
        }
        timer.cancel();
    }

    public void handleRecognizedVoice(String s) {
        if (s == null) {
            return;
        }

        // update transcript bar
        updateTranscriptTextView(s);

        Log.v(TAG + " handleRecognizedVoice", s);
        // listen to OK Trachy if hotword detection is activated
        if (Preferences.getHotword(context) && isListeningToHotword) {
            if (Util.containsIgnoreCase(s, context.getString(R.string.hotword))) {
                isListeningToHotword = false;
                cancelTimer(hotwordTimer);
                stopTTS();
                restart();
            }
            return;
        }

        startSilenceTimer(s);
    }

    // only handle transcript when no words are transcribed after 1 seconds to prevent
    // unnecessary requests
    private void startSilenceTimer(String s) {
        cancelTimer(silenceTimer);

        Log.v(TAG, "started silence timer");
        silenceTimer = new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.v("Silence Timer", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.v(TAG, "finish silence timer");

                handleNLU(s);

                // refresh amazon transcribe transcript to just contain the said words recently
                if (voiceRecognizer instanceof AmazonTranscribe)
                    ((AmazonTranscribe) voiceRecognizer).clearTranscript();

                updateTranscriptTextView("");
            }
        }.start();
    }

    public void cancelAllTimers() {
        cancelTimer(hotwordTimer);
        cancelTimer(silenceTimer);
    }

    // hide transcript view when there is not text to show
    public void updateTranscriptTextView(String s) {
        if (transcriptTextView == null) {
            return;
        }
        transcriptTextView.setVisibility(View.GONE);
        if (Preferences.getTranscript(context) && !s.isEmpty()) {
            transcriptTextView.setText(s);
            transcriptTextView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isGoogle() {
        return getEngine().equalsIgnoreCase(GOOGLE_CLOUD);
    }

    private boolean isAmazon() {
        return getEngine().equalsIgnoreCase(AMAZON);
    }

    private boolean isNone() {
        return getEngine().equalsIgnoreCase(NONE);
    }

    private boolean isCategories() {
        return Fragments.getCurrentFragment() == Fragments.CategoriesFragment;
    }

    private boolean isAlgorithms() {
        return Fragments.getCurrentFragment() == Fragments.AlgorithmsFragment;
    }

    private boolean isAlgorithm() {
        return Fragments.getCurrentFragment() == Fragments.AlgorithmFragment;
    }

    private boolean isSearch() {
        return Fragments.getCurrentFragment() == Fragments.SearchFragment;
    }

    private boolean isAssessment() {
        return Fragments.getCurrentFragment() == Fragments.AssessmentFragment;
    }

    private boolean isFavourites() {
        return Fragments.getCurrentFragment() == Fragments.FavouritesFragment;
    }

    private void handleNLU(String s) {
        startHotwordTimer();

        // TODO change this to use NLU
        // handle algorithm step choices
        if (isAlgorithm() && Activities.getCurrentActivity() == Activities.MainActivity) {
            Log.v(TAG, "AlgorithmFragment");

            FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
            Fragment fragment = manager.findFragmentById(R.id.nav_host_fragment);
            algorithmFragment = (AlgorithmFragment) fragment;

            if (algorithmFragment == null) {
                Log.v(TAG, "return");
                return;
            }

            AlgorithmAdapter adapter = algorithmFragment.getAdapter();

            if (adapter.getLastStep().getOptions() != null) {
                for (Option option : adapter.getLastStep().getOptions()) {
                    if (s.replace(".", "").trim().equalsIgnoreCase(option.getOption())) {
                        adapter.nextStep(option);
                        return;
                    }
                }
            }
        }

        // else, pass to LUIS.ai to handle
        Util.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        NLUController.get(s, new Callback() {
            @Override
            public void onSuccess(Object value) {
                voiceActions((Pair<String, List<Entity>>) value);
                Util.animateView(progressOverlay, View.GONE, 0, 200);
            }

            @Override
            public void onError(String result) {
                //TODO snackbar
                Log.e(TAG, result);
                Util.animateView(progressOverlay, View.GONE, 0, 200);
            }
        });
    }

    public boolean voiceActions(Pair<String, List<Entity>> pair) {
        String intent = pair.first;
        List<Entity> entities = pair.second;
//        Log.v("intent ", intent);
//        for (Entity e : entities) {
//            Log.v("entities", e.getType());
//            if (e.getValue() != null) {
//                Log.v("value for" + e.getType(), e.getLastValue());
//            }
//        }
        FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.nav_host_fragment);
        if (fragment instanceof AlgorithmFragment)
            algorithmFragment = (AlgorithmFragment) fragment;

        Entity e;
        if (Util.containsIgnoreCase(intent, NAVIGATE)) {

            // launch category/algorithm only when not viewing an algorithm.
            if (Fragments.getCurrentFragment() != Fragments.AlgorithmFragment) {
                for (Entity entity : entities) {
                    if (launchCategory(entity.getLastValue()) || launchAlgorithm(entity.getType())) {
                        return true;
                    }
                }
            }

            if ((e = getEntity(entities, HOME)) != null) {
                launchCategories();
                return true;
            } else if (Fragments.getCurrentFragment() == Fragments.AlgorithmFragment && (e = getEntity(entities, "image")) != null) {
                if (Activities.getCurrentActivity() != Activities.ImageViewActivity) {
                    return launchAlgorithmImage();
                }
                return false;
            } else if ((e = getEntity(entities, FAVOURITES)) != null) {
                launchFavourites();
                return true;
            } else if ((e = getEntity(entities, ASSESSMENTS)) != null) {
                launchAssessmentCenter();
                return true;
            }

        } else if (Util.containsIgnoreCase(intent, RETURN)) {
            if (isAlgorithm()) {
                if (Activities.getCurrentActivity() == Activities.ImageViewActivity) {
                    if (imageViewerContext == null) {
                        return false;
                    }

                    ((ImageViewActvity) imageViewerContext).finish();
                    return true;
                } else {
                    AlgorithmAdapter adapter = algorithmFragment.getAdapter();
                    adapter.previousStep();
                    return true;
                }
            } else {
                speak(context.getString(R.string.return_to_previous_page), true);
                ((MainActivity) context).onBackPressed();
                return true;
            }
        }
//        else if (Util.containsIgnoreCase(intent, "settings")) {
//            //TODO expand this
//            speak("Voice recognition is now disabled", true);
//            Preferences.saveEnginePref("none", context);
//            stopRecording();
//            cancelTimer(hotwordTimer);
//            return true;
//        }
        else if (Util.containsIgnoreCase(intent, STEP_ACTION)) {
            if (isAlgorithm()) {
                if ((e = getEntity(entities, REPEAT)) != null) {
                    speak(true);
                    return true;
                } else if ((e = getEntity(entities, RESTART)) != null) {
                    if (algorithmFragment == null || algorithmFragment.getAdapter() == null)
                        return false;

                    speak(context.getString(R.string.step_start_over), true);
                    algorithmFragment.getAdapter().resetSteps();
                    return true;
                }
            }
        } else if (Util.containsIgnoreCase(intent, SEARCH)) {
            if (entities.isEmpty()) {
                return false;
            }

            e = entities.get(entities.size() - 1);
            speak(context.getString(R.string.search_algorithms_contain) + e.getLastValue(), true);
            launchSearchView(e.getLastValue());
            return true;
        } else if (Util.containsIgnoreCase(intent, new String[]{NEGATE_PATIENT_SYMPTOM, PATIENT_SYMPTOM, MEDICAL_ACTION, NEGATE_MEDICAL_ACTION, QUESTION})) {
            if (isAlgorithm()) {
                AlgorithmAdapter adapter = algorithmFragment.getAdapter();
                if (adapter.getAssessment() == null) {
                    adapter.findStepByIntentAndEntities(pair);
                    return true;
                }
            }
        }
        return false;
    }

    private void launchCategories() {
        if (isCategories()) {
            return;
        }

        CategoriesFragment categoriesFragment = new CategoriesFragment();
        speak(context.getString(R.string.launch_categories), true);

        loadFragment(categoriesFragment, CATEGORIES_FRAGMENT);
    }

    private boolean launchCategory(String category) {
        Category currentCategory = jsonController.getCategoryByName(category);

        if (currentCategory == null) {
            return false;
        }
        //TODO feedback if not found
        AlgorithmsFragment algorithmsFragment = new AlgorithmsFragment();
        Bundle args = new Bundle();

        args.putSerializable("category", currentCategory);
        algorithmsFragment.setArguments(args);
        speak(context.getString(R.string.launching) + category + context.getString(R.string.algorithms), true);
        loadFragment(algorithmsFragment, ALGORITHM_FRAGMENT);
        return true;
    }

    private boolean launchAlgorithm(String algo) {
        Log.v(TAG, "AlgorithmsFragment");
        Algorithm algorithm = jsonController.getAlgorithmByName(algo);

        if (algorithm == null) {
            return false;
        }

        algorithmFragment = new AlgorithmFragment();

        Bundle args = new Bundle();
        args.putSerializable("algorithm", algorithm);
        algorithmFragment.setArguments(args);
        speak(context.getString(R.string.launching) + algorithm + context.getString(R.string.algorithm), true);
        loadFragment(algorithmFragment, ALGORITHM_FRAGMENT);
        return true;
    }

    private boolean launchAlgorithmImage() {
        algorithmFragment = (AlgorithmFragment) ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag("AlgorithmFragment");

        if (algorithmFragment == null) {
            return false;
        }

        speak(context.getString(R.string.launch_full_algo), true);
        algorithmFragment.launchImage();
        return true;
    }

    private void launchSearchView(String searchText) {
        SearchFragment searchFragment;
        if (isSearch()) {
            searchFragment = (SearchFragment) ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag("SearchFragment");

            if (searchFragment != null)
                searchFragment.setSearchText(searchText);
        } else {
            searchFragment = new SearchFragment();
            Bundle args = new Bundle();
            args.putSerializable("searchText", searchText);
            searchFragment.setArguments(args);
            loadFragment(searchFragment, SEARCH_FRAGMENT);
        }
    }

    private void launchFavourites() {
        if (isFavourites()) {
            return;
        }
        loadFragment(new FavouritesFragment(), FAVOURITES);
        speak(context.getString(R.string.launch_fav), true);
    }

    private void launchAssessmentCenter() {
        if (isAssessment()) {
            return;
        }
        loadFragment(new AssessmentFragment(), ASSESSMENT_FRAGMENT);
        speak(context.getString(R.string.launch_assessment), true);
    }

    private void loadFragment(Fragment fragment, String name) {
        //switching fragment
        if (fragment == null) {
            return;
        }

        Log.v(TAG, "replacing fragment with " + name);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment, name)
                        .addToBackStack(name)
                        .commitAllowingStateLoss();
            }
        });

    }

    private Entity getEntity(List<Entity> entities, String type) {
        for (Entity e : entities) {
            if (e.getType().equalsIgnoreCase(type)) {
                return e;
            }
        }

        return null;
    }

    private String getEngine() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SHARE_PREF_ENGINE, "none");
    }

    private String getSpeechLanguage() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SHARE_PREF_LANGUAGE, "en-GB");
    }

    private String getApiKey() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SHARE_PREF_API_KEY, "");
    }

    private String getAccessKey() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SHARE_PREF_ACCESS_KEY, "");
    }

    private String getSecretKey() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SHARE_PREF_SECRET_KEY, "");
    }

    public boolean startRecording() {
        if (isNone()) {
            isListeningToHotword = false;
            return true;
        }

        if (!Util.isMicrophoneGranted(context)) {
            ((MainActivity) context).showSnackbar(context.getString(R.string.record_perm_denied), Snackbar.LENGTH_INDEFINITE);
            return false;
        }

        initialise();

        if (voiceRecognizer == null) {
            return false;
        }

        voiceRecognizer.handleTranscript();
        voiceRecognizer.start();
        isRecording = true;

        if (!isListeningToHotword) {
            startHotwordTimer();
        }

        Log.v(TAG, "start recording");
        return true;
    }

    public void stopRecording() {
        Log.v(TAG, "stop recording");
        if (voiceRecognizer != null)
            voiceRecognizer.stop();
        isRecording = false;
        cancelAllTimers();
    }

    public void cleanup() {
        stopRecording();
        if (voiceRecognizer != null)
            voiceRecognizer.cleanup();
    }

    public void restart() {
        stopRecording();
        startRecording();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public VariableChangeListener getResponseStatusListener() {
        return responseStatusListener;
    }

    public void setResponseStatusListener(VariableChangeListener variableChangeListener) {
        this.responseStatusListener = variableChangeListener;
    }

    public Context getImageViewerContext() {
        return imageViewerContext;
    }

    public void setImageViewerContext(Context imageViewerContext) {
        this.imageViewerContext = imageViewerContext;
    }

    public boolean isListeningToHotword() {
        return isListeningToHotword;
    }

    public void setListeningToHotword() {
        isListeningToHotword = Preferences.getHotword(context);
    }

    public void setListeningToHotword(boolean hotword) {
        isListeningToHotword = hotword;
    }


    public VoiceRecognizer getVoiceRecognizer() {
        return voiceRecognizer;
    }
}
