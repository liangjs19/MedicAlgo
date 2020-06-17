package com.mbaxajl3.medicalgo.models;

import java.io.Serializable;
import java.util.List;

public class Step implements Serializable {

    private int id;
    private String image;
    private int color;
    private String title;
    private String step;
    private String ssml;
    private List<Option> options;
    private int sectionId;
    private String intent;
    private List<Entity> entities;
    private boolean optional;

    public Step(int id, int sectionId, String image, int color, String title, String step, String ssml, List<Option> options, String intent, List<Entity> entities, boolean optional) {
        this.id = id;
        this.sectionId = sectionId;
        this.image = image;
        this.color = color;
        this.title = title;
        this.step = step;
        this.options = options;
        this.ssml = ssml;
        this.intent = intent;
        this.entities = entities;
        this.optional = optional;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getSsml() {
        return ssml;
    }

    public void setSsml(String ssml) {
        this.ssml = ssml;
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

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}
