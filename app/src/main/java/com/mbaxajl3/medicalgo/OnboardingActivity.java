package com.mbaxajl3.medicalgo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mbaxajl3.medicalgo.ui.onboarding.OnboardingAdapter;
import com.mbaxajl3.medicalgo.ui.onboarding.OnboardingFragment;
import com.mbaxajl3.medicalgo.voice.googlecloud.GoogleCloudRecognizer;

import java.util.Objects;

import static com.mbaxajl3.medicalgo.Constants.AMAZON;
import static com.mbaxajl3.medicalgo.Constants.GOOGLE_CLOUD;
import static com.mbaxajl3.medicalgo.Constants.NONE;

public class OnboardingActivity extends AppCompatActivity {

    private final static String TAG = "OnboardingActivity";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private ViewPager pager;
    private Context mainContext;
    private Button getStartedBtn;
    private LinearLayout pagerIndicator;
    private OnboardingAdapter adapter;
    private OnboardingFragment onboardingFragment;
    private ImageView[] dots;
    private TextInputEditText editTextApiKey;
    private TextInputEditText editTextAccessKey;
    private TextInputEditText editTextSecretKey;
    private LinearLayout getStartedBtnLayout;
    private SpeechService speechService;
    private int dotsCount;
    private String selectedEngine = NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        speechService = Factory.getSpeechService();
        mainContext = speechService.getContext();

        Objects.requireNonNull(getSupportActionBar()).hide();
        pager = findViewById(R.id.pager_introduction);
        pagerIndicator = findViewById(R.id.view_pager_count_dots);
        getStartedBtn = findViewById(R.id.get_started_btn);
        getStartedBtnLayout = findViewById(R.id.get_started_btn_view);

        adapter = new OnboardingAdapter(this, getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.unselected_item_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.selected_item_dot));

                if (position == 0) {
                    hideButton();
                } else {
                    showButton();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        onboardingFragment = (OnboardingFragment) adapter.getItem(1);

        hideButton();
        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextApiKey = onboardingFragment.getEditTextApiKey();
                editTextAccessKey = onboardingFragment.getEditTextAccessKey();
                editTextSecretKey = onboardingFragment.getEditTextSecretKey();

                String apiKey = editTextApiKey.getText() != null ? editTextApiKey.getText().toString() : "";
                String accessKey = editTextAccessKey.getText() != null ? editTextAccessKey.getText().toString() : "";
                String secretKey = editTextSecretKey.getText() != null ? editTextSecretKey.getText().toString() : "";

                int selectedId = onboardingFragment.getSelectedOption();

                switch (selectedId) {
                    case R.id.radio_none:
                        selectedEngine = NONE;
                        Preferences.saveEnginePref(selectedEngine, mainContext);
                        Preferences.saveOnboarding(true, mainContext);
                        finish();
                        return;
                    case R.id.radio_google:
                        selectedEngine = GOOGLE_CLOUD;
                        Preferences.saveApiKey(apiKey, mainContext);
                        requestPermissions();
                        break;
                    case R.id.radio_amazon:
                        selectedEngine = AMAZON;
                        Preferences.saveAccessKey(accessKey, mainContext);
                        Preferences.saveSecretKey(secretKey, mainContext);
                        requestPermissions();
                        break;
                }
                Preferences.saveEnginePref(selectedEngine, mainContext);

                speechService.setResponseStatusListener(variableThatHasChanged -> new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        if (Preferences.getOnboarding(mainContext)) {
                            return;
                        }

                        if ((boolean) variableThatHasChanged[0]) {
                            Preferences.saveOnboarding(true, mainContext);
                            finish();
                        } else {
                            speechService.stopRecording();
                            showSnackbar(getString(R.string.invalid_keys), Snackbar.LENGTH_LONG);
                        }
                    }
                }));
            }
        });
        setUIPageViewController();
    }

    // disable back press
    @Override
    public void onBackPressed() {

    }

    @Override
    public void onStart() {
        super.onStart();
        onboardingFragment.pressCards();
    }

    private void requestPermissions() {
        if (!Util.isMicrophoneGranted(mainContext)) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            if (selectedEngine.equalsIgnoreCase(GOOGLE_CLOUD)) {
                checkGoogleApi();
                return;
            }
            speechService.startRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onboardingFragment.pressCards();
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (selectedEngine.equalsIgnoreCase(GOOGLE_CLOUD)) {
                    checkGoogleApi();
                    return;
                }
                speechService.startRecording();
            } else {
                showSnackbar(mainContext.getString(R.string.record_perm_denied), Snackbar.LENGTH_SHORT);
            }
        }
    }

    private void checkGoogleApi() {
        GoogleCloudRecognizer.checkApi(Preferences.getApiKey(mainContext), new Callback() {
            @Override
            public void onSuccess(Object valid) {
                if ((boolean) valid) {
                    Preferences.saveOnboarding(true, mainContext);
                    finish();
                    speechService.startRecording();
                } else {
                    showSnackbar(getString(R.string.invalid_keys), Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onError(String result) throws Exception {

            }
        });
        return;
    }

    private void setUIPageViewController() {
        dotsCount = adapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.unselected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(6, 0, 6, 0);

            pagerIndicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.selected_item_dot));
    }

    private void disableButton() {
        getStartedBtn.setClickable(false);
        getStartedBtn.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight));
    }

    private void enableButton() {
        getStartedBtn.setClickable(true);
        getStartedBtn.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void hideButton() {
        getStartedBtnLayout.setVisibility(View.INVISIBLE);
    }

    private void showButton() {
        getStartedBtnLayout.setVisibility(View.VISIBLE);
    }

    private void showSnackbar(String s, int length) {
        View contextView = findViewById(R.id.onboarding_layout);

        Snackbar snackbar = Snackbar.make(contextView, s, length);
        snackbar.setAnchorView(getStartedBtnLayout);
        snackbar.show();

    }

}
