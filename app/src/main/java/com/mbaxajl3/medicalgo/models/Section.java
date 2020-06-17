package com.mbaxajl3.medicalgo.models;

import java.io.Serializable;
import java.util.List;

public class Section implements Serializable {
    private int id;
    private String title;
    private int algorithmId;
    private int color;
    private List<Integer> steps;

    public Section(int id, int algorithmId, String title, int color) {
        this.id = id;
        this.algorithmId = algorithmId;
        this.title = title;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(int algorithmId) {
        this.algorithmId = algorithmId;
    }

    public List<Integer> getSteps() {
        return steps;
    }

    public void setSteps(List<Integer> steps) {
        this.steps = steps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
