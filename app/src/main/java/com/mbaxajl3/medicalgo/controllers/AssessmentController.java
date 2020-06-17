package com.mbaxajl3.medicalgo.controllers;

import android.content.Context;
import android.util.Log;

import com.mbaxajl3.medicalgo.Preferences;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.Assessment;
import com.mbaxajl3.medicalgo.models.Pair;
import com.mbaxajl3.medicalgo.models.Step;

import java.util.ArrayList;
import java.util.List;

import static com.mbaxajl3.medicalgo.Constants.SHARE_PREF_ASSESSMENT;

// only save assessment when there are steps saved
public class AssessmentController {
    private final static String TAG = "AssessmentController";
    private List<Assessment> assessmentList;
    private Context context;
    private Assessment currentAssessment;

    public AssessmentController(Context context) {
        this.context = context;
        assessmentList = getAssessmentList();
    }

    private int getNewId() {
        if (assessmentList.isEmpty()) {
            return 0;
        }

        return getLastAssessment().getId() + 1;
    }

    public void createAssessment(Algorithm algorithm) {
        assessmentList = getAssessmentList();

        currentAssessment = new Assessment(getNewId(), algorithm);
        assessmentList.add(currentAssessment);
    }

    public Assessment getLastAssessment() {
        int max = -1;

        assessmentList = getAssessmentList();

        if (assessmentList.isEmpty()) {
            return null;
        }

        for (Assessment assessment : assessmentList) {
            if (assessment.getId() > max) {
                max = assessment.getId();
            }
        }

        return assessmentList.get(max);
    }

    public void saveAssessment(List<Pair<Step, Boolean>> steps) {
        Log.v(TAG, "assessment saved");
        currentAssessment.setStepsTaken(steps);
        Preferences.setList(SHARE_PREF_ASSESSMENT, SHARE_PREF_ASSESSMENT, assessmentList, context);
    }

    public List<Assessment> getAssessmentList() {
        List<Assessment> assessmentList = Preferences.getAssessmentsList(SHARE_PREF_ASSESSMENT, context);
        if (assessmentList == null) {
            assessmentList = new ArrayList<>();
        }

        return assessmentList;
    }

    public void setAssessmentList(List<Assessment> assessmentList) {
        this.assessmentList = assessmentList;
    }
}
