package com.mbaxajl3.medicalgo.models;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class AssessmentTest {
    private Assessment assessment;
    private Algorithm algorithm;

    @Before
    public void setUp() throws Exception {
        algorithm = new Algorithm(0, 0, null, null, null, null);

        List<Pair<Step, Boolean>> stepsTaken = new ArrayList<>();
        stepsTaken.add(new Pair<>(null, true));
        stepsTaken.add(new Pair<>(null, false));
        assessment = new Assessment(0, algorithm);
        assessment.setStepsTaken(stepsTaken);
    }

    @Test
    public void getId() {
        assertThat(assessment.getId()).isEqualTo(0);
    }

    @Test
    public void setId() {
        assessment.setId(1);
        assertThat(assessment.getId()).isEqualTo(1);
    }

    @Test
    public void getAlgorithm() {
        assertThat(assessment.getAlgorithm()).isEqualTo(algorithm);
    }

    @Test
    public void setAlgorithm() {
        assessment.setAlgorithm(null);
        assertThat(assessment.getAlgorithm()).isNull();
    }

    @Test
    public void getErrors() {
        assertThat(assessment.getErrors()).isEqualTo(1);
    }

    @Test
    public void getCorrect() {
        assertThat(assessment.getCorrect()).isEqualTo(1);
    }

    @Test
    public void getStepsTaken() {
        assertThat(assessment.getStepsTaken()).isNotNull();
    }

    @Test
    public void getSteps() {
        assertThat(assessment.getSteps()).isNotNull();
    }
}
