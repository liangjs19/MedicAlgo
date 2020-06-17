package com.mbaxajl3.medicalgo;

public enum Fragments {
    CategoriesFragment(0),
    AlgorithmsFragment(1),
    AlgorithmFragment(2),
    FavouritesFragment(3),
    AssessmentFragment(4),
    AboutFragment(5),
    SearchFragment(6);

    private static Fragments currentFragment;

    Fragments(int value) {
//        currentFragment = value;
    }

    public static Fragments getCurrentFragment() {
        return currentFragment;
    }

    public static void setCurrentFragment(Fragments currentFragment) {
        Fragments.currentFragment = currentFragment;
    }
}
