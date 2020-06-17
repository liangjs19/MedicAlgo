package com.mbaxajl3.medicalgo.models;

import java.io.Serializable;
import java.util.List;

public class Option implements Serializable {
    private String option;
    private int color;
    private int nextStepId;
    private String intent;
    private List<Entity> entities;

    public Option(int color, String option, int nextStepId, String intent, List<Entity> entities) {
        this.option = option;
        this.nextStepId = nextStepId;
        this.color = color;
        this.intent = intent;
        this.entities = entities;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getNextStepId() {
        return nextStepId;
    }

    public void setNextStepId(int nextStepId) {
        this.nextStepId = nextStepId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
}
