package com.mbaxajl3.medicalgo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.preference.PreferenceManager;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.mbaxajl3.medicalgo.controllers.AssessmentController;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Pair;
import com.mbaxajl3.medicalgo.ui.algorithm.AlgorithmAdapter;
import com.mbaxajl3.medicalgo.ui.algorithm.AlgorithmFragment;
import com.mbaxajl3.medicalgo.ui.algorithms.AlgorithmsFragment;
import com.mbaxajl3.medicalgo.ui.assessment.AssessmentFragment;
import com.mbaxajl3.medicalgo.ui.categories.CategoriesFragment;
import com.mbaxajl3.medicalgo.ui.favourites.FavouritesFragment;
import com.mbaxajl3.medicalgo.ui.search.SearchFragment;

import java.util.Objects;

import static com.mbaxajl3.medicalgo.Constants.ALGORITHMS_FRAGMENT;
import static com.mbaxajl3.medicalgo.Constants.ALGORITHM_FRAGMENT;
import static com.mbaxajl3.medicalgo.Constants.ASSESSMENT_FRAGMENT;
import static com.mbaxajl3.medicalgo.Constants.CATEGORIES_FRAGMENT;
import static com.mbaxajl3.medicalgo.Constants.FAVOURITES_FRAGMENT;
import static com.mbaxajl3.medicalgo.Constants.SEARCH_FRAGMENT;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final String TAG = "MainActivity";
    private SpeechService speechService;
    private JSONController jsonController;
    private FragmentManager manager;
    private BottomNavigationView bottomNavigationView;
    private MenuItem assessmentMenuItem;
    private AlgorithmAdapter algorithmAdapter;
    private MaterialTextView transcriptTextView;
    private SharedPreferences sharedPreferences;
    private AssessmentController assessmentController;
    private ConstraintLayout mainLayout;
    private Snackbar snackbar;

    FragmentManager.OnBackStackChangedListener backStackListener = new FragmentManager.OnBackStackChangedListener() {
        public void onBackStackChanged() {
            Fragment fragment = getCurrentFragment();

            // hide assessment button, only show on algorithm
            if (assessmentMenuItem != null)
                assessmentMenuItem.setVisible(false);

            // close keyboard when changing from search fragment
            if (!(fragment instanceof SearchFragment)) {
                closeKeyboard();
            }

            // stop assessment when navigating out of algorithm fragment
            if (algorithmAdapter != null) {
                if (algorithmAdapter.inAssessment()) {
                    algorithmAdapter.stopAssessment();
                    showSnackbar(getString(R.string.assessment_stop), Snackbar.LENGTH_LONG);
                }
            }

            // clear backstack so that when user back presses in categories, they would
            // leave the app
            if (fragment instanceof CategoriesFragment) {
                Log.v(TAG, CATEGORIES_FRAGMENT);
                Fragments.setCurrentFragment(Fragments.CategoriesFragment);
                CategoriesFragment currFrag = (CategoriesFragment) fragment;
                clearBackStack(0);
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                setActionBarTitle(getString(R.string.title_categories));
            }
            //
            else if (fragment instanceof AlgorithmsFragment) {
                Log.v(TAG, ALGORITHMS_FRAGMENT);
                AlgorithmsFragment currFrag = (AlgorithmsFragment) fragment;
                setActionBarTitle(currFrag.getActionBarTitle());
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                Fragments.setCurrentFragment(Fragments.AlgorithmsFragment);
            }
            //
            else if (fragment instanceof AlgorithmFragment) {
                Log.v(TAG, ALGORITHM_FRAGMENT);
                AlgorithmFragment currFrag = (AlgorithmFragment) fragment;
                setActionBarTitle(currFrag.getActionBarTitle());
                Fragments.setCurrentFragment(Fragments.AlgorithmFragment);

                // show assessment menu button
                algorithmAdapter = currFrag.getAdapter();
                if (algorithmAdapter.getAssessment() == null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            assessmentMenuItem.setVisible(true);
                        }
                    });
                }

            } else if (fragment instanceof FavouritesFragment) {
                Log.v(TAG, FAVOURITES_FRAGMENT);
                Fragments.setCurrentFragment(Fragments.FavouritesFragment);
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                setActionBarTitle(getString(R.string.title_favourites));
            } else if (fragment instanceof AssessmentFragment) {
                Log.v(TAG, ASSESSMENT_FRAGMENT);
                setActionBarTitle(getString(R.string.title_assessment));
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                Fragments.setCurrentFragment(Fragments.AssessmentFragment);
            } else if (fragment instanceof SearchFragment) {
                Log.v(TAG, SEARCH_FRAGMENT);
                setActionBarTitle(getString(R.string.title_search));
                Fragments.setCurrentFragment(Fragments.SearchFragment);
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
            }

            // to reflect menu button changes
            supportInvalidateOptionsMenu();
        }
    };

    private Fragment getCurrentFragment() {
        return manager.findFragmentById(R.id.nav_host_fragment);
    }

    private void loadFragment(Fragment fragment, String name) {
        //switching fragment
        if (fragment != null) {
            Log.v(TAG, "replacing fragment with " + name);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, name)
                    .addToBackStack(name)
                    .commitAllowingStateLoss();
        }
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(title);
    }

    public void clearBackStack(int index) {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(index);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onBackPressed() {
        // stop speech to text when navigating between fragments
        SpeechService.stopTTS();
        super.onBackPressed();
    }

    private void setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    // when microphone permissions are granted, start voice recognition
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Activities.getCurrentActivity() == Activities.MainActivity)
                    dismissSnackbar();
                speechService.startRecording();
            } else {
                showSnackbar(getString(R.string.record_perm_denied), Snackbar.LENGTH_INDEFINITE);
            }
        }
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
    }

    // when main activity comes into foreground
    @Override
    public void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
        Activities.setCurrentActivity(Activities.MainActivity);
        if (speechService != null)
            speechService.updateTranscriptTextView("");

        dismissSnackbar();
    }

    private void start() {
        if (!isOnboardingComplete()
                || (Preferences.getEngine(this).equalsIgnoreCase("none"))
                || speechService.startRecording()) {
            return;
        }

        requestPermissions();
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
        SpeechService.stopTTS();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        if (speechService != null)
            speechService.cleanup();

        SpeechService.cleanTTS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add your refresh button to res/menu/main.xml
        getMenuInflater().inflate(R.menu.main, menu);

        assessmentMenuItem = menu.findItem(R.id.menu_assessment);
        assessmentMenuItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.menu_assessment:
                if (getCurrentFragment() instanceof AlgorithmFragment) {
                    showSnackbar(getString(R.string.assessment_started), Snackbar.LENGTH_INDEFINITE);
                    algorithmAdapter = ((AlgorithmFragment) getCurrentFragment()).getAdapter();
                    algorithmAdapter.startAssessment();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load contents after onboarding load
        setContentView(R.layout.activity_main);

        setupSharedPreferences();

        //load onboarding first
        Factory.init(this);
        jsonController = Factory.getJSONController();
        speechService = Factory.getSpeechService();
        assessmentController = Factory.getAssessmentController();

        // start onboarding
        if (!isOnboardingComplete()) {
            startActivity(new Intent(this, OnboardingActivity.class));
        }

        //set default fragment to home
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.nav_host_fragment, new CategoriesFragment(), "CategoriesFragment");
        tx.commit();
        Fragments.setCurrentFragment(Fragments.CategoriesFragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.nav_bar_item_categories);

        transcriptTextView = findViewById(R.id.transcript);

        getSupportFragmentManager().addOnBackStackChangedListener(backStackListener);
        // if invalid API key
        speechService.setResponseStatusListener(success -> new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                //TODO check cast
                if ((boolean) success[0]) {
                    return;
                }

                if (((Pair<String, Boolean>) success[0]).second) {

                } else {
                    speechService.stopRecording();
                    showSnackbarWithAction(((Pair<String, Boolean>) success[0]).first, "Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            speechService.restart();
                        }
                    }, Snackbar.LENGTH_INDEFINITE);
                }
            }
        }));

        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));
        manager = getSupportFragmentManager();
        mainLayout = findViewById(R.id.main_layout);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleListener());
    }

    public boolean isOnboardingComplete() {
        return Preferences.getOnboarding(this);
    }

    public void showSnackbar(String s, int length) {
        dismissSnackbar();

        snackbar = Snackbar
                .make(mainLayout, s, length);

        if (transcriptTextView.getVisibility() == View.VISIBLE) {
            snackbar.setAnchorView(transcriptTextView);
        } else {
            snackbar.setAnchorView(bottomNavigationView);
        }

        snackbar.show();
    }

    public void showSnackbarWithAction(String s, String action, View.OnClickListener onClickListener, int length) {
        dismissSnackbar();

        snackbar = Snackbar
                .make(mainLayout, s, length);

        snackbar.setAction(action, onClickListener);

        if (transcriptTextView.getVisibility() == View.VISIBLE) {
            snackbar.setAnchorView(transcriptTextView);
        } else {
            snackbar.setAnchorView(bottomNavigationView);
        }

        snackbar.show();
    }

    public void dismissSnackbar() {
        if (snackbar != null) snackbar.dismiss();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        SpeechService.stopTTS();
        Fragment fragment;
        switch (menuItem.getItemId()) {
            case R.id.nav_bar_item_categories:
                Fragments.setCurrentFragment(Fragments.CategoriesFragment);
                fragment = new CategoriesFragment();
                loadFragment(fragment, CATEGORIES_FRAGMENT);
                break;
            case R.id.nav_bar_item_favourites:
                Fragments.setCurrentFragment(Fragments.FavouritesFragment);
                fragment = new FavouritesFragment();
                loadFragment(fragment, FAVOURITES_FRAGMENT);
                break;
            case R.id.nav_bar_item_assessment:
                Fragments.setCurrentFragment(Fragments.AssessmentFragment);
                fragment = new AssessmentFragment();
                loadFragment(fragment, ASSESSMENT_FRAGMENT);
                break;
            case R.id.nav_bar_item_search:
                Fragments.setCurrentFragment(Fragments.SearchFragment);
                fragment = new SearchFragment();
                loadFragment(fragment, SEARCH_FRAGMENT);
                break;
        }
        return true;
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    // class to handle events when app is suspend/resume
    class AppLifecycleListener implements DefaultLifecycleObserver {
        @Override
        public void onStop(LifecycleOwner owner) {
            Log.d("App", "App in background");
            if (speechService != null) {
                speechService.stopRecording();
            }
            closeKeyboard();
            SpeechService.stopTTS();
        }

        @Override
        public void onStart(LifecycleOwner owner) {
            // dont activate voice recognition when resuming from sleep into settings activity
            if (Activities.getCurrentActivity() != Activities.SettingsActivity) {
                // make user speak hotword upon resume
                speechService.setListeningToHotword();
                start();
            }
            Log.d("App", "App in foreground");
        }
    }
}
