package com.mbaxajl3.medicalgo.models;

import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class StepTest {
    private Step step;

    @Before
    public void setup() {
        step = new Step(0, -1, "image", Color.WHITE,
                "title", "step", "ssml", null,
                "intent", null, false);
    }

    @Test
    public void getId() {
        assertThat(step.getId()).isEqualTo(0);
    }

    @Test
    public void setId() {
        step.setId(1);
        assertThat(step.getId()).isEqualTo(1);
    }

    @Test
    public void getImage() {
        assertThat(step.getImage()).isEqualTo("image");
    }

    @Test
    public void setImage() {
        step.setImage("a");
        assertThat(step.getImage()).isEqualTo("a");
    }

    @Test
    public void getColor() {
        assertThat(step.getColor()).isEqualTo(Color.WHITE);
    }

    @Test
    public void setColor() {
        step.setColor(Color.YELLOW);
        assertThat(step.getColor()).isEqualTo(Color.YELLOW);
    }

    @Test
    public void getTitle() {
        assertThat(step.getTitle()).isEqualTo("title");
    }

    @Test
    public void setTitle() {
        step.setTitle("a");
        assertThat(step.getTitle()).isEqualTo("a");
    }

    @Test
    public void getStep() {
        assertThat(step.getStep()).isEqualTo("step");
    }

    @Test
    public void setStep() {
        step.setStep("a");
        assertThat(step.getStep()).isEqualTo("a");
    }

    @Test
    public void getOptions() {
        assertThat(step.getOptions()).isNull();
    }

    @Test
    public void setOptions() {
        List<Option> options = new ArrayList<>();
        step.setOptions(options);
        assertThat(step.getOptions()).isNotNull();
    }

    @Test
    public void getSectionId() {
        assertThat(step.getSectionId()).isEqualTo(-1);
    }

    @Test
    public void setSectionId() {
        step.setSectionId(1);
        assertThat(step.getSectionId()).isEqualTo(1);
    }

    @Test
    public void getSsml() {
        assertThat(step.getSsml()).isEqualTo("ssml");
    }

    @Test
    public void setSsml() {
        step.setSsml("a");
        assertThat(step.getSsml()).isEqualTo("a");
    }

    @Test
    public void getIntent() {
        assertThat(step.getIntent()).isEqualTo("intent");
    }

    @Test
    public void setIntent() {
        step.setIntent("a");
        assertThat(step.getIntent()).isEqualTo("a");
    }

    @Test
    public void getEntities() {
        assertThat(step.getEntities()).isNull();
    }

    @Test
    public void setEntities() {
        List<Entity> entities = new ArrayList<>();
        step.setEntities(entities);
        assertThat(step.getEntities()).isNotNull();
    }

    @Test
    public void isOptional() {
        assertThat(step.isOptional()).isFalse();
    }

    @Test
    public void setOptional() {
        step.setOptional(true);
        assertThat(step.isOptional()).isTrue();
    }
}
