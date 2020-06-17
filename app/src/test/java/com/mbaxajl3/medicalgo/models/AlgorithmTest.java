package com.mbaxajl3.medicalgo.models;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class AlgorithmTest {
    private Algorithm algorithm;

    @Before
    public void setUp() throws Exception {
        algorithm = new Algorithm(0, 0, "algorithm", "image", null, null);
    }

    @Test
    public void getId() {
        assertThat(algorithm.getId()).isEqualTo(0);
    }

    @Test
    public void setId() {
        algorithm.setId(1);
        assertThat(algorithm.getId()).isEqualTo(1);
    }

    @Test
    public void getCategoryId() {
        assertThat(algorithm.getCategoryId()).isEqualTo(0);
    }

    @Test
    public void setCategoryId() {
        algorithm.setCategoryId(1);
        assertThat(algorithm.getCategoryId()).isEqualTo(1);
    }

    @Test
    public void getName() {
        assertThat(algorithm.getName()).isEqualTo("algorithm");
    }

    @Test
    public void setName() {
        algorithm.setName("a");
        assertThat(algorithm.getName()).isEqualTo("a");
    }

    @Test
    public void getImage() {
        assertThat(algorithm.getImage()).isEqualTo("image");
    }

    @Test
    public void setImage() {
        algorithm.setImage("a");
        assertThat(algorithm.getImage()).isEqualTo("a");
    }

    @Test
    public void getSteps() {
        assertThat(algorithm.getSteps()).isNull();
    }

    @Test
    public void setSteps() {
        List<Step> steps = new ArrayList<>();
        algorithm.setSteps(steps);
        assertThat(algorithm.getSteps()).isNotNull();
    }

    @Test
    public void getSections() {
        assertThat(algorithm.getSections()).isNull();
    }

    @Test
    public void setSections() {
        List<Section> sections = new ArrayList<>();
        algorithm.setSections(sections);
        assertThat(algorithm.getSections()).isNotNull();
    }
}
