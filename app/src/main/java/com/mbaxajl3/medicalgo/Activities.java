package com.mbaxajl3.medicalgo;

public enum Activities {
    MainActivity(0),
    ImageViewActivity(1),
    AboutActivity(2),
    SettingsActivity(3),
    OnboardingActivity(4);

    private static Activities currentActivity;

    Activities(int value) {
//        currentFragment = value;
    }

    public static Activities getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activities currentActivity) {
        Activities.currentActivity = currentActivity;
    }
}
