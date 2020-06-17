package com.mbaxajl3.medicalgo.models;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    private String name;
    private int id;
    private List<AlgorithmMetadata> algorithms;
    private int noOfAlgos = 0;
    private String iconPath;

    public Category(int id, String name, String iconPath, List<AlgorithmMetadata> algorithms) {
        this.id = id;
        this.name = name;
        this.algorithms = algorithms;

        if (algorithms == null) {
            this.noOfAlgos = 0;
        } else {
            this.noOfAlgos = algorithms.size();
        }

        this.iconPath = iconPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoOfAlgos() {
        return noOfAlgos;
    }

    public List<AlgorithmMetadata> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<AlgorithmMetadata> algorithms) {
        this.algorithms = algorithms;
        noOfAlgos = algorithms.size();
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}
