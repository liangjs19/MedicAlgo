package com.mbaxajl3.medicalgo.models;

import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class SectionTest {
    private Section section;

    @Before
    public void setUp() throws Exception {
        section = new Section(0, 0, "title", Color.WHITE);
    }

    @Test
    public void getId() {
        assertThat(section.getId()).isEqualTo(0);
    }

    @Test
    public void setId() {
        section.setId(1);
        assertThat(section.getId()).isEqualTo(1);
    }

    @Test
    public void getAlgorithmId() {
        assertThat(section.getAlgorithmId()).isEqualTo(0);
    }

    @Test
    public void setAlgorithmId() {
        section.setAlgorithmId(1);
        assertThat(section.getAlgorithmId()).isEqualTo(1);
    }

    @Test
    public void getSteps() {
        assertThat(section.getSteps()).isNull();
    }

    @Test
    public void setSteps() {
        List<Integer> steps = new ArrayList<>();
        section.setSteps(steps);
        assertThat(section.getSteps()).isNotNull();
    }

    @Test
    public void getTitle() {
        assertThat(section.getTitle()).isEqualTo("title");
    }

    @Test
    public void setTitle() {
        section.setTitle("a");
        assertThat(section.getTitle()).isEqualTo("a");
    }

    @Test
    public void getColor() {
        assertThat(section.getColor()).isEqualTo(Color.WHITE);
    }

    @Test
    public void setColor() {
        section.setColor(Color.YELLOW);
        assertThat(section.getColor()).isEqualTo(Color.YELLOW);
    }
}
