package com.mbaxajl3.medicalgo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Assessment implements Serializable {
    private int id;
    private Algorithm algorithm;
    private int errors = 0;
    private Date date = new Date();

    //true = error
    private List<Pair<Step, Boolean>> stepsTaken = new ArrayList<>();

    public Assessment(int id, Algorithm algorithm) {
        this.id = id;
        this.algorithm = algorithm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public int getErrors() {
        errors = 0;

        if (stepsTaken == null) {
            return errors;
        }

        for (Pair<Step, Boolean> step : stepsTaken) {
            if (step.second) {
                errors++;
            }
        }
        return errors;
    }

    public int getCorrect() {
        int correct = 0;

        if (stepsTaken == null) {
            return correct;
        }

        for (Pair<Step, Boolean> step : stepsTaken) {
            if (!step.second) {
                correct++;
            }
        }
        return correct;
    }

    public Date getDate() {
        return date;
    }

    public List<Pair<Step, Boolean>> getStepsTaken() {
        return stepsTaken;
    }

    public void setStepsTaken(List<Pair<Step, Boolean>> stepsTaken) {
        this.stepsTaken = stepsTaken;
    }

    public List<Step> getSteps() {
        List<Step> steps = new ArrayList<>();

        if (stepsTaken == null) {
            return steps;
        }

        for (Pair<Step, Boolean> step : stepsTaken) {
            steps.add(step.first);
        }

        return steps;
    }
}
