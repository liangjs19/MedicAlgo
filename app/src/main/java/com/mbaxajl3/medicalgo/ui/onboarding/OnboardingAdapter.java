package com.mbaxajl3.medicalgo.ui.onboarding;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class OnboardingAdapter extends FragmentPagerAdapter {

    private Context context;
    private ArrayList<View> views = new ArrayList<View>();
    private OnboardingFragment onboardingFragment;

    public OnboardingAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return OnboardingIntroFragment.newInstance();
            case 1:
                if (onboardingFragment == null)
                    return onboardingFragment = OnboardingFragment.newInstance();
                else
                    return onboardingFragment;
            default:
                return OnboardingIntroFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
