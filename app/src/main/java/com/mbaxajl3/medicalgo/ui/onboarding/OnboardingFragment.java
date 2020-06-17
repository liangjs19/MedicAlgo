package com.mbaxajl3.medicalgo.ui.onboarding;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.mbaxajl3.medicalgo.R;

import java.util.ArrayList;
import java.util.List;

public class OnboardingFragment extends Fragment {

    private final static String TAG = "OnboardingFragment";
    private RadioGroup radioGroup;
    private TextInputEditText editTextApiKey;
    private TextInputEditText editTextAccessKey;
    private TextInputEditText editTextSecretKey;
    private LinearLayout googleFields;
    private LinearLayout amazonFields;
    private List<MaterialCardView> options = new ArrayList<>();
    private boolean isGoogle = false;
    private boolean isAmazon = false;
    private Button getStartedBtn;
    private Context context;
    private int selectedOption;
    private View root;

    public OnboardingFragment() {
        // Required empty public constructor
    }

    public static OnboardingFragment newInstance() {
        return new OnboardingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_onboarding, container, false);
        context = root.getContext();
        getStartedBtn = ((Activity) context).findViewById(R.id.get_started_btn);
        editTextApiKey = root.findViewById(R.id.onboarding_api_key);
        editTextAccessKey = root.findViewById(R.id.onboarding_access_key);
        editTextSecretKey = root.findViewById(R.id.onboarding_secret_key);
        googleFields = root.findViewById(R.id.google_fields_layout);
        amazonFields = root.findViewById(R.id.amazon_fields_layout);

        options.add(root.findViewById(R.id.radio_none));
        options.add(root.findViewById(R.id.radio_google));
        options.add(root.findViewById(R.id.radio_amazon));

        selectedOption = R.id.radio_none;
        pressCards();

        for (MaterialCardView card : options) {
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedOption = card.getId();
                    clearAllFields();
                    hideGoogleFields();
                    hideAmazonFields();
                    disableButton();
                    pressCards();
                    switch (selectedOption) {
                        case R.id.radio_none:
                            Log.v(TAG, "none ");
                            enableButton();
                            break;
                        case R.id.radio_google:
                            Log.v(TAG, "google ");
                            showGoogleFields();
                            break;
                        case R.id.radio_amazon:
                            Log.v(TAG, "amazon ");
                            showAmazonFields();
                            break;
                    }
                }
            });
        }

        editTextApiKey.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Log.v(TAG, "finish typing");
                if (!s.toString().isEmpty() && isGoogle) {
                    enableButton();
                } else if (s.toString().isEmpty() && isGoogle) {
                    disableButton();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextAccessKey.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()
                        && !editTextSecretKey.getText().toString().isEmpty()
                        && isAmazon) {
                    enableButton();
                } else if (s.toString().isEmpty()
                        && !editTextSecretKey.getText().toString().isEmpty()
                        && isAmazon) {
                    disableButton();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextSecretKey.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() &&
                        !editTextAccessKey.getText().toString().isEmpty()
                        && isAmazon) {
                    enableButton();
                } else if (s.toString().isEmpty()
                        && !editTextAccessKey.getText().toString().isEmpty()
                        && isAmazon) {
                    disableButton();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        return root;
    }

    private void hideGoogleFields() {
        googleFields.setVisibility(View.GONE);
        isGoogle = false;
    }

    private void hideAmazonFields() {
        amazonFields.setVisibility(View.GONE);
        isAmazon = false;
    }

    private void clearAllFields() {
        editTextSecretKey.setText("");
        editTextApiKey.setText("");
        editTextAccessKey.setText("");
    }

    private void showGoogleFields() {
        googleFields.setVisibility(View.VISIBLE);
        isGoogle = true;
    }

    private void showAmazonFields() {
        amazonFields.setVisibility(View.VISIBLE);
        isAmazon = true;
    }

    private void disableButton() {
        if (getStartedBtn == null) {
            return;
        }

        getStartedBtn.setClickable(false);
        getStartedBtn.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
    }

    private void enableButton() {
        if (getStartedBtn == null) {
            return;
        }

        getStartedBtn.setClickable(true);
        getStartedBtn.setTextColor(ContextCompat.getColor(context, R.color.white));
    }

    public void pressCards() {
        for (MaterialCardView cardView : options) {
            if (selectedOption == cardView.getId())
                pressCard(cardView, false);
            else
                pressCard(cardView, true);
        }
    }

    private void pressCard(View v, boolean pressed) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                v.setPressed(pressed);
            }
        };
        new Handler().post(run);
    }

    public TextInputEditText getEditTextApiKey() {
        return editTextApiKey;
    }

    public TextInputEditText getEditTextAccessKey() {
        return editTextAccessKey;
    }

    public TextInputEditText getEditTextSecretKey() {
        return editTextSecretKey;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }
}
