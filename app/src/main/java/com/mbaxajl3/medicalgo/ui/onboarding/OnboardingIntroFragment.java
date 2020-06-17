package com.mbaxajl3.medicalgo.ui.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mbaxajl3.medicalgo.R;

public class OnboardingIntroFragment extends Fragment {

    private Context context;

    public OnboardingIntroFragment() {
        // Required empty public constructor
    }

    public static OnboardingIntroFragment newInstance() {
        return new OnboardingIntroFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_onboarding_intro, container, false);
        context = root.getContext();

        return root;
    }

}
