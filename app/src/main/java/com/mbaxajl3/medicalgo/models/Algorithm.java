package com.mbaxajl3.medicalgo.models;

import java.io.Serializable;
import java.util.List;

public class Algorithm implements Serializable {

    private int id;
    private int categoryId;
    private String name;
    private String image;
    private List<Section> sections;
    private List<Step> steps;

    public Algorithm(int id, int categoryId, String name, String image, List<Step> steps, List<Section> sections) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.image = image;
        this.steps = steps;
        this.sections = sections;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

}
