package com.mbaxajl3.medicalgo.models;

import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class OptionTest {
    private Option option;

    @Before
    public void setUp() throws Exception {
        option = new Option(Color.WHITE, "option", 0, "intent", null);
    }

    @Test
    public void getOption() {
        assertThat(option.getOption()).isEqualTo("option");
    }

    @Test
    public void setOption() {
        option.setOption("a");
        assertThat(option.getOption()).isEqualTo("a");
    }

    @Test
    public void getNextStepId() {
        assertThat(option.getNextStepId()).isEqualTo(0);
    }

    @Test
    public void setNextStepId() {
        option.setNextStepId(1);
        assertThat(option.getNextStepId()).isEqualTo(1);
    }

    @Test
    public void getColor() {
        assertThat(option.getColor()).isEqualTo(Color.WHITE);
    }

    @Test
    public void setColor() {
        option.setColor(Color.YELLOW);
        assertThat(option.getColor()).isEqualTo(Color.YELLOW);
    }

    @Test
    public void getIntent() {
        assertThat(option.getIntent()).isEqualTo("intent");
    }

    @Test
    public void setIntent() {
        option.setIntent("a");
        assertThat(option.getIntent()).isEqualTo("a");
    }

    @Test
    public void getEntities() {
        assertThat(option.getEntities()).isEqualTo(null);
    }

    @Test
    public void setEntities() {
        List<Entity> entities = new ArrayList<>();
        option.setEntities(entities);
        assertThat(option.getEntities()).isNotNull();
    }
}
